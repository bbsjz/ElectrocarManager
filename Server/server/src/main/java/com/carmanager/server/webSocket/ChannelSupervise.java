package com.carmanager.server.webSocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author bbg
 * 通道管理类，对于已经加入的客户端进行统计和管理
 */
public class ChannelSupervise {
    private static ChannelGroup GlobalGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //记录当前加入的所有频道信息
    private static ConcurrentMap<String, ChannelId> ChannelMap=new ConcurrentHashMap();

    //记录当前加入的频道是否开启位移提醒
    private static List<Channel> alertOn=new ArrayList<>();

    //将加入连接的用户保存
    public static void addChannel(Channel channel){
        GlobalGroup.add(channel);
        ChannelMap.put(channel.id().asShortText(), channel.id());
    }

    //将退出连接的用户去除
    public static void removeChannel(Channel channel){
        GlobalGroup.remove(channel);
        ChannelMap.remove(channel.id().asShortText());
    }

    //添加开启了位移提醒的通道
    public static void addMoveAlert(Channel channel)
    {
        alertOn.add(channel);
    }

    public static boolean ifAlertOn()
    {
        return !(alertOn.size()==0);
    }

    //删除开启了位移提醒的通道
    public static void removeMoveAlert(Channel channel)
    {
        alertOn.remove(channel);
    }

    public static  Channel findChannel(String id){
        return GlobalGroup.find(ChannelMap.get(id));
    }

    //向所有已经加入连接的客户端推送消息
    public static void send2All(TextWebSocketFrame tws){
        GlobalGroup.writeAndFlush(tws);
    }

    //向所有打开了位移提醒的客户端推送消息
    public static void send2OpenLocationRemoveAlert(TextWebSocketFrame tws)
    {
        for(Channel channel:alertOn)
        {
            channel.writeAndFlush(tws);
        }
    }
}
