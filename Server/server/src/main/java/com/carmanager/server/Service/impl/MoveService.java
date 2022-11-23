package com.carmanager.server.Service.impl;

import com.carmanager.server.Dao.DateMoveJpaRepository;
import com.carmanager.server.Entity.DateMove;
import com.carmanager.server.Entity.Move;
import com.carmanager.server.Service.IMoveService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MoveService implements IMoveService {

    final DateMoveJpaRepository repository;

    public MoveService(DateMoveJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Move addMove(Move move) {
        return null;
    }

    @Override
    public DateMove addDateMove(DateMove dateMove) {
        return null;
    }

    @Override
    public List<DateMove> getDateMoveByTime(Date startTime, Date endTime) {
        return null;
    }
}
