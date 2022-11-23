package com.carmanager.server.Dao;

import com.carmanager.server.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户账号数据库管理
 */
@Repository
public interface UserJpaRepository extends JpaRepository<User, String> {
}
