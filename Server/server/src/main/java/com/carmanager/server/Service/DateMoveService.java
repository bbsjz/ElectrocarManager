package com.carmanager.server.Service;

import com.carmanager.server.Dao.DateMoveJPARepository;
import com.carmanager.server.Entity.DateMove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DateMoveService {

    @Autowired
    DateMoveJPARepository repository;

    public List<DateMove> getAll()
    {
        return repository.findAll();
    }
}
