package com.carmanager.server.Dao;

import com.carmanager.server.Entity.DateMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DateMoveJPARepository extends JpaRepository<DateMove, String> {
}
