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
import com.example.electrocarmanager.Entity.Notification;
import com.example.electrocarmanager.Entity.NotificationGroup;
import com.example.electrocarmanager.Location.CarLocation.GetAddress;
import com.example.electrocarmanager.MainActivity;
import com.example.electrocarmanager.Notification.NotificationGroupRecycleAdapter;
import com.example.electrocarmanager.Notification.NotificationRecyclerViewAdapter;
import com.example.electrocarmanager.R;
import com.example.electrocarmanager.Utils.DateUtils;
import com.google.gson.Gson;
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
import java.util.List;

/**
 * @author bbg
 * 在开启位移提醒时，用于显示移动结果的fragment
 */
public class TrackFragment extends Fragment  implements View.OnClickListener{

    Handler handler;

    Gson gson=new Gson();

    List<NotificationGroup> data;//历史记录
    public List<LatLng> points=new ArrayList<>();//保留实时移动信息的点集数据
    public Notification notification;//实时的移动信息

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


    final String OLD_MOVING_URL="";
    final String STORE_OLD_MOVING_URL="";

    NotificationRecyclerViewAdapter.ItemClickListener itemClickListener;

    NotificationGroupRecycleAdapter adapter;

    String lastDate=null;

    //当前车辆是否处于位移状态
    boolean isAlert=false;
    //累计移动总距离
    double totalDistance=0;

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
//        getOldMsgAndUpdate();
    }

    void initUI(View view)
    {

        recyclerView=view.findViewById(R.id.notifications);
        alert=view.findViewById(R.id.alert);
        notMove=view.findViewById(R.id.not_move);
        move=view.findViewById(R.id.move);
        recyclerView.setPadding(10,alert.getMeasuredHeight()+notMove.getMeasuredHeight()+330,0,0);
        from=move.findViewById(R.id.from);
        time=move.findViewById(R.id.time);
        to=move.findViewById(R.id.to);
        last=move.findViewById(R.id.last);
        distance =move.findViewById(R.id.distance);
    }

    public void getNowMsg(String json)
    {
        if(!MainActivity.realAlertOn)//如果没开启实时位移提醒，就不用更新位移消息
        {
            return;
        }
        MovingDto movingDto = gson.fromJson(json, MovingDto.class);
        if(movingDto.alert&&!isAlert)//当前正在移动，且第一次检测到移动
        {
            //置标志位为真
            isAlert=true;

            //更新栏目
            notMove.setVisibility(View.INVISIBLE);
            move.setVisibility(View.VISIBLE);
            move.setOnClickListener(this);
            recyclerView.setPadding(10,alert.getMeasuredHeight()+move.getMeasuredHeight()+330,0,0);

            //填入初始数据
            GetAddress getAddress=new GetAddress(movingDto.fromLatitude,movingDto.fromLongitude);
            Thread thread=new Thread(getAddress);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            from.setText("移动起始位置:"+getAddress.getAddress());
            time.setText("移动起始时间:"+DateUtils.toHourAndMinute(movingDto.beginTime));
            to.setText("当前车辆位置:"+getAddress.getAddress());
            last.setText("移动累计持续时间:"+"00:00:00");
            distance.setText("移动累计总距离:"+0.0);

            //保存数据
            notification.from=getAddress.getAddress();
            notification.time=DateUtils.toHourAndMinute(movingDto.beginTime);
            notification.to=getAddress.getAddress();
            notification.last="00:00:00";
            notification.distance=0.0;
            points.add(new LatLng(movingDto.fromLatitude,movingDto.fromLongitude));

            //只有在点开实时移动轨迹图的时候才通知主程序更新UI，否则只在后台存储数据
            if(MainActivity.realPointOn)
            {
                Message msg=new Message();
                msg.what=3;
                handler.sendMessage(msg);
            }

        }
        else if(movingDto.alert)//当前正在移动，且之前也在移动
        {
            //保存数据
            points.add(new LatLng(movingDto.fromLatitude,movingDto.fromLongitude));
            totalDistance += DistanceUtil.getDistance(points.get(points.size()-1),points.get(points.size()-2));

            //更新数据
            GetAddress getAddress=new GetAddress(movingDto.fromLatitude,movingDto.fromLongitude);
            Thread thread=new Thread(getAddress);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            to.setText("当前车辆位置:"+getAddress.getAddress());
            last.setText("移动累计持续时间:"+DateUtils.convertMillis((movingDto.endTime.getTime()-movingDto.beginTime.getTime())/1000));
            distance.setText("移动累计总距离:"+totalDistance);

            notification.to=getAddress.getAddress();
            notification.last=DateUtils.convertMillis((movingDto.endTime.getTime()-movingDto.beginTime.getTime())/1000);
            notification.distance=totalDistance;

            //只有在点开实时移动轨迹图的时候才通知主程序更新UI，否则只在后台存储数据
            if(MainActivity.realPointOn)
            {
                Message msg=new Message();
                msg.what=3;
                handler.sendMessage(msg);
            }
        }
        else if(isAlert)//车辆的此次移动已经停止
        {
            //保存数据
            postOldMsg(movingDto.id,totalDistance);

            //清空数据避免影响到下一次
            isAlert=false;
            totalDistance=0;
            points.clear();

            //TODO:直接在本地展示历史数据

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
        List<Notification> notifications=new ArrayList<>();
        NotificationGroup notificationGroup=new NotificationGroup();
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
        for(int i=0;i<list.size();i++)
        {
            String date= DateUtils.toYearAndMonthAndDate(list.get(i).beginTime);
            Notification notification=new Notification();
            notification.time=DateUtils.toHourAndMinute(list.get(i).beginTime)+"-"+DateUtils.toHourAndMinute(list.get(i).endTime);
            try {
                threadsFrom.get(i).join();
                notification.from=getsFrom.get(i).getAddress();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            try {
                threadsTo.get(i).join();
                notification.to=getsTo.get(i).getAddress();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            notification.distance=list.get(i).distance;
            notification.id=list.get(i).id;
            notification.last=DateUtils.convertMillis((list.get(i).beginTime.getTime()-list.get(i).endTime.getTime())/1000);
            if(lastDate==null)
            {
                lastDate=date;
                notificationGroup.date=date;
                notifications.add(notification);
            }
            else if(lastDate==date)
            {
                notifications.add(notification);
            }
            else
            {
                notificationGroup.notifications=notifications;
                data.add(notificationGroup);
                notificationGroup.date=date;
                notifications.clear();
                notifications.add(notification);
                lastDate=date;
            }
        }
        notificationGroup.notifications=notifications;
        data.add(notificationGroup);
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
        Notification notification1 = new Notification(1L,"16:22:23-16:34:57", "武汉大学", "华中科技大学", 1200, "00:12:34");

        Notification notification2 = new Notification(2L,"16:27:02-16:37:26", "街道口", "广埠屯", 66, "00:10:24");

        NotificationGroup notificationGroup = new NotificationGroup();
        notificationGroup.notifications.add(notification1);
        notificationGroup.notifications.add(notification2);
        notificationGroup.date = "11月6日";

        data.add(notificationGroup);

        NotificationGroup notificationGroup1 = new NotificationGroup();
        notificationGroup1.notifications.add(notification1);
        notificationGroup1.notifications.add(notification2);
        notificationGroup1.date = "11月7日";

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
