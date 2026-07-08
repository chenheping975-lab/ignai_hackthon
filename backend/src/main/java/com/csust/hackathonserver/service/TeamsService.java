package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.Teams;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TeamsService {
    Teams createTeam(Teams team);
    Teams findById(Long id);
    List<Teams> findByUserId(Long userId);
    Teams findByEventIdAndName(Long eventId, String teamName);
    List<Teams> findByEventId(Long eventId);
}
