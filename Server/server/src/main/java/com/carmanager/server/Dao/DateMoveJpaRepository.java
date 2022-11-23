package com.carmanager.server.Dao;

import com.carmanager.server.Entity.DateMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 以天为单位的数据库管理
 */
@Repository
public interface DateMoveJpaRepository extends JpaRepository<DateMove, String> {
}
