package com.example.electrocarmanager.LocationUtil.CarLocation;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class WebsocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final WebsocketClientHandler handler;

    public WebsocketChannelInitializer(WebsocketClientHandler handler) {
        this.handler = handler;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpClientCodec());
        p.addLast(new HttpObjectAggregator(8192));
//        p.addLast(WebSocketClientCompressionHandler.INSTANCE);
        p.addLast(handler);
    }
}
