package com.carmanager.server.Service.impl;

import com.carmanager.server.Entity.Command;
import com.carmanager.server.Service.IMqttService;
import com.carmanager.server.Utils.MqttUtils;
import com.carmanager.server.config.MqttConfig;
import com.carmanager.server.mqtt.MqttFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Service
public class MqttService implements IMqttService {

    private final MqttUtils utils;
    private final MqttConfig config;

    private static final Logger logger = Logger.getLogger(String.valueOf(MqttFactory.class));

    public MqttService(MqttUtils utils, MqttConfig config) {
        this.utils = utils;
        this.config = config;
    }

    /**
     * 订阅来自硬件的消息
     */
    @PostConstruct
    public void init() {
        utils.subscribe(config.TOPIC_SENSOR, (s, message) -> {
            logger.info("Receive message from topic: " +
                    config.TOPIC_SENSOR +
                    (new String(message.getPayload(), StandardCharsets.UTF_8)));
        });
    }

    @Override
    public void controlCar(Command command) {

    }
}
