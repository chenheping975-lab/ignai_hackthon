package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.mapper.TracksMapper;
import com.csust.hackathonserver.pojo.Tracks;
import com.csust.hackathonserver.service.TracksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TracksImpl implements TracksService {
    @Autowired
    private TracksMapper tracksMapper;

    @Override
    public List<Tracks> findByEventId(Long eventId) {
        return tracksMapper.findByEventId(eventId);
    }

    @Override
    public Tracks findById(Long id) {
        return tracksMapper.findById(id);
    }

    @Override
    public int insert(Tracks track) {
        return tracksMapper.insert(track);
    }
}
