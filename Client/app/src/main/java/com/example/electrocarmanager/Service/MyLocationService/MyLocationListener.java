package com.example.electrocarmanager.Service.MyLocationService;

import android.os.Handler;
import android.os.Message;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

/**
 * @author bbg
 * 具体的listener，依托于服务，用于定位我的位置
 */
public class MyLocationListener extends BDAbstractLocationListener {

    public static Handler handler;

    public MyLocationListener(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (null != location && location.getLocType() != BDLocation.TypeServerError) {
            Message msg = new Message();
            msg.what = 1;
            msg.obj = location;
            handler.sendMessage(msg);
        }
    }
}
