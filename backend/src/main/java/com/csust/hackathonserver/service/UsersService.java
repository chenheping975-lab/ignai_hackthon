package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.Users;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsersService {
    void register(Users user);

    int login(Users user);

    Users getByAccount(String account);

    int countByRole(String role);

    Users findOneByRole(String role);

    Users findById(Long id);

    List<Users> findAll();

    int updatePassword(Long id, String passwordHash);

    int updateEmail(Long id, String email);

    int updateStatus(Long id, String status);
}
