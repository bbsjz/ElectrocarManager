package com.example.electrocarmanager.LocationUtil.CarLocation;

import android.os.Handler;
import android.os.Message;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class CarLocationClient extends WebSocketClient {
    Handler handler;

    public CarLocationClient(URI serverUri,Handler handler) {
        super(serverUri);
        this.handler=handler;
    }

    @Override
    public void onOpen(ServerHandshake handShakeData) {

    }

    @Override
    public void onMessage(String message) {
        //TODO:接收到服务端发来的位置更新后，更新地图位置
        Message msg=new Message();
        msg.what=2;
        handler.sendMessage(msg);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {
    }
}
