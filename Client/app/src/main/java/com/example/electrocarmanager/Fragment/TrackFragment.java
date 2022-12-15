package com.example.electrocarmanager.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.model.LatLng;
import com.example.electrocarmanager.Entity.Move;
import com.example.electrocarmanager.Entity.MovingDto;
import com.example.electrocarmanager.Entity.Notify;
import com.example.electrocarmanager.Entity.NotifyGroup;
import com.example.electrocarmanager.NetWork.OldMoveMsgGet;
import com.example.electrocarmanager.Service.CarLocationService.GetAddress;
import com.example.electrocarmanager.MainActivity;
import com.example.electrocarmanager.Notification.NotificationGroupRecycleAdapter;
import com.example.electrocarmanager.Notification.NotificationRecyclerViewAdapter;
import com.example.electrocarmanager.R;
import com.example.electrocarmanager.Utils.BDLocUtil;
import com.example.electrocarmanager.Utils.DataBaseDateAdapter;
import com.example.electrocarmanager.Utils.DateAdapter;
import com.example.electrocarmanager.Utils.DateUtils;
import com.example.electrocarmanager.Utils.PxUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author bbg
 * 在开启位移提醒时，用于显示移动结果的fragment
 */
public class TrackFragment extends Fragment  {

    Handler handler;

    Gson gson=new GsonBuilder().registerTypeAdapter(Date.class, new DateAdapter()).create();
    Gson dbGson=new GsonBuilder().registerTypeAdapter(Date.class, new DataBaseDateAdapter()).create();

    List<NotifyGroup> data=new ArrayList<>();//历史记录
    int page=0;//当前在第几页记录
    public List<LatLng> points=new ArrayList<>();//保留实时移动信息的点集数据
    public Notify notify=new Notify();//实时的移动信息
    NotifyGroup group=new NotifyGroup();//实时的移动信息组


    //UI
    TextView alert;
    RecyclerView recyclerView;
    int lastVisibleItem=0;
    LinearLayoutManager linearLayoutManager;

    NotificationRecyclerViewAdapter.ItemClickListener itemClickListener;
    NotificationGroupRecycleAdapter adapter;

    //当前车辆是否处于位移状态
    boolean isMoving =false;
    boolean ifHasNewOldData=false;//是否需要更新旧数据
    final String moving="正在发生的位移";
    final String notMoving="当前没有正在发生的位移";
    MovingDto local;//上一次的移动状态

    public TrackFragment(NotificationRecyclerViewAdapter.ItemClickListener itemClickListener,Handler handler)
    {
        this.itemClickListener=itemClickListener;
        this.handler=handler;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater,
                             @Nullable ViewGroup viewGroup, @Nullable Bundle savedBundle)
    {
        super.onCreateView(layoutInflater,viewGroup,savedBundle);
        return layoutInflater.inflate(R.layout.track_fragment,viewGroup,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedBundle)
    {
        if(data.size()<5)
        {
            getOldMsgAndParse();
        }
        initUI(view);
    }

    void initUI(View view)
    {

        recyclerView=view.findViewById(R.id.notifications);
        alert=view.findViewById(R.id.alert);
        linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new NotificationGroupRecycleAdapter(data,itemClickListener,getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==RecyclerView.SCROLL_STATE_IDLE)//滑动停止
                {
                    if(lastVisibleItem+1==adapter.getItemCount())//如果滑动停止的时候到底，即可以看到最后一个元素的时候
                    {
                        getOldMsgAndParse();

                        if(ifHasNewOldData)//如果查到了新的数据就更新
                        {
                            adapter.setData(data);
                            ifHasNewOldData=false;
                        }
                        else//如果没有查到就显示没有更多数据
                        {
                            adapter.setIfLoading(2);
                        }
                        adapter.updateState();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 在滑动完成后，拿到最后一个可见的item的位置
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
        updateUpperUI();
    }

    void updateUpperUI()
    {
        if(adapter==null)
        {
            return;
        }
        if(MainActivity.realAlertOn)
        {
            alert.setText("位置移动提醒已开启");
            //一旦开启提醒，并且data没有实时信息那一栏，需要手动添加
            if(data.size()==0||(!data.get(0).date.equals(notMoving)&&!data.get(0).date.equals(moving)))
            {
                group.date=notMoving;
                List<Notify> notifies=new ArrayList<>();
                group.notifications=notifies;
                data.add(0,group);
            }
            adapter.setData(data);
            adapter.updateState();
        }
        else
        {
            alert.setText("位置移动提醒未开启");
            if(data.get(0).date.equals(notMoving)||data.get(0).date.equals(moving))
            {
                data.remove(0);
                adapter.setData(data);
                adapter.updateState();
            }
        }
    }

    public void getNowMsg(String json)
    {
        if(!MainActivity.realAlertOn)//如果没开启实时位移提醒，就不用更新位移消息
        {
            return;
        }
        try{
            MovingDto movingDto = gson.fromJson(json, MovingDto.class);
            if(movingDto.alert&&!isMoving)//当前正在移动，且第一次检测到移动
            {
                //置标志位为真
                isMoving =true;

                //填入初始数据
                GetAddress getAddress=new GetAddress(movingDto.fromLatitude,movingDto.fromLongitude);
                Thread thread=new Thread(getAddress);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //保存数据
                notify.from=getAddress.getAddress();
                notify.time=DateUtils.toHourAndMinute(movingDto.beginTime);
                notify.to=getAddress.getAddress();
                notify.last="00:00:00";
                notify.distance=0.0;
                points.add(BDLocUtil.GPStoBD09LL(new LatLng(movingDto.fromLatitude,movingDto.fromLongitude)));

                group.date=moving;
                List<Notify> notifies=new ArrayList<>();
                notifies.add(notify);
                group.notifications=notifies;

                //如果第一条不是group，则加入
                if(data.size()==0||(!data.get(0).date.equals(moving)&&!data.get(0).equals(notMoving)))
                {
                    data.add(0,group);
                }

                updateUpperUI();

                //只有在点开实时移动轨迹图的时候才通知主程序更新UI，否则只在后台存储数据
                if(MainActivity.realPointOn)
                {
                    Message msg=new Message();
                    msg.what=3;
                    handler.sendMessage(msg);
                }

                //发出通知，说明车辆位置发生了移动
                Message msg=new Message();
                msg.what=5;
                handler.sendMessage(msg);
            }
            else if(movingDto.alert)//当前正在移动，且之前也在移动
            {
                //更新数据
                GetAddress getAddress=new GetAddress(movingDto.toLatitude,movingDto.toLongitude);
                Thread thread=new Thread(getAddress);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //保存数据
                points.add(BDLocUtil.GPStoBD09LL(new LatLng(movingDto.toLatitude,movingDto.toLongitude)));
                notify.to=getAddress.getAddress();
                notify.last=DateUtils.convertMillis((movingDto.endTime.getTime()-movingDto.beginTime.getTime())/1000);
                notify.distance=movingDto.distance;
                if(group.notifications.size()!=0)
                {
                    group.notifications.remove(0);
                }
                group.notifications.add(notify);
                group.date=moving;

                if(data.size()==0||(!data.get(0).date.equals(moving)&&!data.get(0).date.equals(notMoving)))//第一条不是移动信息，则添加
                {
                    data.add(0,group);
                }

                //更新UI
                updateUpperUI();

                //只有在点开实时移动轨迹图的时候才通知主程序更新UI，否则只在后台存储数据
                if(MainActivity.realPointOn)
                {
                    Message msg=new Message();
                    msg.what=3;
                    handler.sendMessage(msg);
                }
                local=movingDto;
            }
            else if(isMoving)//如果此前还处于移动状态，但是这次已经显示没有移动了，则从此时开始车辆的此次移动已经停止
            {
                //清空数据避免影响到下一次
                isMoving =false;
                points.clear();

                group.date=notMoving;
                group.notifications=new ArrayList<>();

                if(data.size()==0||(!data.get(0).date.equals(moving)&&!data.get(0).date.equals(notMoving)))//第一条不是正在发生的移动就说明被删了
                {
                    data.add(group);
                }

                //更新UI
                updateUpperUI();

                addToLocal(local);
            }
        }
       catch(Exception ex)
       {
           String e=ex.getMessage();
       }

    }

    //子线程请求之前保存的移动数据
    void getOldMsgAndParse()
    {
        OldMoveMsgGet get=new OldMoveMsgGet(page);
        Thread getOldMoveData=new Thread(get);
        getOldMoveData.start();
        try {
            getOldMoveData.join();
            parseOldData(get.getJson());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将获得的json格式的字符串转为list的数据格式
     */
    void parseOldData(String rowData)
    {
        try {
            if(rowData==null)
            {
                return;
            }
            JSONObject jsonObject=new JSONObject(rowData);
            int totalPage=jsonObject.optInt("totalPages");
            if(totalPage==0||totalPage<=page)
            {
                return;
            }
            JSONArray array=jsonObject.getJSONArray("content");
            String json=array.toString();
            List<Move> list=dbGson.fromJson(json,new TypeToken<List<Move>>(){}.getType());
            List<Thread> threadsFrom=new ArrayList<>();
            List<Thread> threadsTo=new ArrayList<>();
            List<GetAddress> getsFrom=new ArrayList<>();
            List<GetAddress> getsTo=new ArrayList<>();
            for(Move move:list)
            {
                //将出发位置的经纬度转换成实际位置
                GetAddress getFrom=new GetAddress(move.fromLatitude,move.fromLongitude);
                Thread threadFrom=new Thread(getFrom);
                threadFrom.start();
                threadsFrom.add(threadFrom);
                getsFrom.add(getFrom);

                //将结束位置的经纬度转换成实际坐标
                GetAddress getTo=new GetAddress(move.toLatitude,move.toLongitude);
                Thread threadTo=new Thread(getTo);
                threadTo.start();
                threadsTo.add(threadTo);
                getsTo.add(getTo);
            }
            for(int i=0;i<list.size();i++) {
                String date = DateUtils.toYearAndMonthAndDate(list.get(i).beginTime);
                Notify notification = new Notify();
                notification.time = DateUtils.toHourAndMinute(list.get(i).beginTime) + "-" + DateUtils.toHourAndMinute(list.get(i).endTime);
                try {
                    threadsFrom.get(i).join();
                    notification.from = getsFrom.get(i).getAddress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                try {
                    threadsTo.get(i).join();
                    notification.to = getsTo.get(i).getAddress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                notification.distance = list.get(i).distance;
                notification.id = list.get(i).id;
                notification.last = DateUtils.convertMillis((list.get(i).endTime.getTime() - list.get(i).beginTime.getTime()) / 1000);
                if (data.size() != 0 && data.get(data.size() - 1).date.equals(date)) {
                    data.get(data.size() - 1).notifications.add(notification);
                } else {
                    NotifyGroup group = new NotifyGroup();
                    group.date = date;
                    List<Notify> li = new ArrayList<>();
                    li.add(notification);
                    group.notifications = li;
                    data.add(group);
                }
            }
            ifHasNewOldData=true;//上面的一串更新完毕，那么会出现新的数据，需要进行更新
            page++;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    void addToLocal(MovingDto move)
    {
        notify.id=move.id;
        notify.time=DateUtils.toHourAndMinute(move.beginTime) + "-" + DateUtils.toHourAndMinute(move.endTime);
        //如果此时，列表中已经有日期并且和当天日期一样，那么插入，并且因为是最近的一条数据所以要插入在最前端
        if(data.size()!=0&&data.get(1).date.equals(DateUtils.toYearAndMonthAndDate(move.beginTime)))
        {
            List<Notify> list=data.get(1).notifications;
            list.add(0,notify);
            data.get(1).notifications=list;
        }
        //如果当前列表中没有数据，或者说当前列表最前端的日期和这条数据的日期不相符合，
        //那么此时要新建一个日期并且插入，且插入在最前面
        else
        {
            NotifyGroup group=new NotifyGroup();
            group.date=DateUtils.toYearAndMonthAndDate(move.beginTime);
            List<Notify> list=new ArrayList<>();
            list.add(notify);
            group.notifications=list;
            data.add(1,group);
        }
        int total=0;
        for(NotifyGroup notifyGroup:data)
        {
            total+=notifyGroup.notifications.size();
        }
        //如果本地添加后列表的元素刚好是5的倍数+1，则弹出最后一个元素防止下滑再次向服务器请求数据后得到的元素重复
        if(total%5==1)
        {
            data.get(data.size()-1).notifications.remove(data.get(data.size()-1).notifications.size()-1);
        }
        adapter.setData(data);
        adapter.updateState();
    }

}
