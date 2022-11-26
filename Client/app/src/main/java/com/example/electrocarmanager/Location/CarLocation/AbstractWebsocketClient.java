package com.example.electrocarmanager.Location.CarLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public abstract class AbstractWebsocketClient implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(AbstractWebsocketClient.class);

    /**
     * 接收响应的超时时间(秒)
     */
    private final int connectionTimeout;

    /**
     * 任务上下文
     */
    protected WebsocketContext websocketContext;

    public AbstractWebsocketClient(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.websocketContext = new WebsocketContext();
    }

    /**
     * @param message 发送文本
     * @return:
     */
    public void write(String message) throws MyException {
        Channel channel = getChannel();
        if (channel != null) {
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return;
        }
        throw new MyException ("连接已经关闭");
    }

    /**
     * @return:
     */
    public void connect() throws MyException {
        try {
            doOpen();
            doConnect();
        } catch (Exception e) {
            throw new MyException ("连接没有成功打开,原因是:{}" + e.getMessage(), e);
        }
    }


    /**
     * @param countDownLatch  计数器
     * @return:
     */
    private void receive(CountDownLatch countDownLatch) throws MyException {
        boolean waitFlag = false;
        try {
            waitFlag = countDownLatch.await(connectionTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.info("此连接未接收到响应信息");
            Thread.currentThread().interrupt();
        }
        if (!waitFlag) {
            log.error("Timeout({}}s) when receiving response message", connectionTimeout);
            throw new MyException ("此连接未接收到响应信息");
        }
    }

    /**
     * 初始化连接
     * @return:
     */
    protected abstract void doOpen();

    /**
     * 建立连接
     * @return:
     */
    protected abstract void doConnect() throws MyException;

    /**
     * 获取本次连接channel
     * @return: {@link Channel}
     */
    protected abstract Channel getChannel();

    /**
     * 关闭连接
     * @return:
     * @exception:
     */
    @Override
    public abstract void close();
}