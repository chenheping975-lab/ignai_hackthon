package com.csust.hackathonserver.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Votes {
    private Long id;
    private Long projectId;
    private Long voterUserId;
    private String voterKey;
    private Object sourceType;
    private LocalDateTime createdAt;
}
