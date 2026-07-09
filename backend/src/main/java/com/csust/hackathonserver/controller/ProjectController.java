package com.csust.hackathonserver.controller;

import com.csust.hackathonserver.Response.Result;
import com.csust.hackathonserver.Utils.JwtUtil;
import com.csust.hackathonserver.pojo.Events;
import com.csust.hackathonserver.pojo.ProjectFiles;
import com.csust.hackathonserver.pojo.Projects;
import com.csust.hackathonserver.pojo.Teams;
import com.csust.hackathonserver.service.EventsService;
import com.csust.hackathonserver.service.ProjectFilesService;
import com.csust.hackathonserver.service.ProjectsService;
import com.csust.hackathonserver.service.TeamsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api")
public class ProjectController {

    @Autowired
    private ProjectsService projectsService;

    @Autowired
    private ProjectFilesService projectFilesService;

    @Autowired
    private TeamsService teamsService;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${ignai.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping("/projects")
    public Result createProject(@RequestBody Projects project, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "请先登录");
        }

        if (project.getTitle() == null || project.getTitle().isBlank()) {
            return Result.fail("PARAM_INVALID", "作品标题不能为空");
        }

        Events currentEvent = project.getEventId() == null
                ? eventsService.getCurrentEvent()
                : eventsService.findById(project.getEventId());
        if (currentEvent == null) {
            return Result.fail("活动不存在或暂不可提交作品");
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

    @GetMapping("/projects/{projectId}/files")
    public Result listFiles(@PathVariable Long projectId, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "请先登录");
        }

        Projects existing = projectsService.findById(projectId);
        if (existing == null) {
            return Result.fail("作品不存在");
        }
        if (!ownsProject(existing, userId)) {
            return Result.fail("FORBIDDEN", "只能查看自己队伍的作品附件");
        }

        return Result.ok(projectFilesService.findByProjectId(projectId));
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
    public Result uploadFile(@PathVariable Long projectId,
                             @RequestParam("file") MultipartFile file,
                             HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "请先登录");
        }

        Projects existing = projectsService.findById(projectId);
        if (existing == null) {
            return Result.fail("作品不存在");
        }
        if (!ownsProject(existing, userId)) {
            return Result.fail("FORBIDDEN", "只能给自己队伍的作品上传附件");
        }
        if (file == null || file.isEmpty()) {
            return Result.fail("PARAM_INVALID", "请选择要上传的文件");
        }
        if (file.getSize() > 20 * 1024 * 1024) {
            return Result.fail("FILE_TOO_LARGE", "单个附件不能超过 20MB");
        }

        try {
            String originalName = sanitizeFileName(file.getOriginalFilename());
            String extension = getExtension(originalName);
            String storedName = buildStoredName(originalName);
            Path projectDir = resolveUploadRoot().resolve("projects").resolve(String.valueOf(projectId)).normalize();
            Files.createDirectories(projectDir);

            Path storedPath = projectDir.resolve(storedName).normalize();
            if (!storedPath.startsWith(projectDir)) {
                return Result.fail("PARAM_INVALID", "文件名不合法");
            }

            file.transferTo(storedPath);

            ProjectFiles projectFile = new ProjectFiles();
            projectFile.setProjectId(projectId);
            projectFile.setUploadedBy(userId);
            projectFile.setOriginalName(originalName);
            projectFile.setStoredName(storedName);
            projectFile.setFileType(resolveFileType(extension));
            projectFile.setMimeType(file.getContentType());
            projectFile.setSizeBytes(file.getSize());
            projectFile.setSha256(calculateSha256(storedPath));
            projectFile.setFilePath(storedPath.toString().replace('\\', '/'));
            projectFile.setCreatedAt(LocalDateTime.now());

            return Result.ok(projectFilesService.insert(projectFile));
        } catch (Exception e) {
            log.error("作品附件上传失败，projectId={}", projectId, e);
            return Result.fail("UPLOAD_FAILED", "附件上传失败，请稍后重试");
        }
    }

    private boolean ownsProject(Projects project, Long userId) {
        Teams team = teamsService.findById(project.getTeamId());
        return team != null && userId.equals(team.getLeaderUserId());
    }

    private Path resolveUploadRoot() {
        Path configured = Paths.get(uploadDir);
        if (configured.isAbsolute()) {
            return configured.normalize();
        }
        Path cwd = Paths.get("").toAbsolutePath().normalize();
        if (cwd.getFileName() != null && "backend".equals(cwd.getFileName().toString())) {
            Path parent = cwd.getParent();
            if (parent != null) {
                return parent.resolve(configured).normalize();
            }
        }
        return cwd.resolve(configured).normalize();
    }

    private String sanitizeFileName(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            return "attachment";
        }
        String normalized = Paths.get(originalName).getFileName().toString();
        return normalized.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index + 1).toLowerCase();
    }

    private String buildStoredName(String originalName) {
        String prefix = UUID.randomUUID() + "_";
        int maxOriginalLength = Math.max(1, 255 - prefix.length());
        String safeOriginalName = originalName.length() > maxOriginalLength
                ? originalName.substring(originalName.length() - maxOriginalLength)
                : originalName;
        return prefix + safeOriginalName;
    }

    private String resolveFileType(String extension) {
        return switch (extension) {
            case "ppt", "pptx" -> "ppt";
            case "pdf" -> "pdf";
            case "html", "htm" -> "html";
            case "zip", "rar", "7z" -> "zip";
            case "mp3", "wav", "m4a" -> "mp3";
            case "png", "jpg", "jpeg", "gif", "webp" -> "image";
            case "doc", "docx", "md", "txt" -> "doc";
            default -> "other";
        };
    }

    private String calculateSha256(Path path) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream input = Files.newInputStream(path);
             DigestInputStream digestInput = new DigestInputStream(input, digest)) {
            byte[] buffer = new byte[8192];
            while (digestInput.read(buffer) != -1) {
                // DigestInputStream updates the digest as bytes are read.
            }
        }
        return HexFormat.of().formatHex(digest.digest());
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
