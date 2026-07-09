package com.csust.hackathonserver.controller;

import com.csust.hackathonserver.Response.EventVO;
import com.csust.hackathonserver.Response.ProjectVO;
import com.csust.hackathonserver.Response.Result;
import com.csust.hackathonserver.Utils.JwtUtil;
import com.csust.hackathonserver.pojo.Events;
import com.csust.hackathonserver.pojo.FormFields;
import com.csust.hackathonserver.pojo.Projects;
import com.csust.hackathonserver.pojo.Ratings;
import com.csust.hackathonserver.pojo.Votes;
import com.csust.hackathonserver.service.EventsService;
import com.csust.hackathonserver.service.FormFieldsService;
import com.csust.hackathonserver.service.ProjectsService;
import com.csust.hackathonserver.service.RatingsServices;
import com.csust.hackathonserver.service.TracksService;
import com.csust.hackathonserver.mapper.VotesMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/public")
public class PublicController {
    @Autowired
    private EventsService eventsService;
    @Autowired
    private ProjectsService projectsService;
    @Autowired
    private FormFieldsService formFieldsService;
    @Autowired
    private TracksService tracksService;
    @Autowired
    private RatingsServices ratingsServices;
    @Autowired
    private VotesMapper votesMapper;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/events/current")
    public Result getCurrentEvent() {
        Events currentEvent = eventsService.getCurrentEvent();
        if (currentEvent == null) {
            return Result.fail("暂时还没有符合条件的活动，敬请期待");
        }
        return Result.ok(buildEventData(currentEvent, true));
    }

    @GetMapping("/events/{eventId}")
    public Result getEventById(@PathVariable Long eventId) {
        Events event = eventsService.findById(eventId);
        if (event == null) {
            return Result.fail("活动不存在");
        }
        return Result.ok(buildEventData(event, true));
    }

    /**
     * 分页查询活动列表
     * GET /api/public/events?page=1&pageSize=10
     */
    @GetMapping("/events")
    public Result listEvents(@RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        PageInfo<Events> pageInfo = eventsService.listEvents(page, pageSize);
        List<Events> events = pageInfo.getList();
        List<EventVO> eventVOs = new ArrayList<>();
        for (Events e : events) {
            EventVO vo = new EventVO();
            vo.setId(e.getId());
            vo.setTitle(e.getTitle());
            vo.setSubtitle(e.getSubtitle());
            vo.setLocation(e.getLocation());
            vo.setDescription(e.getDescription());
            vo.setStatus((String) e.getStatus());
            vo.setRegistrationOpen(Integer.valueOf(1).equals(e.getRegistrationOpen()));
            vo.setRegistrationDeadline(e.getRegistrationDeadline());
            vo.setSubmissionDeadline(e.getSubmissionDeadline());
            eventVOs.add(vo);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", eventVOs);
        data.put("total", pageInfo.getTotal());
        data.put("page", pageInfo.getPageNum());
        data.put("pageSize", pageInfo.getPageSize());
        return Result.ok(data);
    }

    @GetMapping("/projects")
    public Result getPublicProjects(@RequestParam(value = "status", required = false) String status) {
        if (status == null || status.isBlank()) {
            status = "published";
        }
        List<Projects> projects = projectsService.getPublicProjects(status);
        List<ProjectVO> projectVOS = new ArrayList<ProjectVO>();
        projects.forEach(project -> {
            ProjectVO projectVO = new ProjectVO();
            projectVO.setId(project.getId());
            projectVO.setEventId(project.getEventId());
            projectVO.setTitle(project.getTitle());
            projectVO.setTrackId(project.getTrackId());
            projectVO.setTagline(project.getTagline());
            projectVO.setDescription(project.getDescription());
            projectVO.setCoverUrl(project.getDemoUrl());
            projectVO.setStatus((String) project.getStatus());
            projectVO.setSubmittedAt(project.getSubmittedAt());
            projectVO.setVotes(votesMapper.countByProjectId(project.getId()));
            projectVOS.add(projectVO);
        });
        return Result.ok(projectVOS);
    }

    @GetMapping("/projects/{projectId}")
    public Result getPublicProjectDetail(@PathVariable Long projectId) {
        Projects project = projectsService.findById(projectId);
        if (project == null) {
            return Result.fail("作品不存在");
        }
        ProjectVO projectVO = new ProjectVO();
        projectVO.setId(project.getId());
        projectVO.setEventId(project.getEventId());
        projectVO.setTitle(project.getTitle());
        projectVO.setTrackId(project.getTrackId());
        projectVO.setTagline(project.getTagline());
        projectVO.setDescription(project.getDescription());
        projectVO.setCoverUrl(project.getDemoUrl());
        projectVO.setStatus((String) project.getStatus());
        projectVO.setSubmittedAt(project.getSubmittedAt());
        projectVO.setVotes(votesMapper.countByProjectId(project.getId()));
        return Result.ok(projectVO);
    }

    @GetMapping("/events/{eventId}/tracks")
    public Result getTracks(@PathVariable Long eventId) {
        Events event = eventsService.findById(eventId);
        if (event == null) {
            return Result.fail("活动不存在");
        }
        return Result.ok(tracksService.findByEventId(eventId));
    }

    /**
     * 获取活动的动态表单字段
     * GET /api/public/events/{eventId}/form-fields?target=registration|project
     */
    @GetMapping("/events/{eventId}/form-fields")
    public Result getFormFields(@PathVariable("eventId") Long eventId,
                                @RequestParam("target") String target) {
        // 校验 target 参数
        if (!"registration".equals(target) && !"project".equals(target)) {
            return Result.fail("target 参数必须为 registration 或 project");
        }

        List<FormFields> fields = formFieldsService.getFormFields(eventId, target);

        List<Map<String, Object>> fieldList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (FormFields f : fields) {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("fieldKey", f.getFieldKey());
            fieldMap.put("label", f.getLabel());
            fieldMap.put("fieldType", f.getFieldType());
            fieldMap.put("required", Integer.valueOf(1).equals(f.getRequired()));
            fieldMap.put("placeholder", f.getPlaceholder());
            fieldMap.put("sortOrder", f.getSortOrder());
            fieldMap.put("enabled", Integer.valueOf(1).equals(f.getEnabled()));

            // 解析 optionsJson 为 List<String>
            List<String> options = parseOptions(f.getOptionsJson(), objectMapper);
            fieldMap.put("options", options);

            fieldList.add(fieldMap);
        }
        return Result.ok(fieldList);
    }

    /**
     * 解析 MySQL JSON 字段为 List&lt;String&gt;，兼容 String/List/null 类型
     */
    @SuppressWarnings("unchecked")
    private List<String> parseOptions(Object optionsJson, ObjectMapper objectMapper) {
        if (optionsJson == null) {
            return Collections.emptyList();
        }
        try {
            if (optionsJson instanceof String) {
                return objectMapper.readValue((String) optionsJson, List.class);
            } else if (optionsJson instanceof List) {
                return (List<String>) optionsJson;
            }
        } catch (Exception e) {
            log.warn("解析 optionsJson 失败: {}", optionsJson, e);
        }
        return Collections.emptyList();
    }

    private Map<String, Object> buildEventData(Events event, boolean includeConfig) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", event.getId());
        data.put("title", event.getTitle());
        data.put("subtitle", event.getSubtitle());
        data.put("location", event.getLocation());
        data.put("description", event.getDescription());
        data.put("status", event.getStatus());
        data.put("registrationOpen", Integer.valueOf(1).equals(event.getRegistrationOpen()));
        data.put("ratingEnabled", Integer.valueOf(1).equals(event.getRatingEnabled()));
        data.put("voteEnabled", Integer.valueOf(1).equals(event.getVoteEnabled()));
        data.put("registrationDeadline", event.getRegistrationDeadline());
        data.put("submissionDeadline", event.getSubmissionDeadline());
        data.put("ratingStartAt", event.getRatingStartAt());
        data.put("ratingEndAt", event.getRatingEndAt());
        data.put("officialSiteUrl", event.getOfficialSiteUrl());
        data.put("benchmarkSiteUrl", event.getBenchmarkSiteUrl());

        if (includeConfig) {
            data.put("tracks", tracksService.findByEventId(event.getId()));
            data.put("registrationFields", buildFormFields(event.getId(), "registration"));
            data.put("projectFields", buildFormFields(event.getId(), "project"));
        }
        return data;
    }

    private List<Map<String, Object>> buildFormFields(Long eventId, String target) {
        List<FormFields> fields = formFieldsService.getFormFields(eventId, target);
        List<Map<String, Object>> fieldList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (FormFields f : fields) {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("fieldKey", f.getFieldKey());
            fieldMap.put("label", f.getLabel());
            fieldMap.put("fieldType", f.getFieldType());
            fieldMap.put("required", Integer.valueOf(1).equals(f.getRequired()));
            fieldMap.put("placeholder", f.getPlaceholder());
            fieldMap.put("sortOrder", f.getSortOrder());
            fieldMap.put("enabled", Integer.valueOf(1).equals(f.getEnabled()));
            fieldMap.put("options", parseOptions(f.getOptionsJson(), objectMapper));
            fieldList.add(fieldMap);
        }
        return fieldList;
    }

    @PostMapping("/projects/{projectId}/votes")
    public Result voteProject(@PathVariable Long projectId, HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return Result.fail("UNAUTHORIZED", "请先登录");
        }

        String voterKey = "user_" + userId;
        Votes existing = votesMapper.findByProjectIdAndVoterKey(projectId, voterKey);
        if (existing != null) {
            int count = votesMapper.countByProjectId(projectId);
            Map<String, Object> data = new HashMap<>();
            data.put("votes", count);
            data.put("liked", true);
            data.put("duplicated", true);
            return Result.ok(data);
        }

        Votes vote = new Votes();
        vote.setProjectId(projectId);
        vote.setVoterUserId(userId);
        vote.setVoterKey(voterKey);
        vote.setSourceType("participant");
        vote.setCreatedAt(LocalDateTime.now());
        votesMapper.insert(vote);

        int count = votesMapper.countByProjectId(projectId);
        Map<String, Object> data = new HashMap<>();
        data.put("votes", count);
        data.put("liked", true);
        data.put("duplicated", false);
        return Result.ok(data);
    }

    @PostMapping("/projects/{projectId}/ratings")
    public Result rateProject(@PathVariable Long projectId, @RequestBody Map<String, Object> body) {
        String raterKey = (String) body.get("raterKey");
        if (raterKey == null || raterKey.isBlank()) {
            return Result.fail("PARAM_INVALID", "raterKey 不能为空");
        }

        Object scoreObj = body.get("score");
        if (scoreObj == null) {
            return Result.fail("PARAM_INVALID", "score 不能为空");
        }
        BigDecimal score = new BigDecimal(scoreObj.toString());

        // 检查重复
        Ratings existing = ratingsServices.findByProjectIdAndRaterKey(projectId, raterKey);
        if (existing != null) {
            return Result.fail("DUPLICATED", "已经评分过了");
        }

        Ratings rating = new Ratings();
        rating.setProjectId(projectId);
        rating.setRaterKey(raterKey);
        rating.setScore(score);
        rating.setSourceType("public");
        rating.setCreatedAt(LocalDateTime.now());
        ratingsServices.addRating(rating);

        int count = ratingsServices.countByProjectId(projectId);
        Map<String, Object> data = new HashMap<>();
        data.put("ratingCount", count);
        return Result.ok(data);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        try {
            return jwtUtil.getUserId(header.substring(7));
        } catch (Exception e) {
            return null;
        }
    }
}
