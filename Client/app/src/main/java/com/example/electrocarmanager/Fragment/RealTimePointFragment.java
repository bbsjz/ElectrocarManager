package com.example.electrocarmanager.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.electrocarmanager.R;
import java.util.List;

public class RealTimePointFragment extends Fragment implements View.OnClickListener{

    List<LatLng> data;
    ImageView arrow;
    PointFragment.ArrowClickListener arrowClickListener;

    MapView mapView;
    BaiduMap baiduMap;

    public RealTimePointFragment(PointFragment.ArrowClickListener arrowClickListener)
    {
        this.arrowClickListener=arrowClickListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater,
                             @Nullable ViewGroup viewGroup, @Nullable Bundle savedBundle)
    {
        super.onCreateView(layoutInflater,viewGroup,savedBundle);
        return layoutInflater.inflate(R.layout.point_fragment,viewGroup,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedBundle)
    {
        arrow=view.findViewById(R.id.arrow);
        mapView=view.findViewById(R.id.map);
        baiduMap=mapView.getMap();
        arrow.setOnClickListener(this);
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


    @Override
    public void onClick(View v) {
        if(arrowClickListener!=null)
        {
            arrowClickListener.onArrowClick();
        }
    }

    public interface RealArrowClickListener{
        void onRealArrowClick();
    }

    /**
     * 更新数据来源
     * 根据新的点集数据，将途径点轨迹绘制在地图上，调用一次则更新一次数据，并更新一次地图
     * @param data 点集数据
     */
    public void updateDataAndMap(List<LatLng> data)
    {
        this.data=data;
        //设定中心点坐标
        LatLng cent = new LatLng(data.get(data.size()/2).latitude,data.get(data.size()/2).longitude);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(cent)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);
        //设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(10)
                .color(0xAAFF0000)
                .points(data);
        //在地图上绘制折线
        //mPloyline 折线对象
        Overlay mPolyline = baiduMap.addOverlay(mOverlayOptions);
    }
}
