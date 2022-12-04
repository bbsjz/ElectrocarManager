package com.carmanager.server.controller;

import com.carmanager.server.Entity.Move;
import com.carmanager.server.Service.IMoveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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
    public Page<Move> getAllMove(@RequestParam(defaultValue = "0") Integer pageNum,
                       @RequestParam(defaultValue = "5") Integer pageSize) {
        return moveService.getAllMove(pageNum, pageSize);
    }

    @ApiOperation("按移动的Id获取移动信息")
    @GetMapping("/{id}")
    public Move getMove(@PathVariable long id) {
        return moveService.getMove(id);
    }

}
