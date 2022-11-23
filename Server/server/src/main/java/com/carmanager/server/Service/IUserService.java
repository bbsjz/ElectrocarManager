package com.carmanager.server.Service;

import com.carmanager.server.Entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 与用户相关的管理服务，提供用户信息添加，查询
 */
@Service
public interface IUserService {

    User addUser(User user);

    void deleteUser(String name);

    User getUser(String name);

    User updateUser(String name, User user);

    List<User> findAllUser();

}
