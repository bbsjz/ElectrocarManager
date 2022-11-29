package com.carmanager.server.Service;

import com.carmanager.server.Entity.Command;
import org.springframework.stereotype.Service;

/**
 * 硬件端服务，提供开锁，关锁，启动接口供调用
 */
@Service
public interface IMqttService {

    /**
     * 遥控电动车开关与启停
     * @param command 指令
     */
    void controlCar(Command command);

}
