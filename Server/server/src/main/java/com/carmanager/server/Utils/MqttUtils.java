package com.carmanager.server.Utils;

import com.carmanager.server.config.MqttConfig;
import com.carmanager.server.mqtt.MqttFactory;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Component
public class MqttUtils {

    private static final Logger logger = Logger.getLogger(String.valueOf(MqttUtils.class));

    private final MqttFactory factory;

    private final MqttConfig config;

    public MqttUtils(MqttFactory factory, MqttConfig config) {
        this.factory = factory;
        this.config = config;
    }

    /**
     * 发送消息
     * @param topic 主题
     * @param data 消息内容
     */
    public void send(String topic, Object data) {
        IMqttClient client = factory.getInstance();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        try {
            MqttMessage message = new MqttMessage(json.getBytes(StandardCharsets.UTF_8));
            message.setQos(config.QOS);
            message.setRetained(config.RETAINED);
            client.publish(topic, message);
        } catch (MqttException e) {
            logger.severe(String.format("MQTT: 向主题[%s]发送消息失败, 消息内容: [%s]", topic, json));
        }
    }

    /**
     * 订阅主题
     * @param topic 主题
     * @param listener 消息监听处理函数
     */
    public void subscribe(String topic, IMqttMessageListener listener) {
        IMqttClient client = factory.getInstance();
        try {
            client.subscribe(topic, listener);
        } catch (MqttException e) {
            logger.severe(String.format("MQTT: 订阅主题[%s]失败", topic));
        }
    }

}
