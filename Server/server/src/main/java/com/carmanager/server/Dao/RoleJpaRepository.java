package com.carmanager.server.Dao;

import com.carmanager.server.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 角色管理
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<Role, String> {
}
