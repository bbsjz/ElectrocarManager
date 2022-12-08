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
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.electrocarmanager.Entity.Point;
import com.example.electrocarmanager.NetWork.PointPost;
import com.example.electrocarmanager.R;
import com.example.electrocarmanager.Utils.DataBaseDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

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
 * 用于点开某一条位移信息后绘制该次位移信息的详细路线轨迹
 */
public class PointFragment extends Fragment implements View.OnClickListener{

    Long id;

    List<LatLng> data=new ArrayList<>();
    ImageView arrow;
    ArrowClickListener arrowClickListener;

    MapView mapView;
    BaiduMap baiduMap;

    public PointFragment(ArrowClickListener arrowClickListener,Long id)
    {
        this.id=id;
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
        getOldTrackPointAndUpdate();
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
     * 子线程请求网络获取此次位移的轨迹，并更新在地图上
     */
    void getOldTrackPointAndUpdate()
    {
        PointPost post=new PointPost(id);
        Thread thread=new Thread(post);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getData(post.getResult());
        updateMap();
    }

    /**
     * 将String类型的原始数据转化为list<LatLng>
     * @param rowData
     */
    void getData(String rowData)
    {
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DataBaseDateAdapter()).create();
        List<Point> points=gson.fromJson(rowData,new TypeToken<List<Point>>(){}.getType());
        if(points==null)
        {
            return;
        }
        for(Point point:points)
        {
            LatLng latLng=new LatLng(point.latitude,point.longitude);
            data.add(latLng);
        }
    }

    @Override
    public void onClick(View v) {
        if(arrowClickListener!=null)
        {
            arrowClickListener.onArrowClick();
        }
    }

    public interface ArrowClickListener{
        void onArrowClick();
    }

    /**
     * 根据点集数据，将途径点轨迹绘制在地图上，调用一次则更新一次
     */
    void updateMap()
    {
        if(data.size()==0)
        {
            return;
        }
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
                .color(0xAA6495ED)
                .points(data);
        //在地图上绘制折线
        //mPloyline 折线对象
        Overlay mPolyline = baiduMap.addOverlay(mOverlayOptions);

    }

    void test()
    {
        LatLng p=new LatLng(39.5,116.0);
        LatLng p2=new LatLng(39.5,116.1);
        LatLng p3=new LatLng(39.5,116.2);
        LatLng p4=new LatLng(39.5,116.3);
        LatLng p5=new LatLng(39.5,116.4);
        data.add(p);
        data.add(p2);
        data.add(p3);
        data.add(p4);
        data.add(p5);
    }
}
