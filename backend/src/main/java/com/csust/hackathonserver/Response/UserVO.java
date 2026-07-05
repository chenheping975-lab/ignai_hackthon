package com.csust.hackathonserver.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private String name;
    private String email;
    private String phone;
    private String role;
}
