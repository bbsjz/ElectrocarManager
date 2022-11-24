package com.carmanager.server.controller;

import com.carmanager.server.Entity.Point;
import com.carmanager.server.Service.IPointService;
import com.carmanager.server.exception.NotSupportArgumentException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Api("历史数据管理")
@RestController
@RequestMapping("point")
public class PointController {

    final IPointService pointService;

    public PointController(IPointService pointService) {
        this.pointService = pointService;
    }

    @ApiOperation("按时间顺序获取历史数据")
    @GetMapping("")
    public List<Point> getPoint(@RequestParam Date startTime,
                                @RequestParam Date endTime) {
        return pointService.selectPointByTime(startTime, endTime);
    }

}
