package com.csust.hackathonserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerEmailChangeRequest {
    private String password;
    private String ownerEmail;
}
