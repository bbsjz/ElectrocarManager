package com.example.electrocarmanager.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.electrocarmanager.R;

/**
 * @author bbg
 *用于显示车辆实时位置，车辆持有者实时位置的fragment
 */
public class LocationFragment extends Fragment {

    MapView mapView;

    //定位相关
    BaiduMap baiduMap;

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
        mapView=view.findViewById(R.id.map);
        baiduMap=mapView.getMap();
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
     * @param lat
     * @param log
     */
    public void updateCarLocation(double lat,double log)
    {
        //定义Maker坐标点
        LatLng point = new LatLng(lat, log);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.loc1);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option);
    }

    /**
     * 更新我的当前位置
     * @param location
     */
    public void updateMyLocation(BDLocation location)
    {
        if(baiduMap==null)//防止更新位置方法在fragment被初始化之前就被调用导致空指针
        {
            return;
        }
        baiduMap.setMyLocationEnabled(true);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.getDirection()).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        baiduMap.setMyLocationData(locData);
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null);
        baiduMap.setMyLocationConfiguration(config);
    }
}
