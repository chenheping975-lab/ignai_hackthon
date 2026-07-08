package com.csust.hackathonserver.controller;

import com.csust.hackathonserver.Response.RegistrationRequest;
import com.csust.hackathonserver.Response.RegistrationVO;
import com.csust.hackathonserver.Response.Result;
import com.csust.hackathonserver.Utils.JwtUtil;
import com.csust.hackathonserver.pojo.Events;
import com.csust.hackathonserver.pojo.Registrations;
import com.csust.hackathonserver.service.EventsService;
import com.csust.hackathonserver.service.RegistrationMembersService;
import com.csust.hackathonserver.service.RegistrationsService;
import com.csust.hackathonserver.service.TracksService;
import com.csust.hackathonserver.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 报名控制器 —— 处理活动报名相关请求
 */
@RestController
@Slf4j
@RequestMapping("/api")
public class RegistrationController {
    @Autowired
    UsersService usersService;
    @Autowired
    EventsService eventsService;
    @Autowired
    RegistrationMembersService registrationMembersService;
    @Autowired
    RegistrationsService registrationsService;
    @Autowired
    TracksService tracksService;
    @Autowired
    JwtUtil jwtUtil;

    /**
     * 提交报名
     * POST /api/registrations
     * Authorization: Bearer <accessToken>
     *
     * @param request  请求体，包含活动ID、报名类型、联系人等字段
     * @param srequest HTTP 请求对象，用于提取 JWT
     * @return Result 包含 RegistrationVO 或错误信息
     */
    @PostMapping("/registrations")
    public Result registration(@RequestBody RegistrationRequest request, HttpServletRequest srequest) {

        // ========== 1. 从请求头解析 JWT，获取当前登录用户 ID ==========
        String authHeader = srequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.fail("UNAUTHORIZED", "未登录或登录已失效，请重新登录");
        }
        String token = authHeader.substring(7); // 去掉 "Bearer " 前缀
        Long userId;
        try {
            userId = jwtUtil.getUserId(token);
        } catch (Exception e) {
            log.warn("JWT 解析失败: {}", e.getMessage());
            return Result.fail("UNAUTHORIZED", "登录已失效，请重新登录");
        }
        log.info("用户 {} 提交报名，活动ID: {}", userId, request.getEventId());

        // ========== 2. 校验活动是否存在且开放报名 ==========
        if (request.getEventId() == null) {
            return Result.fail("PARAM_INVALID", "活动ID不能为空");
        }
        Events event = eventsService.getCurrentEvent();
        if (event == null || !event.getId().equals(request.getEventId())) {
            return Result.fail("EVENT_NOT_FOUND", "活动不存在或已结束");
        }
        // 检查报名是否开放：registrationOpen = 1 表示开放，0 表示关闭
        if (event.getRegistrationOpen() == null || event.getRegistrationOpen() == 0) {
            return Result.fail("REGISTRATION_CLOSED", "报名未开放或已截止");
        }
        // 检查是否超过报名截止时间
        if (event.getRegistrationDeadline() != null
                && LocalDateTime.now().isAfter(event.getRegistrationDeadline())) {
            return Result.fail("REGISTRATION_CLOSED", "报名已截止");
        }

        // ========== 3. 检查重复报名：同一活动下同一用户只能报名一次 ==========
        Registrations existing = registrationsService.getByEventIdAndUserId(request.getEventId(), userId);
        if (existing != null) {
            return Result.fail("REGISTRATION_DUPLICATED", "你已经提交过报名");
        }

        // ========== 4. 构建 Registrations 实体并入库 ==========
        Registrations registration = new Registrations();
        registration.setEventId(request.getEventId());
        registration.setUserId(userId);
        registration.setRegistrationType(request.getRegistrationType()); // 当前前端固定传 "individual"
        registration.setTeamName(request.getTeamName());                 // 当前前端固定传 null
        registration.setContactName(request.getContactName());           // 来自 /api/auth/me 的用户姓名
        registration.setContactPhone(request.getContactPhone());         // 来自 /api/auth/me 的用户手机号
        registration.setContactEmail(request.getContactEmail());         // 来自 /api/auth/me 的用户邮箱
        registration.setTrackId(request.getTrackId());                   // 当前前端固定传 null
        registration.setStatus("pending");                               // 报名状态默认为待审核
        registration.setCreatedAt(LocalDateTime.now());
        registration.setUpdatedAt(LocalDateTime.now());

        registrationsService.submitRegistration(registration);
        log.info("用户 {} 报名成功，报名ID: {}", userId, registration.getId());

        // ========== 5. 构建 RegistrationVO 并返回 ==========
        RegistrationVO vo = new RegistrationVO();
        vo.setId(registration.getId());
        vo.setEventId(registration.getEventId());
        vo.setUserId(registration.getUserId());
        vo.setRegistrationType((String) registration.getRegistrationType());
        vo.setTeamName(registration.getTeamName());
        vo.setContactName(registration.getContactName());
        vo.setContactPhone(registration.getContactPhone());
        vo.setContactEmail(registration.getContactEmail());
        vo.setTrackId(registration.getTrackId());
        vo.setStatus((String) registration.getStatus());
        vo.setCreatedAt(registration.getCreatedAt());

        return Result.ok(vo);
    }
}
