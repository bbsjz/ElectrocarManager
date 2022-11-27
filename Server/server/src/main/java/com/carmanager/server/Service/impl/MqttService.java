package com.carmanager.server.Service.impl;

import com.carmanager.server.Entity.Command;
import com.carmanager.server.Service.IMqttService;
import com.carmanager.server.Utils.MqttUtils;
import com.carmanager.server.config.MqttConfig;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Service
public class MqttService implements IMqttService {

    private final MqttUtils utils;
    private final MqttConfig config;

    public MqttService(MqttUtils utils, MqttConfig config) {
        this.utils = utils;
        this.config = config;
    }

    @PostConstruct
    public void init() {
        utils.subscribe(config.TOPIC_SENSOR, (s, message) -> {
            System.out.println(new String(message.getPayload(), StandardCharsets.UTF_8));
        });
    }

    @Override
    public void controlCar(Command command) {

    }
}
