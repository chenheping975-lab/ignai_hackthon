package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.mapper.UsersMapper;
import com.csust.hackathonserver.pojo.Users;
import com.csust.hackathonserver.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public int countByRole(String role) {
        return usersMapper.countByRole(role);
    }

    @Override
    public Users findOneByRole(String role) {
        return usersMapper.findOneByRole(role);
    }

    @Override
    public Users findById(Long id) {
        return usersMapper.findById(id);
    }

    @Override
    public List<Users> findAll() {
        return usersMapper.findAll();
    }

    @Override
    public int updatePassword(Long id, String passwordHash) {
        return usersMapper.updatePassword(id, passwordHash);
    }

    @Override
    public int updateEmail(Long id, String email) {
        return usersMapper.updateEmail(id, email);
    }

    @Override
    public int updateStatus(Long id, String status) {
        return usersMapper.updateStatus(id, status);
    }
}
