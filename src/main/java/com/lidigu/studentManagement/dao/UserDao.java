package com.lidigu.studentManagement.dao;

import com.lidigu.studentManagement.entity.User;

public interface UserDao {
    User findByUserName(String username);
}
