package com.example.electrocarmanager.Service.CarLocationService;

import android.os.Handler;
import android.os.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

public class WebsocketClient extends AbstractWebsocketClient {
    public Handler msgHandler;

    private static final Logger log = LoggerFactory.getLogger(WebsocketClient.class);

    private static final NioEventLoopGroup NIO_GROUP = new NioEventLoopGroup();

    private final URI uri;

    private final int port;

    private Bootstrap bootstrap;

    private WebsocketClientHandler handler;

    private Channel channel;

    public WebsocketClient(String url, int connectionTimeout,Handler handler) throws URISyntaxException, MyException {
        super(connectionTimeout);
        this.uri = new URI(url);
        this.port = getPort();
        this.msgHandler=handler;
    }

    /**
     * Extract the specified port
     *
     * @return the specified port or the default port for the specific scheme
     */
    private int getPort() throws MyException {
        int port = uri.getPort();
        if (port == -1) {
            String scheme = uri.getScheme();
            if ("wss".equals(scheme)) {
                return 443;
            } else if ("ws".equals(scheme)) {
                return 80;
            } else {
                throw new MyException("unknown scheme: " + scheme);
            }
        }
        return port;
    }

    @Override
    protected void doOpen() {
        // websocket客户端握手实现的基类
        WebSocketClientHandshaker webSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
        // 业务处理类
        handler = new WebsocketClientHandler(webSocketClientHandshaker, this.websocketContext,this.msgHandler);
        // client端，引导client channel启动
        bootstrap = new Bootstrap();
        // 添加管道 绑定端口 添加作用域等
        bootstrap.group(NIO_GROUP).channel(NioSocketChannel.class).handler(new WebsocketChannelInitializer(handler));
    }

    @Override
    protected void doConnect() {
        try {
            // 启动连接
            channel = bootstrap.connect(uri.getHost(), port).sync().channel();
            // 等待握手响应
            handler.handshakeFuture().sync();
        } catch (InterruptedException e) {
            log.error("websocket连接发生异常", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected Channel getChannel() {
        return channel;
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.close();
        }
    }

    @Override
    public void connect()  {
        new Thread()
        {
            @Override
            public void run()
            {
                try {
                    doOpen();
                    doConnect();
                } catch (Exception e) {
                    Message msg=new Message();
                    msg.what=7;
                    msg.obj="与服务器连接失败,原因是:{" + e.getMessage()+"}";
                    msgHandler.sendMessage(msg);
                }
            }
        }.start();

    }

    public boolean isOpen() {
        return channel.isOpen();
    }

    public void sendMsg(String msg)
    {
        handler.sendMsgToServer(msg);
    }
}
