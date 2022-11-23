package com.carmanager.server.Service;

import com.carmanager.server.Entity.DateMove;
import com.carmanager.server.Entity.Move;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface IMoveService {

    Move addMove(Move move);

    DateMove addDateMove(DateMove dateMove);

    List<DateMove> getDateMoveByTime(Date startTime, Date endTime);

}
