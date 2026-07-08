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
    public List<Projects> getPublicProjects(String status) {
        return projectsMapper.getPublicProjects(status);
    }

    @Override
    public int countAll() {
        return projectsMapper.countAll();
    }

    @Override
    public int countByStatus(String status) {
        return projectsMapper.countByStatus(status);
    }

    @Override
    public List<Projects> findAll() {
        return projectsMapper.findAll();
    }

    @Override
    public Projects findById(Long id) {
        return projectsMapper.findById(id);
    }

    @Override
    public int updateStatus(Long id, String status) {
        return projectsMapper.updateStatus(id, status);
    }

    @Override
    public Projects insert(Projects project) {
        projectsMapper.insert(project);
        return project;
    }

    @Override
    public int update(Projects project) {
        return projectsMapper.update(project);
    }

    @Override
    public int submit(Long id) {
        return projectsMapper.submit(id);
    }

    @Override
    public Projects findByTeamId(Long teamId) {
        return projectsMapper.findByTeamId(teamId);
    }

    @Override
    public List<Projects> findByUserId(Long userId) {
        return projectsMapper.findByUserId(userId);
    }
}
