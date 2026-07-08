package com.csust.hackathonserver.service;

import com.csust.hackathonserver.pojo.Events;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

@Service
public interface EventsService {
    Events getCurrentEvent();

    /**
     * 分页查询活动列表
     */
    PageInfo<Events> listEvents(int page, int pageSize);
}
