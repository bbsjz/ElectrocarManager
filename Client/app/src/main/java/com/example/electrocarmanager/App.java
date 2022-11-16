package com.example.electrocarmanager;

import android.app.Application;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.example.electrocarmanager.LocationUtil.MyLocation.MyLocationService;

public class App extends Application {
    MyLocationService myLocationService;

    @Override
    public void onCreate()
    {
        super.onCreate();
        SDKInitializer.setAgreePrivacy(this,true);
        SDKInitializer.initialize(this);
        LocationClient.setAgreePrivacy(true);
        try {
            myLocationService = new MyLocationService(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
