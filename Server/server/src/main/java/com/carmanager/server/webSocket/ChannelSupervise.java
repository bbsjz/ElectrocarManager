package com.carmanager.server.webSocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author bbg steve
 * 通道管理类，对于已经加入的客户端进行统计和管理
 */
public class ChannelSupervise {

    /**
     * 记录当前加入的所有频道信息
     */
    private static final ChannelGroup globalGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 记录当前加入的频道是否开启位移提醒
     */
    private static final ChannelGroup alertGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 将加入连接的用户保存
     * @param channel 保存的频道
     */
    public static void addChannel(Channel channel) {
        globalGroup.add(channel);
    }

    /**
     * 将退出连接的用户去除
     * @param channel 去除的频道
     */
    public static void removeChannel(Channel channel) {
        globalGroup.remove(channel);
        if (alertGroup.find(channel.id()) != null) {
            alertGroup.remove(channel);
        }
    }

    /**
     * 添加开启了位移提醒的通道
     * @param channel 添加的频道
     */
    public static void addMoveAlert(Channel channel) {
        alertGroup.add(channel);
    }

    /**
     * 获得当前是否有频道开启了位移提醒
     * @return 是否有频道开启了位移提醒
     */
    public static boolean ifAlertOn() {
        return alertGroup.size() > 0;
    }

    /**
     * 删除开启了位移提醒的通道
     * @param channel 删除的通道
     */
    public static void removeMoveAlert(Channel channel) {
        alertGroup.remove(channel);
    }

    /**
     * 根据Id查找通道
     * @param id 通道Id
     * @return 查找到的通道
     */
    public static Channel findChannel(ChannelId id){
        return globalGroup.find(id);
    }

    /**
     * 向所有已经加入连接的客户端推送消息
     * @param tws 消息
     */
    public static void send2All(TextWebSocketFrame tws){
        for (Channel channel : globalGroup) {
            channel.writeAndFlush(tws);
        }
    }

    /**
     * 向所有打开了位移提醒的客户端推送消息
     * @param tws 消息
     */
    public static void send2OpenLocationRemoveAlert(TextWebSocketFrame tws) {
        for (Channel channel : alertGroup) {
            channel.writeAndFlush(tws);
        }
    }

    /**
     * 向所有未打开位移提醒的客户端推送消息
     * @param tws 消息
     */
    public static void send2CloseLocationRemoveAlert(TextWebSocketFrame tws) {
        for (Channel channel : globalGroup) {
            if (alertGroup.find(channel.id()) == null) {
                channel.writeAndFlush(tws);
            }
        }
    }
}
