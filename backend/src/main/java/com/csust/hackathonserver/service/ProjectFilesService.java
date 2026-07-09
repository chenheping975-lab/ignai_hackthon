package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.ProjectFiles;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectFilesService {
    ProjectFiles insert(ProjectFiles file);

    List<ProjectFiles> findByProjectId(Long projectId);
}
