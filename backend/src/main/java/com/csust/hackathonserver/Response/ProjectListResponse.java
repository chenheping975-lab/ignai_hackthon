package com.csust.hackathonserver.Response;

import lombok.Data;

import java.util.List;

@Data
public class ProjectListResponse {
    private long total;
    private List<ProjectVO> projects;
}
