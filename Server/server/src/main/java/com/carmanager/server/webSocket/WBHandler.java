package com.carmanager.server.webSocket;

import com.carmanager.server.Entity.DateMove;
import com.carmanager.server.Entity.Move;
import com.carmanager.server.Service.DateMoveService;
import com.carmanager.server.Utils.DateUtils;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;


/**
 * @author bbg
 * 业务控制器
 * 成功建立链接之后，在此类中进行具体的消息收发处理
 */
public class WBHandler extends SimpleChannelInboundHandler<Object> {

    @Autowired
    DateMoveService service;

    Gson gson=new Gson();


    //上一次的移动方向角，用于判断是否发生了移动

    double lastDir=-1;
    //上一次的移动加速度，用于判断是否发生了移动过
    double lastAccelerated=-1;

    //用于记录当前是否处于移动状态
    boolean ifIsMoving;

    //最近一次正处于移动状态的时间
    Date lastMovingTime;

    //是否刚刚打开移动功能，用于更新移动位置
    boolean onOpen=false;

    //当前的位置移动信息
    Move move=new Move();
    private final Logger logger=Logger.getLogger(String.valueOf(this.getClass()));

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("收到消息："+msg);
        if (msg instanceof FullHttpRequest){
            //以http请求形式接入，但是走的是websocket
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }else if (msg instanceof WebSocketFrame){
            //处理websocket客户端的消息
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加连接
        logger.info("客户端加入连接："+ctx.channel());
        ChannelSupervise.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //断开连接
        logger.info("客户端断开连接："+ctx.channel());
        ChannelSupervise.removeChannel(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 每次收到更新的位置之后，服务器将位置更新给所有在线的用户，
     * 当有用户开启位移提醒时，服务器判断是否发生移动，并将移动信息返回给开启移动提醒的用户
     * @param lat
     * @param log
     * @param dir
     * @param accelerated
     */
    public void sendMsg(double lat,double log,double dir,double accelerated)
    {
        TextWebSocketFrame tws=new TextWebSocketFrame("{"+"\"lat\":"+lat+",\"log\":"+log+"}");
        ChannelSupervise.send2All(tws);
        if(ChannelSupervise.ifAlertOn())
        {
            String json="";
            //如果现在发现正在移动，且正处于移动状态中，记录此刻发生移动的时间
            if(isMoving(dir,accelerated)&&ifIsMoving)
            {
                lastMovingTime=new Date();
                move.setEndTime(null);
                move.setToLocation("{"+"\"lat\":"+lat+",\"log\":"+log+"}");
                //TODO:修改json把当前正在移动的数据加入进去
            }
            //如果发现正在移动，且未置为移动状态，则记录首次移动时间，首次移动位置，并将当前状态设置为正在移动
            else if(isMoving(dir,accelerated))
            {
                move.setBeginTime(DateUtils.toHourAndMinute(new Date()));
                move.setFromLocation("{"+"\"lat\":"+lat+",\"log\":"+log+"}");
                lastMovingTime=new Date();
                ifIsMoving=true;
            }
            else if(isStop())
            {
                move.setEndTime(DateUtils.toHourAndMinute(new Date()));
                move.setToLocation("{"+"\"lat\":"+lat+",\"log\":"+log+"}");
                ifIsMoving=false;
                //TODO:调用数据库存储这条记录
            }
            ChannelSupervise.send2OpenLocationRemoveAlert(new TextWebSocketFrame(json));
        }
    }

    public boolean isMoving(double dir,double accelerated)
    {
        if(!onOpen)//刚刚打开位移提醒，则把最后一次的位置更新成现在的位置
        {
            lastDir=dir;
            lastAccelerated=accelerated;
            onOpen=true;
            return false;
        }
        return (Math.abs(lastDir-dir)>2||Math.abs(lastAccelerated-accelerated)>2);
    }

    public boolean isStop()
    {
        //都没有开启一次移动，那肯定也没有停止的概念，直接返回false
        if(!ifIsMoving)
        {
            return false;
        }
        return new Date().after(DateUtils.subtractTime(lastMovingTime,300000));
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            logger.info("本例程仅支持文本消息，不支持二进制消息");
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }
        // 返回应答消息
        String request = ((TextWebSocketFrame) frame).text();

        //判断该消息是否为开启位移提醒的消息
        if(request.equals("开启位移提醒"))
        {
            ChannelSupervise.addMoveAlert(ctx.channel());
        }
        //判断该消息是否为关闭位移提醒的消息
        if(request.equals("关闭位移提醒"))
        {
            ChannelSupervise.removeMoveAlert(ctx.channel());
        }

        logger.info("服务端收到：" + request);
        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
                + ctx.channel().id() + "服务端已经收到：" + request);
        // 群发
        ChannelSupervise.send2All(tws);
        // 返回【谁发的发给谁】
        // ctx.channel().writeAndFlush(tws);
    }
    /**
     * 唯一的一次http请求，用于创建websocket
     * */
    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) {
        //要求Upgrade为websocket，过滤掉get/Post
        if (!req.decoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            //若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:8080/websocket", null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }
    /**
     * 拒绝不合法的请求，并返回错误信息
     * */
    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // 如果是非Keep-Alive，关闭连接
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
