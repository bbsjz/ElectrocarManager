package com.carmanager.server.controller;

import com.carmanager.server.Entity.Point;
import com.carmanager.server.Service.IPointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Api("历史轨迹数据管理")
@RestController
@RequestMapping("point")
public class PointController {

    final IPointService pointService;

    public PointController(IPointService pointService) {
        this.pointService = pointService;
    }

    @ApiOperation("按时间顺序获取历史数据")
    @GetMapping("/select")
    public List<Point> getPointByTime(@ApiParam("开始时间") @RequestParam Date startTime,
                                @ApiParam("结束时间") @RequestParam Date endTime) {
        return pointService.selectPointByTime(startTime, endTime);
    }

    @ApiOperation("按移动的Id获取历史数据")
    @GetMapping("/{id}")
    public List<Point> getPointByMoveId(@ApiParam("移动的Id") @PathVariable long id) {
        return pointService.selectPointByMoveId(id);
    }

}
