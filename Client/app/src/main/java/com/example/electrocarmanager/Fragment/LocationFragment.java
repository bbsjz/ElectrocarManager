package com.example.electrocarmanager.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.electrocarmanager.Entity.MovingDto;
import com.example.electrocarmanager.MainActivity;
import com.example.electrocarmanager.R;
import com.google.gson.Gson;

/**
 * @author bbg
 *用于显示车辆实时位置，车辆持有者实时位置的fragment
 */
public class LocationFragment extends Fragment {

    Gson gson=new Gson();

    MapView mapView;

    boolean isFirst=true;

    //UI
    BaiduMap baiduMap;
    ImageView navigation;
    ImageView round;
    ImageView moveNotification;

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
    }

    void initUI(View view)
    {
        mapView=view.findViewById(R.id.map);
        baiduMap=mapView.getMap();
        round=view.findViewById(R.id.round);
        navigation=view.findViewById(R.id.navigation);
        moveNotification=view.findViewById(R.id.move_notification);
        round.setOnClickListener(v->{
            updateNotificationUI();
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
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
        super.onDestroy();
    }


    /**
     * 更新车辆的实时位置
     * @param json 序列化的MovingDto
     */
    public void updateCarLocation(String json)
    {
        MovingDto movingDto = gson.fromJson(json, MovingDto.class);
        if(movingDto.toLatitude!=null&&movingDto.toLongitude!=null)
        {
            //定义Maker坐标点
            LatLng point = new LatLng(movingDto.toLatitude, movingDto.toLongitude);
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.loc1);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            baiduMap.addOverlay(option);
        }
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
        if(isFirst)
        {
            //设定中心点坐标
            LatLng cent = new LatLng(location.getLatitude(),location.getLongitude());
            //定义地图状态
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(cent)
                    .build();
            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            baiduMap.setMapStatus(mMapStatusUpdate);
            isFirst=false;
        }
        baiduMap.setMyLocationEnabled(true);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.getDirection()).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        baiduMap.setMyLocationData(locData);
    }

    void updateNotificationUI()
    {
        if(!MainActivity.realAlertOn)
        {
            round.setImageDrawable(getResources().getDrawable(R.drawable.round_open));
            MainActivity.realAlertOn=true;
        }
        else
        {
            round.setImageDrawable(getResources().getDrawable(R.drawable.round));
            moveNotification.setImageDrawable(getResources().getDrawable(R.drawable.move_notification));
            MainActivity.realAlertOn=false;
        }
    }
}
