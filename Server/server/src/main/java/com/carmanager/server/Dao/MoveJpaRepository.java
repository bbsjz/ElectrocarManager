package com.carmanager.server.Dao;

import com.carmanager.server.Entity.Move;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * 以单条记录为单位的数据库管理
 */
@Repository
public interface MoveJpaRepository extends PagingAndSortingRepository<Move, Long> {
    Page<Move> findAll(Pageable pageable);
}
