package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.mapper.UsersMapper;
import com.csust.hackathonserver.pojo.Users;
import com.csust.hackathonserver.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersImpl implements UsersService {
    @Autowired
    UsersMapper usersMapper;
    @Override
    public void register(Users user) {
        usersMapper.register(user);
    }

    @Override
    public int login(Users user) {
        return usersMapper.login(user);
    }

    @Override
    public Users getByAccount(String account) {
        return usersMapper.getByAccount(account);
    }
}
