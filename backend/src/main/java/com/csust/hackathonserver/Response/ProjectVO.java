package com.csust.hackathonserver.Response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProjectVO {
    private Long id;
    private String title;
    private Long trackId;
    private String trackName;
    private String tagline;
    private String description;
    private String coverUrl;
}
