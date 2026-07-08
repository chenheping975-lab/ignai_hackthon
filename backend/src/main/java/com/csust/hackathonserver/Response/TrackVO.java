package com.csust.hackathonserver.Response;

import lombok.Data;

@Data
public class TrackVO {
    private Long id;
    private Long eventId;
    private String name;
    private String description;
    private Integer sortOrder;
}
