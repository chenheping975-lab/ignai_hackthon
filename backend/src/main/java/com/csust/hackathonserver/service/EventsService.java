package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.Events;
import org.springframework.stereotype.Service;

@Service
public interface EventsService {
    Events getCurrentEvent();
}
