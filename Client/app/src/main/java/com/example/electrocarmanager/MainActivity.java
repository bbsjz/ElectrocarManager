package com.example.electrocarmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.example.electrocarmanager.Entity.Notify;
import com.example.electrocarmanager.Fragment.LocationFragment;
import com.example.electrocarmanager.Fragment.PointFragment;
import com.example.electrocarmanager.Fragment.RealTimePointFragment;
import com.example.electrocarmanager.Fragment.SwitchFragment;
import com.example.electrocarmanager.Fragment.TrackFragment;
import com.example.electrocarmanager.Service.CarLocationService.MyException;
import com.example.electrocarmanager.Service.CarLocationService.WebsocketClient;
import com.example.electrocarmanager.Service.MyLocationService.MyLocationListener;
import com.example.electrocarmanager.Service.MyLocationService.MyLocationService;
import com.example.electrocarmanager.Notification.NotificationRecyclerViewAdapter;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity implements NotificationRecyclerViewAdapter.ItemClickListener,
        PointFragment.ArrowClickListener, RealTimePointFragment.RealArrowClickListener,TrackFragment.RealTimeClickListener{

    //三个fragment
    SwitchFragment switchFragment;
    LocationFragment locationFragment;
    TrackFragment trackFragment;
    RealTimePointFragment realTimePointFragment;
    FragmentManager fragmentManager;

    NotificationManager notificationManager;

    //底部
    RadioButton open;
    RadioButton location;
    RadioButton track;
    LinearLayout point;//一个点轨迹栏
    RadioGroup tabs;//一个三个按钮的按钮集合
    TextView time;
    TextView from;
    TextView to;
    TextView last;
    TextView distance;

    public final String WS_SERVER_ADDRESS = "ws://jp.safengine.xyz:8081/websocket";

    //定位服务
    MyLocationService myLocationService;
    MyLocationListener myLocationListener;
    WebsocketClient websocketClient;
    Timer timer;//用于执行重连服务的定时器
    int delayTime=1000;//经过多少时间重新连接WS，初始值为1秒钟，自动重连失败则累加


    public static Handler handler;
    public static boolean ifConnected;//是否已经连接
    public static String token;//token，全局共享
    public static boolean realPointOn=false;//是否正处在实时轨迹界面
    public static boolean realAlertOn=false;//当前是否开启实时位移提醒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token=getIntent().getStringExtra("token");
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(myLocationService !=null&& myLocationService.isStart())
        {
            myLocationService.stop();
        }
        if(myLocationService !=null)
        {
            myLocationService.unregisterListener(myLocationListener);
        }
        if(websocketClient!=null&&websocketClient.isOpen())
        {
            websocketClient.close();
        }
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        String requestCode=intent.getStringExtra("requestCode");
        if(requestCode==null)
        {
            return;
        }
        else if(requestCode.equals("moving"))
        {
            fragmentManager.beginTransaction().replace(R.id.fragment,trackFragment).commit();
        }
    }

    void init()
    {
        if(!checkPermission())
        {
            return;
        }
        initHandler();
        initFragment();
        initButton();
        initMyLocationService();
        initCarLocationService();
    }

    //设置权限
    boolean checkPermission()
    {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)!=
                    PackageManager.PERMISSION_GRANTED)
        {
            String[] request=new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(MainActivity.this,request,1);
            return false;
        }
        return true;
    }

    //获取权限之后的回调函数
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                //申请权限成功
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    init();
                }
                else//申请权限失败
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setTitle("申请权限失败")
                            .setMessage("权限申请失败，该APP无法使用，若要使用，请同意相关权限！")//内容
                            .setIcon(R.drawable.warning)//图标
                            .setOnDismissListener(v->{
                                killAppProcess();
                            })
                            .create();
                    alertDialog.show();
                }
                return;
            }
        }
    }

    //初始化message用于接受处理各种消息
    void initHandler()
    {
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                switch(msg.what)
                {
                    //1表示自我定位服务返回了结果
                    case 1:
                    {
                        locationFragment.updateMyLocation((BDLocation) msg.obj);
                        break;
                    }
                    //2表示车辆定位服务返回了结果
                    case 2:
                    {
                        locationFragment.parseJsonAndUpdateCarLocation((String) msg.obj);
                        trackFragment.getNowMsg((String) msg.obj);
                        break;
                    }
                    //3表示实时位移信息发生更新
                    case 3:
                    {
                        updateRealPointDetail(trackFragment.points,trackFragment.notify);
                        break;
                    }
                    //4表示开启或者关闭位移提醒，需要向服务器发送消息
                    case 4:
                    {
                        websocketClient.sendMsg((String) msg.obj);
                        break;
                    }
                    //5表示发生了移动，需要进行通知
                    case 5:
                    {
                        notifyLocationChange();
                        break;
                    }
                    case 6://6表示WS连接成功
                    {
                        Toast.makeText(getApplicationContext(),"客户端服务端已连接",Toast.LENGTH_LONG).show();
                        ifConnected=true;
                        if(timer!=null)
                        {
                            timer.cancel();
                        }
                        delayTime=1000;
                        locationFragment.updateConnectUI();
                        break;
                    }
                    case 7://7表示WS连接失败，则重新连接一次，连接按照时长进行
                    {
                        Toast.makeText(getApplicationContext(),(String)msg.obj,Toast.LENGTH_LONG).show();
                        ifConnected=false;
                        reConnect();
                        locationFragment.updateConnectUI();
                        break;
                    }
                    case 8://8表示主动发起重连，立刻重连
                    {
                        websocketClient.connect();
                    }
                    case 9://9表示曾经连接成功过，但是现在连接断开了
                    {
                        Toast.makeText(getApplicationContext(),"客户端与服务端断开连接",Toast.LENGTH_LONG).show();
                        ifConnected=false;
                        reConnect();
                        locationFragment.updateConnectUI();
                        break;
                    }
                }
            }
        };
    }

    //开启我的位置定位服务
    void initMyLocationService()
    {
        myLocationService =((App)getApplication()).myLocationService;
        myLocationListener =new MyLocationListener(handler);
        myLocationService.registerListener(myLocationListener);
        myLocationService.start();
    }

    //开启车辆位置定位
    void initCarLocationService()
    {
        try {
            websocketClient=new
                    WebsocketClient(WS_SERVER_ADDRESS,1,handler);
            websocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    //初始化各个fragment
    void initFragment()
    {
        fragmentManager =getSupportFragmentManager();
        notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //实例化四个fragment，其中，默认一开始显示开锁页面
        switchFragment=new SwitchFragment(handler);
        locationFragment=new LocationFragment(handler);
        trackFragment=new TrackFragment(this,handler);
        realTimePointFragment=new RealTimePointFragment(this);

        fragmentManager.beginTransaction().add(R.id.fragment,switchFragment).commit();
    }

    //初始化主界面各个按钮
    void initButton()
    {
        open=findViewById(R.id.open);
        location=findViewById(R.id.location);
        track=findViewById(R.id.track);

        point=findViewById(R.id.point);
        tabs=findViewById(R.id.tabs);

        time=findViewById(R.id.time);
        from=findViewById(R.id.from);
        to=findViewById(R.id.to);
        last=findViewById(R.id.last);
        distance=findViewById(R.id.distance);

        location.setOnClickListener(view -> {
            fragmentManager.beginTransaction().replace(R.id.fragment,locationFragment).commit();
        });

        open.setOnClickListener(view->{
            fragmentManager.beginTransaction().replace(R.id.fragment,switchFragment).commit();
        });
        track.setOnClickListener(view->{
            fragmentManager.beginTransaction().replace(R.id.fragment,trackFragment).commit();
        });
    }

    void reConnect()
    {
        timer=new Timer();
        timer.schedule(new Task(),delayTime);
        delayTime*=2;
    }

    //关闭程序
    public void killAppProcess()
    {
        //先杀掉相关进程最后杀掉主进程
        ActivityManager mActivityManager = (ActivityManager)MainActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> mList = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : mList)
        {
            if (runningAppProcessInfo.pid != android.os.Process.myPid())
            {
                android.os.Process.killProcess(runningAppProcessInfo.pid);
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    //发出车辆移动通知
    void notifyLocationChange()
    {
        String id = "1"; //自定义设置通道ID属性
        String description = "通知车辆移动信息";//自定义设置通道描述属性
        int importance = NotificationManager.IMPORTANCE_HIGH;//通知栏管理重要提示消息声音设定
        NotificationChannel mChannel = null;//建立通知栏通道类（需要有ID，重要属性）
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("requestCode","moving");
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),
                0,intent,PendingIntent.FLAG_IMMUTABLE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(id, description, importance);
            notificationManager.createNotificationChannel(mChannel);////最后在notificationmanager中创建该通知渠道
            Notification notification = new Notification.Builder(getApplicationContext(), id)//创建Notification对象。
                    .setContentTitle("移动提醒")  //设置通知标题
                    .setSmallIcon(R.drawable.notify)//设置通知小图标
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.loc))//设置通知大图标
                    .setContentText("车辆位置发生移动，请及时关注")//设置通知内容
                    .setAutoCancel(true)//设置自动删除通知
                    .setContentIntent(pendingIntent)
                    .build();//运行
            notificationManager.notify((int) System.currentTimeMillis(), notification); //通知栏保留多条通知
        }
    }

    @Override
    public void onItemClick(Notify notification) {
        PointFragment pointFragment=new PointFragment(this,notification.id);
        fragmentManager.beginTransaction().replace(R.id.fragment,pointFragment).commit();

        updatePointDetail(notification);

        tabs.setVisibility(View.INVISIBLE);
        point.setVisibility(View.VISIBLE);
    }

    @Override
    public void onArrowClick() {
        fragmentManager.beginTransaction().replace(R.id.fragment,trackFragment).commit();
        tabs.setVisibility(View.VISIBLE);
        point.setVisibility(View.INVISIBLE);
    }

    void updatePointDetail(Notify notification)
    {
        time.setText(notification.time);
        from.setText("移动起始位置:"+notification.from);
        to.setText("移动结束位置:"+notification.to);
        last.setText("移动持续时间:"+notification.last);
        distance.setText("移动距离:"+notification.distance);
    }

    @Override
    public void onRealTimeClick() {
        realPointOn=true;
        realTimePointFragment.updateDataAndMap(trackFragment.points);
        fragmentManager.beginTransaction().replace(R.id.fragment,realTimePointFragment).commit();

        updateRealPointDetail(trackFragment.points,trackFragment.notify);

        tabs.setVisibility(View.INVISIBLE);
        point.setVisibility(View.VISIBLE);
    }

    void updateRealPointDetail(List<LatLng> data, Notify notification)
    {
        realTimePointFragment.updateDataAndMap(data);
        from.setText("移动起始位置:"+notification.from);
        time.setText("移动起始时间:"+ notification.time);
        to.setText("当前车辆位置:"+notification.to);
        last.setText("移动累计持续时间:"+notification.last);
        distance.setText("移动累计总距离:"+notification.distance);
    }

    @Override
    public void onRealArrowClick() {
        realPointOn=false;
        fragmentManager.beginTransaction().replace(R.id.fragment,trackFragment).commit();
        tabs.setVisibility(View.VISIBLE);
        point.setVisibility(View.INVISIBLE);
    }

    public class Task extends TimerTask {

        @Override
        public void run() {
            websocketClient.connect();
        }
    }

}