package com.carmanager.server.Service.impl;

import com.carmanager.server.Dao.PointJpaRepository;
import com.carmanager.server.Entity.Move;
import com.carmanager.server.Entity.Point;
import com.carmanager.server.Service.IPointService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PointService implements IPointService {

    final PointJpaRepository repository;

    final MoveService moveService;

    public PointService(PointJpaRepository repository, MoveService moveService) {
        this.repository = repository;
        this.moveService = moveService;
    }

    @Override
    public Point addPoint(Point point) {
        return repository.save(point);
    }

    @Override
    public Point getLatestPoint() {
        return repository.queryLatest();
    }

    @Override
    public List<Point> selectPointByTime(Date startTime, Date endTime) {
        return repository.selectPointByTime(startTime, endTime);
    }

    @Override
    public List<Point> selectPointByMoveId(long moveId) {
        Move move = moveService.getMove(moveId);
        return selectPointByTime(move.getBeginTime(), move.getEndTime());
    }
}
