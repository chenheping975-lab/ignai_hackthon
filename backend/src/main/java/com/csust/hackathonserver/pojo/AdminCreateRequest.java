package com.csust.hackathonserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
}
