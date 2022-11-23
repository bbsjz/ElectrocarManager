package com.carmanager.server.Dao;

import com.carmanager.server.Entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 经纬度点的数据库管理
 */
@Repository
public interface PointJpaRepository extends JpaRepository<Point, Long> {
}
