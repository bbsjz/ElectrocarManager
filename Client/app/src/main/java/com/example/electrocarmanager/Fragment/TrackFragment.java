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
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.electrocarmanager.Entity.Move;
import com.example.electrocarmanager.Entity.MovingDto;
import com.example.electrocarmanager.Entity.Notify;
import com.example.electrocarmanager.Entity.NotifyGroup;
import com.example.electrocarmanager.Location.CarLocation.GetAddress;
import com.example.electrocarmanager.MainActivity;
import com.example.electrocarmanager.Notification.NotificationGroupRecycleAdapter;
import com.example.electrocarmanager.Notification.NotificationRecyclerViewAdapter;
import com.example.electrocarmanager.R;
import com.example.electrocarmanager.Utils.DateAdapter;
import com.example.electrocarmanager.Utils.DateUtils;
import com.example.electrocarmanager.Utils.PxUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author bbg
 * 在开启位移提醒时，用于显示移动结果的fragment
 */
public class TrackFragment extends Fragment  implements View.OnClickListener{

    Handler handler;

    Gson gson=new GsonBuilder().registerTypeAdapter(Date.class, new DateAdapter()).create();

    List<NotifyGroup> data=new ArrayList<>();//历史记录
    public List<LatLng> points=new ArrayList<>();//保留实时移动信息的点集数据
    public Notify notify=new Notify();//实时的移动信息

    RealTimeClickListener listener;

    //UI
    TextView alert;
    View notMove;
    View move;
    RecyclerView recyclerView;
    TextView from;
    TextView time;
    TextView to;
    TextView last;
    TextView distance;

    String fromText;
    String timeText;
    String toText;
    String lastText;
    double distanceText;


    final String OLD_MOVING_URL="";
    final String STORE_OLD_MOVING_URL="";

    NotificationRecyclerViewAdapter.ItemClickListener itemClickListener;
    NotificationGroupRecycleAdapter adapter;

    //当前车辆是否处于位移状态
    boolean isMoving =false;

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
        data=new ArrayList<>();
        initUI(view);
        test();
        updateMovingMsg();
        getOldMsgAndUpdate();
    }

    void initUI(View view)
    {

        recyclerView=view.findViewById(R.id.notifications);
        alert=view.findViewById(R.id.alert);
        notMove=view.findViewById(R.id.not_move);
        move=view.findViewById(R.id.move);
        from=move.findViewById(R.id.from);
        time=move.findViewById(R.id.time);
        to=move.findViewById(R.id.to);
        last=move.findViewById(R.id.last);
        distance =move.findViewById(R.id.distance);
        updateUpperUI();
    }

    void updateUpperUI()
    {
        if(move==null)
        {
            return;
        }
        if(MainActivity.realAlertOn)
        {
            alert.setText("位置移动提醒已开启");
            if(isMoving)//正在移动时，应该显示移动信息
            {
                notMove.setVisibility(View.INVISIBLE);
                move.setVisibility(View.VISIBLE);
                recyclerView.setPadding(PxUtils.dp2px(getContext(),5),PxUtils.dp2px(getContext(),250),0,0);
                from.setText("移动起始位置:"+fromText);
                time.setText("移动起始时间:"+timeText);
                to.setText("当前车辆位置:"+toText);
                last.setText("移动累计持续时间:"+lastText);
                distance.setText("移动累计总距离:"+distanceText);
            }
            else//没有移动时，显示没有移动
            {
                notMove.setVisibility(View.VISIBLE);
                move.setVisibility(View.INVISIBLE);
                recyclerView.setPadding(PxUtils.dp2px(getContext(),5),PxUtils.dp2px(getContext(),120),0,0);
            }
        }
        else
        {
            alert.setText("位置移动提醒未开启");
            notMove.setVisibility(View.INVISIBLE);
            move.setVisibility(View.INVISIBLE);
            recyclerView.setPadding(PxUtils.dp2px(getContext(),5),PxUtils.dp2px(getContext(),50),0,0);
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

                //更新栏目
                updateUpperUI();

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
                points.add(new LatLng(movingDto.fromLatitude,movingDto.fromLongitude));
                fromText=getAddress.getAddress();
                timeText=DateUtils.toHourAndMinute(movingDto.beginTime);
                toText=getAddress.getAddress();
                lastText="00:00:00";
                distanceText=0.0;

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
                points.add(new LatLng(movingDto.toLatitude,movingDto.toLongitude));
                toText=getAddress.getAddress();
                lastText=DateUtils.convertMillis((movingDto.endTime.getTime()-movingDto.beginTime.getTime())/1000);
                distanceText=movingDto.distance;
                notify.to=getAddress.getAddress();
                notify.last=DateUtils.convertMillis((movingDto.endTime.getTime()-movingDto.beginTime.getTime())/1000);
                notify.distance=movingDto.distance;

                updateUpperUI();

                //只有在点开实时移动轨迹图的时候才通知主程序更新UI，否则只在后台存储数据
                if(MainActivity.realPointOn)
                {
                    Message msg=new Message();
                    msg.what=3;
                    handler.sendMessage(msg);
                }
            }
            else if(isMoving)//车辆的此次移动已经停止
            {
                //清空数据避免影响到下一次
                isMoving =false;
                points.clear();
                addToLocal(movingDto);
                updateMovingMsg();
                updateUpperUI();
            }
        }
       catch(Exception ex)
       {
           return;
       }

    }

    //子线程请求之前保存的移动数据
    void getOldMsgAndUpdate()
    {
        new Thread(){
            @Override
            public void run()
            {
                try {
                    URL url = new URL(OLD_MOVING_URL);
                    HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    InputStream in=httpURLConnection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder builder=new StringBuilder();
                    String oneLine;
                    while((oneLine=reader.readLine())!=null)
                    {
                        builder.append(oneLine);
                    }
                    getData(builder.toString());
                    updateMovingMsg();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //子线程发post请求保存一条结束的移动信息
    void postOldMsg(Long id,double distance)
    {
        new Thread() {
            @Override
            public void run() {
                try{
                    URL url = new URL(STORE_OLD_MOVING_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Charset", "UTF-8");
                    // 设置文件类型:
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    String json="{"+"\"id\":"+id+","+"\"distance\":"+distance+"}";
                    // 往服务器里面发送数据
                    byte[] data = json.getBytes(StandardCharsets.UTF_8);
                    OutputStream os = conn.getOutputStream();
                    os.write(data, 0, data.length);
                    int tmp=conn.getResponseCode();
                    os.close();
                }
                catch(ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 将获得的json格式的字符串转为list的数据格式
     */
    void getData(String rowData)
    {
        List<Move> list=gson.fromJson(rowData,new TypeToken<List<Move>>(){}.getType());
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
            notification.last = DateUtils.convertMillis((list.get(i).beginTime.getTime() - list.get(i).endTime.getTime()) / 1000);
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
    }


    void addToLocal(MovingDto move)
    {
        //如果此时，列表中已经有日期并且和当天日期一样，那么插入，并且因为是最近的一条数据所以要插入在最前端
        if(data.size()!=0&&data.get(0).date.equals(DateUtils.toYearAndMonthAndDate(move.beginTime)))
        {
            List<Notify> list=data.get(0).notifications;
            list.add(0,notify);
            data.get(0).notifications=list;
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
            data.add(0,group);
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
        updateMovingMsg();
    }

    //获取list格式的数据后更新
    void updateMovingMsg()
    {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        adapter=new NotificationGroupRecycleAdapter(data,itemClickListener,getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    void test() {
        data = new ArrayList<>();
        Notify notification1 = new Notify(1L,"最近的时间", "武汉大学", "华中科技大学", 1200, "00:12:34");

        Notify notification2 = new Notify(2L,"倒数第二近的时间", "街道口", "广埠屯", 66, "00:10:24");

        NotifyGroup notificationGroup = new NotifyGroup();
        notificationGroup.notifications.add(notification1);
        notificationGroup.notifications.add(notification2);
        notificationGroup.date = "最近的一天";

        data.add(notificationGroup);

        NotifyGroup notificationGroup1 = new NotifyGroup();
        notificationGroup1.notifications.add(notification1);
        notificationGroup1.notifications.add(notification2);
        notificationGroup1.date = "倒数第二近的一天";

        data.add(notificationGroup1);
    }

    //当实时移动信息被点击后执行
    @Override
    public void onClick(View v) {
        if(listener!=null)
        {
            listener.onRealTimeClick();
        }
    }

    public interface RealTimeClickListener
    {
        void onRealTimeClick();
    }
}
