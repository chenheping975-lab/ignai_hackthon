package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.mapper.EventsMapper;
import com.csust.hackathonserver.pojo.Events;
import com.csust.hackathonserver.service.EventsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventsImpl implements EventsService {
    @Autowired
    private EventsMapper eventsMapper;

    @Override
    public Events getCurrentEvent() {
        return eventsMapper.getCurrentEvent();
    }

    @Override
    public PageInfo<Events> listEvents(int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Events> list = eventsMapper.findAll();
        return new PageInfo<>(list);
    }

    @Override
    public Events findById(Long id) {
        return eventsMapper.findById(id);
    }

    @Override
    public int insert(Events event) {
        return eventsMapper.insert(event);
    }

    @Override
    public int update(Events event) {
        return eventsMapper.update(event);
    }

    @Override
    public int updateWindows(Events event) {
        return eventsMapper.updateWindows(event);
    }
}
