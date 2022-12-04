package com.carmanager.server.webSocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author bbg
 * 通道配置
 * 初始化通道，设置端口配置
 */
public class WBChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast("logging",new LoggingHandler("DEBUG"));
        ch.pipeline().addLast("http-codec",new HttpServerCodec());
        ch.pipeline().addLast("aggregator",new HttpObjectAggregator(65536));
        ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
        ch.pipeline().addLast("handler",new WBHandler());
    }
}
