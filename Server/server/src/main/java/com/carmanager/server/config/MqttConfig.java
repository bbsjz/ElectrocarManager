package com.carmanager.server.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    public static final String MQTT_SERVER_ADDRESS = "tcp://jp.safengine.xyz:8883"; // broker-cn.emqx.io

    public static final String MQTT_PUBLISHER_ID = "spring-server";

    public static final String TOPIC_SENSOR = "car-manager/sensor";

    public static final String TOPIC_LOCK = "car-manager/lock";

    public static final int QOS = 0;

    public static final boolean RETAINED = false;

}
