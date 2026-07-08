package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.Projects;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectsService {
    List<Projects> getPublicProjects(String status);

    int countAll();

    int countByStatus(String status);

    List<Projects> findAll();

    Projects findById(Long id);

    int updateStatus(Long id, String status);

    Projects insert(Projects project);

    int update(Projects project);

    int submit(Long id);

    Projects findByTeamId(Long teamId);

    List<Projects> findByUserId(Long userId);
}
