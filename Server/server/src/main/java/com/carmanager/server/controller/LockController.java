package com.carmanager.server.controller;

import com.carmanager.server.Entity.Command;
import com.carmanager.server.Service.IMqttService;
import com.carmanager.server.exception.NotSupportArgumentException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api("开关锁控制")
@RestController
@RequestMapping("lock")
public class  LockController {

    final IMqttService mqttService;

    public LockController(IMqttService mqttService) {
        this.mqttService = mqttService;
    }

    @ApiOperation("向电动车发送控制指令")
    @GetMapping("")
    public ResponseEntity<Void> emitCommand(
            @ApiParam("控制指令 1关锁 2开锁 3启动") @RequestParam int id) {
        Command cmd;
        switch (id) {
            case 1:
                cmd = Command.LOCK;
                break;
            case 2:
                cmd = Command.UNLOCK;
                break;
            case 3:
                cmd = Command.START_ENGINE;
                break;
            default:
                throw new NotSupportArgumentException(String.format("不支持的控制代码 %d", id));
        }
        mqttService.controlCar(cmd);
        return ResponseEntity.ok().build();
    }

}
