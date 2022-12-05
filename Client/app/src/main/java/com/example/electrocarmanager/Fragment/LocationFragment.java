package com.example.electrocarmanager.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.electrocarmanager.Entity.MovingDto;
import com.example.electrocarmanager.MainActivity;
import com.example.electrocarmanager.R;
import com.example.electrocarmanager.Utils.DateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author bbg
 *用于显示车辆实时位置，车辆持有者实时位置的fragment
 */
public class LocationFragment extends Fragment {

    Gson gson=new GsonBuilder().registerTypeAdapter(Date.class,new DateAdapter()).create();

    MapView mapView;

    Handler handler;

    LatLng myLoc;
    LatLng carLoc;
    float ra;//radius
    float di;//direction
    RoutePlanSearch search;
    List<LatLng> points=new ArrayList<>();//路线规划展示的路径
    List<Overlay> polyLines =new ArrayList<>();//存储路线
    boolean ifFirst=true;

    //UI
    BaiduMap baiduMap;
    MapStatus mapStatus=null;//全局地图状态
    ImageView navigation;
    ImageView round;
    ImageView moveNotification;
    ImageView roundConnect;
    ImageView connect;
    List<Overlay> overlays=new ArrayList<>();


    public LocationFragment(Handler handler)
    {
        this.handler=handler;
    }

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        if(ifFirst)
        {
            //获取最近一次使用时我和车的位置，若是第一次使用则设置默认值
            SharedPreferences preferences=getContext().getSharedPreferences("lastLoc", Context.MODE_PRIVATE);
            double myLat=preferences.getFloat("myLat",30.5f);
            double myLog=preferences.getFloat("myLog",114.3f);
            double carLat=preferences.getFloat("carLat",30.45f);
            double carLog=preferences.getFloat("carLog",114.25f);
            float radius=preferences.getFloat("radius",30.0f);
            float direction=preferences.getFloat("direction",30.0f);
            myLoc=new LatLng(myLat,myLog);
            carLoc=new LatLng(carLat,carLog);
            ra=radius;
            di=direction;
            ifFirst=false;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup,@Nullable Bundle savedBundle)
    {
        super.onCreateView(layoutInflater,viewGroup,savedBundle);
        return layoutInflater.inflate(R.layout.location_fragment,viewGroup,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@NonNull Bundle savedBundle)
    {
        super.onViewCreated(view,savedBundle);
        initUI(view);
        updateLocation();
        updateCarLocation();
        updateRoute();
    }

    void initUI(View view)
    {
        mapView=view.findViewById(R.id.map);
        baiduMap=mapView.getMap();
        round=view.findViewById(R.id.round);
        navigation=view.findViewById(R.id.navigation);
        roundConnect=view.findViewById(R.id.round_connect);
        connect=view.findViewById(R.id.connect);
        moveNotification=view.findViewById(R.id.move_notification);
        round.setOnClickListener(v->{
            updateNotificationUI();
        });
        if(MainActivity.realAlertOn)
        {
            round.setImageResource(R.drawable.round_open);
            moveNotification.setImageResource(R.drawable.notification_on);
        }
        else
        {
            round.setImageResource(R.drawable.round);
            moveNotification.setImageResource(R.drawable.move_notification);
        }
        if(MainActivity.ifConnected)
        {
            roundConnect.setVisibility(View.INVISIBLE);
            connect.setVisibility(View.INVISIBLE);
        }
        else
        {
            roundConnect.setVisibility(View.VISIBLE);
            connect.setVisibility(View.VISIBLE);
            roundConnect.setOnClickListener(v->{
                Message msg=new Message();
                msg.what=8;
                handler.sendMessage(msg);
            });
        }

        navigation.setOnClickListener(v-> {
            if(polyLines.size()!=0)
            {
                baiduMap.removeOverLays(polyLines);
                polyLines.clear();
            }
            search=RoutePlanSearch.newInstance();
            OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
                @Override
                public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
                    //创建WalkingRouteOverlay实例
                    List<WalkingRouteLine> list=walkingRouteResult.getRouteLines();
                    for(WalkingRouteLine line:list)
                    {
                        List<WalkingRouteLine.WalkingStep> steps=line.getAllStep();
                        for(WalkingRouteLine.WalkingStep step:steps)
                        {
                            List<LatLng> data=step.getWayPoints();
                            {
                                for(LatLng point:data)
                                {
                                    points.add(point);
                                }
                            }
                        }
                    }
                    updateRoute();
                }

                @Override
                public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

                }

                @Override
                public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

                }

                @Override
                public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

                }

                @Override
                public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

                }

                @Override
                public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

                }
            };
            search.setOnGetRoutePlanResultListener(listener);
            PlanNode stNode = PlanNode.withLocation(myLoc);
            PlanNode enNode = PlanNode.withLocation(carLoc);
            search.walkingSearch((new WalkingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
        });
    }
    @Override
    public void onResume()
    {
        super.onResume();
        mapView.onResume();

    }

    @Override
    public void onPause()
    {
        mapView.onPause();
        super.onPause();

    }

    @Override
    public void onDestroy()
    {
        mapStatus= baiduMap.getMapStatus();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        overlays.clear();
        baiduMap=null;
        if(search!=null)
        {
            search.destroy();
        }
        SharedPreferences preferences=getContext().getSharedPreferences("lastLoc",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putFloat("myLat",(float) myLoc.latitude);
        editor.putFloat("myLog",(float) myLoc.longitude);
        editor.putFloat("carLat",(float) carLoc.latitude);
        editor.putFloat("carLog",(float) carLoc.longitude);
        editor.putFloat("radius",ra);
        editor.putFloat("direction",di);
        editor.commit();
        super.onDestroy();
    }


    /**
     * 解析传过来的json实时位置信息，若解析成功则更新位置坐标
     * @param json 序列化的MovingDto
     */
    public void parseJsonAndUpdateCarLocation(String json)
    {
        try{
            MovingDto movingDto = gson.fromJson(json, MovingDto.class);
            if(movingDto.toLatitude!=null&&movingDto.toLongitude!=null) {
                carLoc = new LatLng(movingDto.toLatitude, movingDto.toLongitude);
                updateCarLocation();
            }
        }
        catch(Exception ex)
        {
            return;
        }
    }


    /**
     * 更新车辆的实时位置
     */
    public void updateCarLocation()
    {
        if(baiduMap==null)
        {
            return;
        }
        if(overlays.size()!=0)
        {
            baiduMap.removeOverLays(overlays);
            overlays.clear();
        }
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.loc1);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                    .position(carLoc)
                    .icon(bitmap);
        //在地图上添加Marker，并显示
        Overlay overlay=baiduMap.addOverlay(option);
        overlays.add(overlay);
    }

    /**
     * 更新我的当前位置
     * @param location 我的当前位置
     */
    public void updateMyLocation(BDLocation location)
    {
        if(baiduMap==null)//防止更新位置方法在fragment被初始化之前就被调用导致空指针
        {
            return;
        }
        myLoc=new LatLng(location.getLatitude(),location.getLongitude());
        baiduMap.setMyLocationEnabled(true);
        ra=location.getRadius();
        di=location.getDirection();
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(ra)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(di).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        baiduMap.setMyLocationData(locData);
    }

    /**
     * 绘制第一次我的位置
     */
    void updateLocation()
    {
        MapStatus mMapStatus;
        myLoc=new LatLng(myLoc.latitude,myLoc.longitude);
        if(mapStatus!=null)
        {
            mMapStatus=mapStatus;
        }
        else
        {
            //定义地图状态，设定中心点坐标
            mMapStatus = new MapStatus.Builder()
                    .target(myLoc)
                    .build();
        }
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        baiduMap.setMapStatus(mMapStatusUpdate);

        baiduMap.setMyLocationEnabled(true);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(ra)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(di).latitude(myLoc.latitude)
                .longitude(myLoc.longitude).build();
        baiduMap.setMyLocationData(locData);
    }

    void updateRoute()
    {
        if(points.size()==0)
        {
            return;
        }
        //设定中心点坐标
        LatLng cent = new LatLng(points.get(points.size()/2).latitude,points.get(points.size()/2).longitude);

        MapStatus mMapStatus;

        //定义地图状态
        if(mapStatus!=null)
        {
            mMapStatus=mapStatus;
        }
        else
        {
            mMapStatus = new MapStatus.Builder()
                    .target(cent)
                    .build();
        }

        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);
        //设置折线的属性
        OverlayOptions overlayOptions = new PolylineOptions()
                .width(10)
                .color(0xAA6495ED)
                .points(points);
        //在地图上绘制折线
        //mPloyline 折线对象
        Overlay polyline = baiduMap.addOverlay(overlayOptions);
        polyLines.add(polyline);
    }

    /**
     * 控制位移提醒的开关，UI变化
     */
    public void updateNotificationUI()//简单的反转
    {

        Message msg=new Message();
        msg.what=4;
        if(!MainActivity.realAlertOn)
        {
            round.setImageResource(R.drawable.round_open);
            moveNotification.setImageResource(R.drawable.notification_on);
            msg.obj="OPEN_MOVING_ALERT";
            MainActivity.realAlertOn=true;
        }
        else
        {
            round.setImageResource(R.drawable.round);
            moveNotification.setImageResource(R.drawable.move_notification);
            msg.obj="CLOSE_MOVING_ALERT";
            MainActivity.realAlertOn=false;
        }
        handler.sendMessage(msg);
    }
}
