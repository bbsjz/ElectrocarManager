package com.carmanager.server.Service;

import com.carmanager.server.Entity.Point;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface IPointService {

    Point addPoint(Point point);

    Point getLatestPoint();

    List<Point> selectPointByTime(Date startTime, Date endTime);

}
