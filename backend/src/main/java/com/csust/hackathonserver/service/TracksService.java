package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.Tracks;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TracksService {
    List<Tracks> findByEventId(Long eventId);
    Tracks findById(Long id);
    int insert(Tracks track);
}
