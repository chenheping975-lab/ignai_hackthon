package com.csust.hackathonserver.service.impl;

import com.csust.hackathonserver.mapper.EventsMapper;
import com.csust.hackathonserver.pojo.Events;
import com.csust.hackathonserver.service.EventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventsImpl implements EventsService {
    @Autowired
    private EventsMapper eventsMapper;
    @Override
    public Events getCurrentEvent(){
         return eventsMapper.getCurrentEvent();
    }
}
