package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.mapper.ProjectsMapper;
import com.csust.hackathonserver.pojo.Projects;
import com.csust.hackathonserver.service.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectsImpl implements ProjectsService {
    @Autowired
    private ProjectsMapper projectsMapper;
    @Override
    public List<Projects> getPublicProjects(String status, Integer pageSize) {
        return projectsMapper.getPublicProjects(status,pageSize);
    }
}
