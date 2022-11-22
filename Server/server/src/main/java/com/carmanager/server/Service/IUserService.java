package com.carmanager.server.Service;

import com.carmanager.server.Entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {

    User addUser(User user);

    void deleteUser(String name);

    User getUser(String name);

    User updateUser(String name, User user);

    List<User> findAllUser();

}
