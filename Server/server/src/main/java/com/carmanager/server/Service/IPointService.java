package com.carmanager.server.Service;

import com.carmanager.server.Entity.Point;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 与移动路径（点）相关的数据库管理服务，提供位置点阵查询，添加的服务
 */
@Service
public interface IPointService {

    Point addPoint(Point point);

    Point getLatestPoint();

    List<Point> selectPointByTime(Date startTime, Date endTime);

    List<Point> selectPointByMoveId(long moveId);

}
