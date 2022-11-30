package com.example.electrocarmanager.Location.CarLocation;

import android.os.Handler;
import android.os.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebsocketClientHandler extends SimpleChannelInboundHandler<Object> {

    Channel channel;//建立连接的通道

    public Handler handler;

    private static final Logger log = LoggerFactory.getLogger(WebsocketClientHandler.class);

    /**
     * 连接处理器
     */
    private final WebSocketClientHandshaker webSocketClientHandshaker;

    /**
     * netty提供的数据过程中的数据保证
     */
    private ChannelPromise handshakeFuture;

    /**
     * 任务上下文
     */
    private final WebsocketContext websocketContext;

    public WebsocketClientHandler(WebSocketClientHandshaker webSocketClientHandshaker,
                                  WebsocketContext websocketContext,Handler handler) {
        this.webSocketClientHandshaker = webSocketClientHandshaker;
        this.websocketContext = websocketContext;
        this.handler=handler;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    /**
     * ChannelHandler添加到实际上下文中准备处理事件,调用此方法
     *
     * @param ctx ChannelHandlerContext
     */

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    /**
     * 当客户端主动链接服务端的链接后,调用此方法
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channel = ctx.channel();
        webSocketClientHandshaker.handshake(channel);
        log.info("建立连接");
        Message msg=new Message();
        msg.what=6;
        handler.sendMessage(msg);
    }

    /**
     * 链接断开后,调用此方法
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("连接断开");
    }

    /**
     * 接收消息,调用此方法
     *
     * @param ctx ChannelHandlerContext
     * @param msg Object
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (!webSocketClientHandshaker.isHandshakeComplete()) {
            this.handleHttpRequest(msg);
            log.info("websocket已经建立连接");
            return;
        }
        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString() + ')');
        }
        this.handleWebSocketFrame(msg);
    }

    /**
     * 处理http连接请求
     * @param msg:
     * @return:
     */
    private void handleHttpRequest(Object msg) {
        webSocketClientHandshaker.finishHandshake(channel, (FullHttpResponse) msg);
        handshakeFuture.setSuccess();
    }

    /**
     * @param msg 服务端发来的文本信息
     */
    private void handleWebSocketFrame(Object msg) {
        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) //接收到文本消息
        {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            String message=textFrame.text();
            Message json=new Message();
            json.what=2;
            json.obj=message;
            handler.sendMessage(json);
            // ...自定义
        } else if (frame instanceof CloseWebSocketFrame) //接收到关闭帧
             {
            log.info("连接收到关闭帧");
            channel.close();
        }
    }

    /**
     * 运行过程中未捕获的异常,调用此方法
     *
     * @param ctx ChannelHandlerContext
     * @param cause Throwable
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.info("监控触发异常=>{}", cause.getMessage(), cause);
    }

    public void sendMsgToServer(String msg)
    {
        if(channel==null)
        {
            return;
        }
        TextWebSocketFrame text=new TextWebSocketFrame(msg);
        channel.writeAndFlush(text);
    }
}
