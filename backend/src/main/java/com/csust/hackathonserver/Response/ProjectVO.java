package com.csust.hackathonserver.Response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ProjectVO {
    private Long id;
    private Long eventId;
    private String title;
    private Long trackId;
    private String trackName;
    private String tagline;
    private String description;
    private String coverUrl;
    private String status;
    private LocalDateTime submittedAt;
    private Integer votes;
    private List<Map<String, Object>> files;
}
