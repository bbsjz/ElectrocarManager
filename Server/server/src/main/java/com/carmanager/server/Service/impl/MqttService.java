package com.carmanager.server.Service.impl;

import com.carmanager.server.Entity.Command;
import com.carmanager.server.Entity.Point;
import com.carmanager.server.Service.IMqttService;
import com.carmanager.server.Utils.MqttUtils;
import com.carmanager.server.config.MqttConfig;
import com.carmanager.server.mqtt.MqttFactory;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

@Service
public class MqttService implements IMqttService {

    private final MqttUtils utils;
    private final MqttConfig config;

    private final WebSocketService webSocketService;

    private static final Logger logger = Logger.getLogger(String.valueOf(MqttFactory.class));

    public MqttService(MqttUtils utils, MqttConfig config, WebSocketService webSocketService) {
        this.utils = utils;
        this.config = config;
        this.webSocketService = webSocketService;
    }

    /**
     * 订阅来自硬件的消息
     */
    @PostConstruct
    public void init() {
        utils.subscribe(config.TOPIC_SENSOR, (s, message) -> {
            String json = new String(message.getPayload(), StandardCharsets.UTF_8);
            logger.info("Receive message from topic: " + config.TOPIC_SENSOR + json);

            //TODO:Add AES128

            Gson gson = new Gson();
            Point point = gson.fromJson(json, Point.class);
            point.setId(null);  // 防止Id重复
            point.setCreateTime(new Date());
            System.out.println(point);
            webSocketService.update(point);
        });
    }

    @Override
    public void controlCar(Command command) {
        utils.send(config.TOPIC_LOCK, command);
    }
}
