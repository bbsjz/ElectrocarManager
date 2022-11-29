package com.carmanager.server.Dao;

import com.carmanager.server.Entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 经纬度点的数据库管理
 */
@Repository
public interface PointJpaRepository extends JpaRepository<Point, Long> {

    @Query("FROM Point WHERE createTime=(SELECT MAX(createTime) FROM Point)")
    Point queryLatest();

    @Query("FROM Point WHERE createTime>=?1 AND createTime<=?2 ORDER BY createTime DESC")
    List<Point> selectPointByTime(Date startTime, Date endTime);

}
