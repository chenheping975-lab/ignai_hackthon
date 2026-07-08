package com.csust.hackathonserver.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 报名响应 VO，返回给前端
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationVO {
    private Long id;
    private Long eventId;
    private Long userId;
    private String registrationType;
    private String teamName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Long trackId;
    private String status;
    private LocalDateTime createdAt;
}
