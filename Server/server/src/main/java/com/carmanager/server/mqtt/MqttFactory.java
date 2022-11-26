package com.carmanager.server.mqtt;

import com.carmanager.server.config.MqttConfig;
import io.netty.handler.codec.mqtt.MqttProperties;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class MqttFactory {

    private static IMqttClient client;

    private static final Logger logger = Logger.getLogger(String.valueOf(MqttFactory.class));

    /**
     * 获取客户端实例
     * 单例模式, 存在则返回, 不存在则初始化
     */
    public static IMqttClient getInstance() {
        if (client == null) {
            init();
        }
        return client;
    }

    /**
     *   初始化客户端
     */
    public static void init() {
        try {
            // null用于防止MqttClient产生大量持久化文件夹
            client = new MqttClient(MqttConfig.MQTT_SERVER_ADDRESS, MqttConfig.MQTT_PUBLISHER_ID, null);

            // MQTT配置对象
            MqttConnectOptions options = new MqttConnectOptions();
            // 设置自动重连
            options.setAutomaticReconnect(true);
            // 重连清除旧消息
            options.setCleanSession(true);
            // 发送心跳包
            options.setKeepAliveInterval(60);

            if (!client.isConnected()) {
                client.connect(options);
            }
        } catch (MqttException e) {
            logger.severe(String.format("MQTT: 连接消息服务器[%s]失败", MqttConfig.MQTT_SERVER_ADDRESS));
        }
    }

}
