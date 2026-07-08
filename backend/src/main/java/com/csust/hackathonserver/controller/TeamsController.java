package com.csust.hackathonserver.controller;

import com.csust.hackathonserver.Response.Result;
import com.csust.hackathonserver.Utils.JwtUtil;
import com.csust.hackathonserver.pojo.Events;
import com.csust.hackathonserver.pojo.Teams;
import com.csust.hackathonserver.service.EventsService;
import com.csust.hackathonserver.service.TeamsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
public class TeamsController {

    @Autowired
    private TeamsService teamsService;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/teams")
    public Result createOrJoinTeam(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "请先登录");
        }

        String teamName = (String) body.get("teamName");
        if (teamName == null || teamName.isBlank()) {
            return Result.fail("PARAM_INVALID", "队伍名称不能为空");
        }

        // 获取当前活动
        Events currentEvent = eventsService.getCurrentEvent();
        if (currentEvent == null) {
            return Result.fail("暂无进行中的活动");
        }

        // 检查队名是否已存在
        Teams existing = teamsService.findByEventIdAndName(currentEvent.getId(), teamName);
        if (existing != null) {
            return Result.fail("队伍名称已存在");
        }

        // 创建队伍
        Teams team = new Teams();
        team.setEventId(currentEvent.getId());
        team.setTeamName(teamName);
        team.setLeaderUserId(userId);
        team.setStatus("forming");
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());

        teamsService.createTeam(team);

        Map<String, Object> data = new HashMap<>();
        data.put("id", team.getId());
        data.put("teamName", team.getTeamName());
        data.put("status", team.getStatus());
        return Result.ok(data);
    }

    @GetMapping("/teams/me")
    public Result getMyTeam(HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "请先登录");
        }

        List<Teams> teams = teamsService.findByUserId(userId);
        if (teams.isEmpty()) {
            return Result.ok(null);
        }

        Teams team = teams.get(0);
        Map<String, Object> data = new HashMap<>();
        data.put("id", team.getId());
        data.put("eventId", team.getEventId());
        data.put("teamName", team.getTeamName());
        data.put("leaderUserId", team.getLeaderUserId());
        data.put("status", team.getStatus());
        return Result.ok(data);
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
