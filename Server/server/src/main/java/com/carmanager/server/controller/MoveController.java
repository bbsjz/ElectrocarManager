package com.carmanager.server.controller;

import com.carmanager.server.Entity.Move;
import com.carmanager.server.Service.IMoveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api("移动记录管理")
@RestController
@RequestMapping("move")
public class MoveController {

    final IMoveService moveService;

    public MoveController(IMoveService moveService) {
        this.moveService = moveService;
    }

    @ApiOperation("按时间倒序获取车辆移动信息")
    @GetMapping("")
    public Page<Move> getMove(@RequestParam(defaultValue = "0") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return moveService.getMove(pageNum, pageSize);
    }

}
