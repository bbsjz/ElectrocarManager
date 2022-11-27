package com.carmanager.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Value("${mqtt.server-address}")
    public String MQTT_SERVER_ADDRESS;

    @Value("${mqtt.publisher-id}")
    public String MQTT_PUBLISHER_ID;

    @Value("${mqtt.user-name}")
    public String USER_NAME;

    @Value("${mqtt.password}")
    public String PASSWORD;


    @Value("${mqtt.topic.sensor}")
    public String TOPIC_SENSOR;

    @Value("${mqtt.topic.lock}")
    public String TOPIC_LOCK;

    @Value("${mqtt.qos}")
    public int QOS;

    @Value("${mqtt.retained}")
    public boolean RETAINED;

}
