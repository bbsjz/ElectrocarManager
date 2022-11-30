package com.carmanager.server.Service.impl;

import com.carmanager.server.Dao.MoveJpaRepository;
import com.carmanager.server.Entity.DateMove;
import com.carmanager.server.Entity.Move;
import com.carmanager.server.Service.IMoveService;
import com.carmanager.server.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MoveService implements IMoveService {

    final MoveJpaRepository repository;

    public MoveService(MoveJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Move saveMove(Move move) {
        Move pureMove = new Move(move); // transform dto to normal move
        return repository.save(pureMove);
    }

    @Override
    public Page<Move> getAllMove(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("beginTime").descending());
        return repository.findAll(pageable);
    }

    @Override
    public Move getMove(long id) {
        Optional<Move> move = repository.findById(id);
        if (move.isPresent()) {
            return move.get();
        }
        throw new NotFoundException(String.format("不存在Id为%d的Move对象", id));
    }
}
