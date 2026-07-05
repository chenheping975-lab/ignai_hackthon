package com.csust.hackathonserver.Response;


import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private UserVO user;
}