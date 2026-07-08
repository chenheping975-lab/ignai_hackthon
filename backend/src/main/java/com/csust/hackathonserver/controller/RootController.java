package com.csust.hackathonserver.controller;

import com.csust.hackathonserver.Response.LoginResponse;
import com.csust.hackathonserver.Response.Result;
import com.csust.hackathonserver.Response.UserVO;
import com.csust.hackathonserver.Utils.JwtUtil;
import com.csust.hackathonserver.pojo.LoginRequest;
import com.csust.hackathonserver.pojo.OwnerEmailChangeRequest;
import com.csust.hackathonserver.pojo.PasswordChangeRequest;
import com.csust.hackathonserver.pojo.RootBootstrapRequest;
import com.csust.hackathonserver.pojo.Users;
import com.csust.hackathonserver.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/root")
public class RootController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 检查 root 是否已初始化
     */
    @GetMapping("/setup-status")
    public Result setupStatus() {
        int count = usersService.countByRole("root");
        Map<String, Object> data = new HashMap<>();
        data.put("initialized", count > 0);
        return Result.ok(data);
    }

    /**
     * 首次初始化 root 账号（系统生命周期内只能调用一次）
     */
    @PostMapping("/bootstrap")
    public Result bootstrap(@RequestBody RootBootstrapRequest request) {
        // 检查是否已初始化
        int count = usersService.countByRole("root");
        if (count > 0) {
            return Result.fail("ROOT_EXISTS", "root 已初始化，不能重复创建");
        }

        // 校验参数
        if (request.getOwnerEmail() == null || request.getOwnerEmail().isBlank()) {
            return Result.fail("PARAM_INVALID", "创始人邮箱不能为空");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            return Result.fail("PARAM_INVALID", "密码至少 8 位");
        }

        // 创建 root 用户
        Users root = new Users();
        root.setRole("root");
        root.setEmail(request.getOwnerEmail());
        root.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        root.setName(request.getName() != null ? request.getName() : "IGNAI Root");
        root.setCreatedAt(LocalDateTime.now());
        root.setUpdatedAt(LocalDateTime.now());

        usersService.register(root);
        log.info("root 账号初始化成功，邮箱: {}", request.getOwnerEmail());

        return Result.ok("root 初始化成功");
    }

    /**
     * root 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        // 通过账号查找用户
        Users user = usersService.getByAccount(request.getAccount());
        if (user == null) {
            return Result.fail("用户名或密码错误");
        }

        // 必须是 root 角色
        if (!"root".equals(user.getRole().toString())) {
            return Result.fail("该账号不是超级管理员");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return Result.fail("用户名或密码错误");
        }

        // 生成 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getRole().toString());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(7200L);
        response.setUser(new UserVO(user.getName(), user.getEmail(), user.getPhone(), (String) user.getRole()));

        return Result.ok(response);
    }

    /**
     * root 退出（JWT 无状态，前端清除 Token 即可）
     */
    @PostMapping("/logout")
    public Result logout() {
        return Result.ok("已退出");
    }

    /**
     * 获取当前 root 用户信息
     */
    @GetMapping("/me")
    public Result me(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "未登录或登录已失效");
        }

        Users user = usersService.findById(userId);
        if (user == null || !"root".equals(user.getRole().toString())) {
            return Result.fail("UNAUTHORIZED", "无权访问");
        }

        UserVO vo = new UserVO(user.getName(), user.getEmail(), user.getPhone(), (String) user.getRole());
        return Result.ok(vo);
    }

    /**
     * 修改 root 密码
     */
    @PatchMapping("/password")
    public Result changePassword(@RequestBody PasswordChangeRequest request, HttpServletRequest srequest) {
        Long userId = getUserIdFromToken(srequest);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "未登录或登录已失效");
        }

        Users user = usersService.findById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            return Result.fail("旧密码不正确");
        }

        // 更新为新密码
        String newHash = passwordEncoder.encode(request.getNewPassword());
        usersService.updatePassword(userId, newHash);

        return Result.ok("密码修改成功");
    }

    /**
     * 更换创始人邮箱
     */
    @PatchMapping("/owner-email")
    public Result changeEmail(@RequestBody OwnerEmailChangeRequest request, HttpServletRequest srequest) {
        Long userId = getUserIdFromToken(srequest);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "未登录或登录已失效");
        }

        Users user = usersService.findById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return Result.fail("密码不正确");
        }

        // 更新邮箱
        usersService.updateEmail(userId, request.getOwnerEmail());

        return Result.ok("邮箱修改成功");
    }

    /**
     * 从请求头中解析 JWT 获取 userId
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        try {
            return jwtUtil.getUserId(token);
        } catch (Exception e) {
            log.warn("JWT 解析失败: {}", e.getMessage());
            return null;
        }
    }
}
