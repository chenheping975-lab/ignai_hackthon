package com.csust.hackathonserver.controller;

import com.csust.hackathonserver.Response.EventConfigRequest;
import com.csust.hackathonserver.Response.EventDetailVO;
import com.csust.hackathonserver.Response.Result;
import com.csust.hackathonserver.service.EventsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/admin/events")
public class AdminEventController {
    @Autowired
    private EventsService eventsService;

    @GetMapping("/current/config")
    public Result getCurrentEventConfig() {
        EventDetailVO event = eventsService.getCurrentEventDetail();
        if (event == null) {
            return Result.fail("暂时还没有可配置的活动");
        }
        return Result.ok(event);
    }

    @GetMapping("/{eventId}/config")
    public Result getEventConfig(@PathVariable Long eventId) {
        EventDetailVO event = eventsService.getEventDetail(eventId);
        if (event == null) {
            return Result.fail("活动不存在");
        }
        return Result.ok(event);
    }

    @PutMapping("/{eventId}/config")
    public Result saveEventConfig(@PathVariable Long eventId,
                                  @RequestBody EventConfigRequest request) {
        EventDetailVO event = eventsService.saveEventConfig(eventId, request);
        if (event == null) {
            return Result.fail("活动不存在或请求为空");
        }
        return Result.ok("活动配置已保存", event);
    }
}
