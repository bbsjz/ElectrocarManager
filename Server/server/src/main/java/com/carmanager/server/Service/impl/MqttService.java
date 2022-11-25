package com.carmanager.server.Service.impl;

import com.carmanager.server.Entity.Command;
import com.carmanager.server.Service.IMqttService;
import com.carmanager.server.Utils.MqttUtils;
import com.carmanager.server.config.MqttConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class MqttService implements IMqttService {

    public MqttService() {
        MqttUtils.subscribe(MqttConfig.TOPIC_SENSOR, (s, message) -> {
            System.out.println(new String(message.getPayload(), StandardCharsets.UTF_8));
        });
    }

    @Override
    public void controlCar(Command command) {

    }
}
