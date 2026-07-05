package com.csust.hackathonserver.controller;

import com.csust.hackathonserver.Response.LoginResponse;
import com.csust.hackathonserver.Response.Result;
import com.csust.hackathonserver.Response.UserVO;
import com.csust.hackathonserver.Utils.JwtUtil;
import com.csust.hackathonserver.pojo.LoginRequest;
import com.csust.hackathonserver.pojo.RegisterRequest;
import com.csust.hackathonserver.pojo.Users;
import com.csust.hackathonserver.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UsersService usersService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtil jwtUtil;
    @PostMapping("/register")
    public Result register(@RequestBody RegisterRequest request) {
        Users user = new Users();
        user.setRole("participant");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        usersService.register(user);
        return Result.ok();
    }
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request){
        Users user = usersService.getByAccount(request.getAccount());
        if(user == null){
            return Result.fail("用户名或密码错误，请重新输入");
        }
        boolean matched=passwordEncoder.
                matches(request.getPassword(),
                        user.getPasswordHash());
        if(!matched){
            return Result.fail("用户名或密码错误，请重新输入");
        }
        String token=jwtUtil.generateToken(user.getId(), user.getRole().toString());
        LoginResponse response=new LoginResponse();
        response.setAccessToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(7200L);
        UserVO userVO=new UserVO(user.getName(), user.getEmail(), user.getPhone(), (String) user.getRole());
        response.setUser(userVO);
        return Result.ok(response);
    }
}
