package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.Projects;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectsService {
    List<Projects> getPublicProjects(String status, Integer pageSize);
}
