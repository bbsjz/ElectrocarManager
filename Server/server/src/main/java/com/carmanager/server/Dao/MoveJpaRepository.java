package com.carmanager.server.Dao;

import com.carmanager.server.Entity.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoveJpaRepository extends JpaRepository<Move, Long> {
}
