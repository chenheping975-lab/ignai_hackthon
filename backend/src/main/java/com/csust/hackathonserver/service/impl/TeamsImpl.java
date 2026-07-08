package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.mapper.TeamsMapper;
import com.csust.hackathonserver.pojo.Teams;
import com.csust.hackathonserver.service.TeamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamsImpl implements TeamsService {
    @Autowired
    private TeamsMapper teamsMapper;

    @Override
    public Teams createTeam(Teams team) {
        teamsMapper.insert(team);
        return team;
    }

    @Override
    public Teams findById(Long id) {
        return teamsMapper.findById(id);
    }

    @Override
    public List<Teams> findByUserId(Long userId) {
        return teamsMapper.findByLeaderUserId(userId);
    }

    @Override
    public Teams findByEventIdAndName(Long eventId, String teamName) {
        return teamsMapper.findByEventIdAndName(eventId, teamName);
    }

    @Override
    public List<Teams> findByEventId(Long eventId) {
        return teamsMapper.findByEventId(eventId);
    }
}
