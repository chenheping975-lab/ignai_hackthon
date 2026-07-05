package com.csust.hackathonserver.controller;

import com.csust.hackathonserver.Response.EventVO;
import com.csust.hackathonserver.Response.ProjectVO;
import com.csust.hackathonserver.Response.Result;
import com.csust.hackathonserver.mapper.EventsMapper;
import com.csust.hackathonserver.pojo.Events;
import com.csust.hackathonserver.pojo.Projects;
import com.csust.hackathonserver.service.EventsService;
import com.csust.hackathonserver.service.ProjectsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/public")
public class PublicController {
    @Autowired
    private EventsService eventsService;
    @Autowired
    private ProjectsService projectsService;
    @GetMapping("/events/current")
    public Result getCurrentEvent(){
        Events currentEvent = eventsService.getCurrentEvent();
        if(currentEvent == null){
            return Result.fail("暂时还没有符合条件的活动，敬请期待");
        }
        EventVO eventVO = new EventVO();
        eventVO.setId(currentEvent.getId());
        eventVO.setTitle(currentEvent.getTitle());
        eventVO.setSubtitle(currentEvent.getSubtitle());
        eventVO.setLocation(currentEvent.getLocation());
        eventVO.setDescription(currentEvent.getDescription());
        if(Integer.valueOf(1).equals(currentEvent.getRegistrationOpen())) {
            eventVO.setRegistrationOpen(true);
        }else{
            eventVO.setRegistrationOpen(false);
        }
        eventVO.setRegistrationDeadline(currentEvent.getRegistrationDeadline());
        return Result.ok(eventVO);
    }
    @GetMapping("/projects")
    public Result getPublicProjects(@RequestParam(value = "status", required = false) String status,
                              @RequestParam(value = "pageSize",required = false) Integer pageSize){
        List<Projects> projects= projectsService.getPublicProjects(status,pageSize);
        if(projects.size() == 0){
            return Result.fail("暂无公开作品，敬请期待");
        }
        List<ProjectVO> projectVOS = new ArrayList<ProjectVO>();
        projects.forEach(project->{
            ProjectVO projectVO = new ProjectVO();
            projectVO.setId(project.getId());
            projectVO.setTitle(project.getTitle());
            projectVO.setTrackId(project.getTrackId());
            projectVO.setTagline(project.getTagline());
            projectVO.setDescription(project.getDescription());
            projectVO.setCoverUrl(project.getDemoUrl());
            projectVOS.add(projectVO);
        });
        return Result.ok(projectVOS);

}
}
