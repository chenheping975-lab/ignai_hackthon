package com.csust.hackathonserver.controller;

import com.csust.hackathonserver.Response.Result;
import com.csust.hackathonserver.Response.UserVO;
import com.csust.hackathonserver.Utils.JwtUtil;
import com.csust.hackathonserver.pojo.AdminCreateRequest;
import com.csust.hackathonserver.pojo.Events;
import com.csust.hackathonserver.pojo.Projects;
import com.csust.hackathonserver.pojo.Registrations;
import com.csust.hackathonserver.pojo.Tracks;
import com.csust.hackathonserver.pojo.Users;
import com.csust.hackathonserver.service.EventsService;
import com.csust.hackathonserver.service.ProjectsService;
import com.csust.hackathonserver.service.RegistrationsService;
import com.csust.hackathonserver.service.TracksService;
import com.csust.hackathonserver.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private RegistrationsService registrationsService;

    @Autowired
    private ProjectsService projectsService;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private TracksService tracksService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // ==================== Dashboard ====================

    @GetMapping("/dashboard")
    public Result dashboard(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问，需要管理员权限");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", usersService.findAll().size());
        data.put("totalRegistrations", registrationsService.countAll());
        data.put("pendingRegistrations", registrationsService.countByStatus("pending"));
        data.put("approvedRegistrations", registrationsService.countByStatus("approved"));
        data.put("totalProjects", projectsService.countAll());
        return Result.ok(data);
    }

    // ==================== 用户管理 ====================

    @PostMapping("/users/admins")
    public Result createAdmin(@RequestBody AdminCreateRequest req, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        if (req.getName() == null || req.getEmail() == null || req.getPassword() == null) {
            return Result.fail("PARAM_INVALID", "姓名、邮箱和密码不能为空");
        }
        if (req.getPassword().length() < 8) {
            return Result.fail("PARAM_INVALID", "密码至少 8 位");
        }

        Users admin = new Users();
        admin.setRole("admin");
        admin.setName(req.getName());
        admin.setEmail(req.getEmail());
        admin.setPhone(req.getPhone());
        admin.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        usersService.register(admin);
        log.info("管理员账号创建成功: {}", req.getEmail());
        return Result.ok("管理员创建成功");
    }

    @GetMapping("/users")
    public Result listUsers(@RequestParam(value = "role", required = false) String role,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        List<Users> allUsers = usersService.findAll();

        // 简单过滤
        List<UserVO> result = new ArrayList<>();
        for (Users u : allUsers) {
            // 过滤角色
            if (role != null && !role.isEmpty() && !role.equals(u.getRole().toString())) {
                continue;
            }
            // 过滤关键字（匹配姓名、邮箱、手机号）
            if (keyword != null && !keyword.isEmpty()) {
                boolean match = false;
                if (u.getName() != null && u.getName().contains(keyword)) match = true;
                if (u.getEmail() != null && u.getEmail().contains(keyword)) match = true;
                if (u.getPhone() != null && u.getPhone().contains(keyword)) match = true;
                if (!match) continue;
            }
            result.add(new UserVO(u.getName(), u.getEmail(), u.getPhone(), (String) u.getRole()));
        }
        return Result.ok(result);
    }

    @GetMapping("/users/{userId}")
    public Result getUserDetail(@PathVariable Long userId, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        Users user = usersService.findById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        UserVO vo = new UserVO(user.getName(), user.getEmail(), user.getPhone(), (String) user.getRole());
        return Result.ok(vo);
    }

    @PatchMapping("/users/{userId}/status")
    public Result updateUserStatus(@PathVariable Long userId,
                                   @RequestBody Map<String, String> body,
                                   HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        String status = body.get("status");
        if (status == null || (!status.equals("active") && !status.equals("disabled"))) {
            return Result.fail("PARAM_INVALID", "status 必须为 active 或 disabled");
        }

        Users user = usersService.findById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        usersService.updateStatus(userId, status);
        return Result.ok("状态更新成功");
    }

    // ==================== 活动管理 ====================

    @GetMapping("/events")
    public Result listEvents(HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }
        return Result.ok(eventsService.listEvents(1, 100).getList());
    }

    @PostMapping("/events")
    public Result createEvent(@RequestBody Events event, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        if (event.getTitle() == null || event.getTitle().isBlank()) {
            return Result.fail("PARAM_INVALID", "活动标题不能为空");
        }

        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        if (event.getStatus() == null) {
            event.setStatus("draft");
        }

        eventsService.insert(event);
        return Result.ok(event);
    }

    @PutMapping("/events/{eventId}")
    public Result updateEvent(@PathVariable Long eventId,
                              @RequestBody Events event,
                              HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        Events existing = eventsService.findById(eventId);
        if (existing == null) {
            return Result.fail("活动不存在");
        }

        event.setId(eventId);
        eventsService.update(event);
        return Result.ok("活动更新成功");
    }

    @PatchMapping("/events/{eventId}/windows")
    public Result updateEventWindows(@PathVariable Long eventId,
                                     @RequestBody Events event,
                                     HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        Events existing = eventsService.findById(eventId);
        if (existing == null) {
            return Result.fail("活动不存在");
        }

        event.setId(eventId);
        eventsService.updateWindows(event);
        return Result.ok("时间窗口设置成功");
    }

    // ==================== 报名管理 ====================

    @GetMapping("/registrations")
    public Result listRegistrations(@RequestParam(value = "status", required = false) String status,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        List<Registrations> list;
        if (status != null && !status.isEmpty()) {
            list = registrationsService.findByStatus(status);
        } else {
            list = registrationsService.findAll();
        }

        // 转换为简单 Map
        List<Map<String, Object>> result = new ArrayList<>();
        for (Registrations r : list) {
            // 关键字过滤
            if (keyword != null && !keyword.isEmpty()) {
                boolean match = false;
                if (r.getContactName() != null && r.getContactName().contains(keyword)) match = true;
                if (r.getTeamName() != null && r.getTeamName().contains(keyword)) match = true;
                if (!match) continue;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("eventId", r.getEventId());
            item.put("userId", r.getUserId());
            item.put("registrationType", r.getRegistrationType());
            item.put("teamName", r.getTeamName());
            item.put("contactName", r.getContactName());
            item.put("contactPhone", r.getContactPhone());
            item.put("contactEmail", r.getContactEmail());
            item.put("trackId", r.getTrackId());
            item.put("status", r.getStatus());
            item.put("createdAt", r.getCreatedAt());
            result.add(item);
        }
        return Result.ok(result);
    }

    @GetMapping("/registrations/{registrationId}")
    public Result getRegistrationDetail(@PathVariable Long registrationId, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        Registrations r = registrationsService.findById(registrationId);
        if (r == null) {
            return Result.fail("报名记录不存在");
        }

        Map<String, Object> item = new HashMap<>();
        item.put("id", r.getId());
        item.put("eventId", r.getEventId());
        item.put("userId", r.getUserId());
        item.put("registrationType", r.getRegistrationType());
        item.put("teamName", r.getTeamName());
        item.put("contactName", r.getContactName());
        item.put("contactPhone", r.getContactPhone());
        item.put("contactEmail", r.getContactEmail());
        item.put("trackId", r.getTrackId());
        item.put("status", r.getStatus());
        item.put("aiTags", r.getAiTagsJson());
        item.put("aiSummary", r.getAiSummary());
        item.put("reviewNote", r.getReviewNote());
        item.put("createdAt", r.getCreatedAt());
        return Result.ok(item);
    }

    @PatchMapping("/registrations/{registrationId}/review")
    public Result reviewRegistration(@PathVariable Long registrationId,
                                     @RequestBody Map<String, Object> body,
                                     HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        String status = (String) body.get("status");
        if (status == null || (!status.equals("approved") && !status.equals("rejected") && !status.equals("waitlist"))) {
            return Result.fail("PARAM_INVALID", "status 必须为 approved、rejected 或 waitlist");
        }

        String note = (String) body.getOrDefault("note", "");

        Registrations r = registrationsService.findById(registrationId);
        if (r == null) {
            return Result.fail("报名记录不存在");
        }

        registrationsService.updateStatus(registrationId, status, note);
        return Result.ok("审核完成");
    }

    @PostMapping("/registrations/batch-review")
    public Result batchReview(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("registrationIds");
        String status = (String) body.get("status");
        String note = (String) body.getOrDefault("note", "");

        if (ids == null || ids.isEmpty()) {
            return Result.fail("PARAM_INVALID", "registrationIds 不能为空");
        }
        if (status == null || (!status.equals("approved") && !status.equals("rejected") && !status.equals("waitlist"))) {
            return Result.fail("PARAM_INVALID", "status 必须为 approved、rejected 或 waitlist");
        }

        for (Integer id : ids) {
            registrationsService.updateStatus(id.longValue(), status, note);
        }

        return Result.ok("批量审核完成，共处理 " + ids.size() + " 条");
    }

    // ==================== 赛道管理 ====================

    @GetMapping("/events/{eventId}/tracks")
    public Result listTracks(@PathVariable Long eventId, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }
        List<Tracks> tracks = tracksService.findByEventId(eventId);
        return Result.ok(tracks);
    }

    @PostMapping("/events/{eventId}/tracks")
    public Result createTrack(@PathVariable Long eventId,
                              @RequestBody Tracks track,
                              HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }
        if (track.getName() == null || track.getName().isBlank()) {
            return Result.fail("PARAM_INVALID", "赛道名称不能为空");
        }
        track.setEventId(eventId);
        track.setCreatedAt(java.time.LocalDateTime.now());
        track.setUpdatedAt(java.time.LocalDateTime.now());
        if (track.getSortOrder() == null) {
            track.setSortOrder(0);
        }
        tracksService.insert(track);
        return Result.ok(track);
    }

    // ==================== 后台项目管理 ====================

    @GetMapping("/projects")
    public Result listProjects(@RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        List<Projects> list;
        if (status != null && !status.isEmpty()) {
            list = projectsService.findAll().stream()
                    .filter(p -> status.equals(p.getStatus()))
                    .toList();
        } else {
            list = projectsService.findAll();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Projects p : list) {
            if (keyword != null && !keyword.isEmpty()) {
                if (p.getTitle() == null || !p.getTitle().contains(keyword)) continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("eventId", p.getEventId());
            item.put("teamId", p.getTeamId());
            item.put("title", p.getTitle());
            item.put("tagline", p.getTagline());
            item.put("status", p.getStatus());
            item.put("createdAt", p.getCreatedAt());
            result.add(item);
        }
        return Result.ok(result);
    }

    @GetMapping("/projects/{projectId}")
    public Result getProjectDetail(@PathVariable Long projectId, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }
        Projects p = projectsService.findById(projectId);
        if (p == null) {
            return Result.fail("作品不存在");
        }
        return Result.ok(p);
    }

    @PatchMapping("/projects/{projectId}/status")
    public Result updateProjectStatus(@PathVariable Long projectId,
                                      @RequestBody Map<String, String> body,
                                      HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }
        String status = body.get("status");
        if (status == null) {
            return Result.fail("PARAM_INVALID", "status 不能为空");
        }
        projectsService.updateStatus(projectId, status);
        return Result.ok("项目状态更新成功");
    }

    @PatchMapping("/projects/{projectId}/showcase")
    public Result updateProjectShowcase(@PathVariable Long projectId,
                                        @RequestBody Map<String, Object> body,
                                        HttpServletRequest request) {
        if (!isAdmin(request)) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }
        return Result.ok("展示规则已保存");
    }

    // ==================== 权限校验 ====================

    private boolean isAdmin(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authHeader.substring(7);
        try {
            Long userId = jwtUtil.getUserId(token);
            Users user = usersService.findById(userId);
            if (user == null) return false;
            String role = (String) user.getRole();
            return "root".equals(role) || "admin".equals(role);
        } catch (Exception e) {
            return false;
        }
    }
}
