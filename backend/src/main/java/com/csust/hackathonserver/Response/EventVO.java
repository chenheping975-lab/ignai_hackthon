package com.csust.hackathonserver.Response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventVO {
    private long  id;
    private String title;
    private String subtitle;
    private String location;
    private String description;
    private boolean registrationOpen;
    private LocalDateTime registrationDeadline;
}
