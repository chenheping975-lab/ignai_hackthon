package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.Events;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

@Service
public interface EventsService {
    Events getCurrentEvent();

    PageInfo<Events> listEvents(int page, int pageSize);

    Events findById(Long id);

    int insert(Events event);

    int update(Events event);

    int updateWindows(Events event);
}
