package com.carmanager.server.Service.impl;

import com.carmanager.server.Entity.Point;
import com.carmanager.server.Service.IPointService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PointService implements IPointService {
    @Override
    public Point addPoint(Point point) {
        return null;
    }

    @Override
    public Point getLatestPoint() {
        return null;
    }

    @Override
    public List<Point> selectPointByTime(Date startTime, Date endTime) {
        return null;
    }

    @Override
    public List<Point> selectPointByMoveId(long moveId) {
        return null;
    }
}
