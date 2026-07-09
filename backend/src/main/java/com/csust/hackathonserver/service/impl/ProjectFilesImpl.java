package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.mapper.ProjectFilesMapper;
import com.csust.hackathonserver.pojo.ProjectFiles;
import com.csust.hackathonserver.service.ProjectFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectFilesImpl implements ProjectFilesService {
    @Autowired
    private ProjectFilesMapper projectFilesMapper;

    @Override
    public ProjectFiles insert(ProjectFiles file) {
        projectFilesMapper.insert(file);
        return file;
    }

    @Override
    public List<ProjectFiles> findByProjectId(Long projectId) {
        return projectFilesMapper.findByProjectId(projectId);
    }
}
