package com.csust.hackathonserver.controller;

import com.csust.hackathonserver.Response.Result;
import com.csust.hackathonserver.Utils.JwtUtil;
import com.csust.hackathonserver.pojo.Events;
import com.csust.hackathonserver.pojo.Projects;
import com.csust.hackathonserver.pojo.Teams;
import com.csust.hackathonserver.service.EventsService;
import com.csust.hackathonserver.service.ProjectsService;
import com.csust.hackathonserver.service.TeamsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
public class ProjectController {

    @Autowired
    private ProjectsService projectsService;

    @Autowired
    private TeamsService teamsService;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/projects")
    public Result createProject(@RequestBody Projects project, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "请先登录");
        }

        if (project.getTitle() == null || project.getTitle().isBlank()) {
            return Result.fail("PARAM_INVALID", "作品标题不能为空");
        }

        // 获取当前活动
        Events currentEvent = eventsService.getCurrentEvent();
        if (currentEvent == null) {
            return Result.fail("暂无进行中的活动");
        }

        // 查用户在当前活动下的队伍；个人参赛时自动创建一个默认队伍承载作品归属
        List<Teams> userTeams = teamsService.findByUserId(userId);
        Teams team = null;
        for (Teams item : userTeams) {
            if (currentEvent.getId().equals(item.getEventId())) {
                team = item;
                break;
            }
        }
        if (team == null) {
            team = new Teams();
            team.setEventId(currentEvent.getId());
            team.setTeamName("个人作品-" + userId);
            team.setLeaderUserId(userId);
            team.setStatus("forming");
            team.setCreatedAt(LocalDateTime.now());
            team.setUpdatedAt(LocalDateTime.now());
            teamsService.createTeam(team);
        }

        // 检查是否已创建过作品
        Projects existing = projectsService.findByTeamId(team.getId());
        if (existing != null) {
            return Result.fail("该队伍已创建过作品，请使用编辑功能");
        }

        project.setEventId(currentEvent.getId());
        project.setTeamId(team.getId());
        project.setStatus("draft");
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        projectsService.insert(project);
        return Result.ok(project);
    }

    @GetMapping("/projects/my")
    public Result getMyProject(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "请先登录");
        }

        List<Projects> projects = projectsService.findByUserId(userId);
        if (projects.isEmpty()) {
            return Result.ok(null);
        }

        return Result.ok(projects.get(0));
    }

    @PutMapping("/projects/{projectId}")
    public Result updateProject(@PathVariable Long projectId,
                                @RequestBody Projects project,
                                HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "请先登录");
        }

        Projects existing = projectsService.findById(projectId);
        if (existing == null) {
            return Result.fail("作品不存在");
        }
        Teams team = teamsService.findById(existing.getTeamId());
        if (team == null || !userId.equals(team.getLeaderUserId())) {
            return Result.fail("FORBIDDEN", "只能修改自己队伍的作品");
        }

        project.setId(projectId);
        projectsService.update(project);
        return Result.ok("作品更新成功");
    }

    @PostMapping("/projects/{projectId}/submit")
    public Result submitProject(@PathVariable Long projectId, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "请先登录");
        }

        Projects existing = projectsService.findById(projectId);
        if (existing == null) {
            return Result.fail("作品不存在");
        }
        Teams team = teamsService.findById(existing.getTeamId());
        if (team == null || !userId.equals(team.getLeaderUserId())) {
            return Result.fail("FORBIDDEN", "只能提交自己队伍的作品");
        }

        projectsService.submit(projectId);
        return Result.ok("作品提交成功");
    }

    @PostMapping("/projects/{projectId}/files")
    public Result uploadFile(@PathVariable Long projectId, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "请先登录");
        }

        Projects existing = projectsService.findById(projectId);
        if (existing == null) {
            return Result.fail("作品不存在");
        }

        // MVP 阶段文件上传暂不实现
        return Result.fail("NOT_IMPLEMENTED", "文件上传功能暂未实现");
    }

    private Long getUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        try {
            return jwtUtil.getUserId(header.substring(7));
        } catch (Exception e) {
            return null;
        }
    }
}
