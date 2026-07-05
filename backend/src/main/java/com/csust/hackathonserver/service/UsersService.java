package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.Users;
import org.springframework.stereotype.Service;

@Service
public interface UsersService {
    void register(Users user);

    int login(Users user);

    Users getByAccount(String account);
}
