package com.example.electrocarmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
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

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.example.electrocarmanager.Entity.Notification;
import com.example.electrocarmanager.Fragment.LocationFragment;
import com.example.electrocarmanager.Fragment.PointFragment;
import com.example.electrocarmanager.Fragment.RealTimePointFragment;
import com.example.electrocarmanager.Fragment.SwitchFragment;
import com.example.electrocarmanager.Fragment.TrackFragment;
import com.example.electrocarmanager.Location.CarLocation.MyException;
import com.example.electrocarmanager.Location.CarLocation.WebsocketClient;
import com.example.electrocarmanager.Location.MyLocation.MyLocationListener;
import com.example.electrocarmanager.Location.MyLocation.MyLocationService;
import com.example.electrocarmanager.Notification.NotificationRecyclerViewAdapter;
import com.example.electrocarmanager.Utils.DateUtils;

import java.net.URISyntaxException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotificationRecyclerViewAdapter.ItemClickListener,
        PointFragment.ArrowClickListener, RealTimePointFragment.RealArrowClickListener,TrackFragment.RealTimeClickListener{

    //三个fragment
    SwitchFragment switchFragment;
    LocationFragment locationFragment;
    TrackFragment trackFragment;
    RealTimePointFragment realTimePointFragment;
    FragmentManager fragmentManager;

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

    final String SERVER_ADDRESS= "ws://10.128.160.17:8081/websocket";

    //定位服务
    MyLocationService myLocationService;
    MyLocationListener myLocationListener;
    WebsocketClient websocketClient;


    public static Handler handler;
    public static boolean realPointOn=false;//是否正处在实时轨迹界面
    public static boolean realAlertOn=false;//当前是否开启实时位移提醒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    void init()
    {
        //设置状态栏为透明
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                        return;
                    }
                    //2表示车辆定位服务返回了结果
                    case 2:
                    {
                        locationFragment.updateCarLocation((String) msg.obj);
                        trackFragment.getNowMsg((String) msg.obj);
                    }
                    //3表示实时位移信息发生更新
                    case 3:
                    {
                        updateRealPointDetail(trackFragment.points,trackFragment.notification);
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
                    WebsocketClient(SERVER_ADDRESS,1);
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

        //实例化四个fragment，其中，默认一开始显示开锁页面
        switchFragment=new SwitchFragment();
        locationFragment=new LocationFragment();
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

    @Override
    public void onItemClick(Notification notification) {
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

    void updatePointDetail(Notification notification)
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

        updateRealPointDetail(trackFragment.points,trackFragment.notification);

        tabs.setVisibility(View.INVISIBLE);
        point.setVisibility(View.VISIBLE);
    }

    void updateRealPointDetail(List<LatLng> data,Notification notification)
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
}