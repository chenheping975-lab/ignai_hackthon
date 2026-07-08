# API 端口与接口设计

版本：v0.5
更新日期：2026-07-08
OpenAPI 文件：`api/openapi.yaml`

## 1. 当前接口状态

本文件区分两种状态：

| 状态 | 含义 |
| --- | --- |
| 已实现 | 当前 Spring Boot Controller 中已经存在，可进入联调。 |
| 规划 | 已纳入产品和 API 设计，后续按此实现。 |

当前已查询代码，已实现的 Controller：

| Controller | 基础路径 | 已实现接口 |
| --- | --- | --- |
| `AuthController` | `/api/auth` | `POST /register`、`POST /login` |
| `PublicController` | `/api/public` | `GET /events/current`、`GET /events`、`GET /projects` |
| `RegistrationController` | `/api` | `POST /registrations` |

未实现的接口：

| 接口 | 状态 | 说明 |
|------|------|------|
| `GET /api/auth/me` | ❌ 404 | 前端需要获取当前用户信息，报名时 contactName/phone/email 依赖此接口 |
| `GET /api/public/events/{eventId}/tracks` | ❌ 404 | 赛道数据接口未实现 |
| `GET /api/registrations/my` | ❌ 404 | 用户查看自己的报名状态 |

后台管理系统为本轮新增规划，尚未实现。

## 2. 服务端口

| 服务 | 端口 / 路径 | 说明 |
| --- | --- | --- |
| 前端页面 | `http://localhost:8080/` | 静态前端，建议由 Spring Boot 托管或 Nginx 反代 |
| 后台页面入口 | `http://localhost:8080/root/` | `frontend/root/` 目录 |
| 后端服务 | `http://localhost:8080/` | Spring Boot 默认端口 |
| API 前缀 | `/api` | 所有 JSON 接口 |
| MySQL | `118.89.62.40:3306` | 远程数据库（当前配置） |
| 文件上传 | `/api/projects/{projectId}/files` | 参赛者上传项目附件 |
| 文件访问 | `/uploads/*` 或 `/api/files/{fileId}` | MVP 可用静态路径，正式建议受控下载 |

> **联调说明：** 前端 `api.js` 使用相对路径 `/api`，需要前端和后端同源（同一端口）或通过 Nginx/CORS 解决跨域。当前后端未配置 CORS，也未托管前端静态文件。

## 3. 权限和认证

| API 前缀 | 权限 |
| --- | --- |
| `/api/root/*` | 仅 root。用于 root 初始化、root 登录、密码和创始人邮箱管理。 |
| `/api/admin/*` | root / admin。用于后台业务管理。 |
| `/api/auth/*` | 公开注册登录。 |
| `/api/projects/*`、`/api/teams/*` | 登录参赛者。 |
| `/api/public/*` | 公开读取；点赞建议要求登录用户。 |

JWT 规则沿用当前登录返回的 Bearer Token：

```http
Authorization: Bearer <accessToken>
```

## 4. Root 后台入口

| 方法 | 路径 | 状态 | 说明 |
| --- | --- | --- | --- |
| `GET` | `/api/root/setup-status` | 规划 | 检查系统是否已经初始化 root。 |
| `POST` | `/api/root/bootstrap` | 规划 | 首次设置 root 密码和创始人邮箱，只允许执行一次。 |
| `POST` | `/api/root/login` | 规划 | root 登录后台。 |
| `POST` | `/api/root/logout` | 规划 | root 退出。 |
| `GET` | `/api/root/me` | 规划 | 获取当前 root 信息。 |
| `PATCH` | `/api/root/password` | 规划 | root 修改密码。 |
| `PATCH` | `/api/root/owner-email` | 规划 | 更换创始人邮箱，需要 root 密码二次确认。 |

核心规则：

- `root_owner_email` 存入 `system_settings`。
- 如果 root 已初始化，`bootstrap` 必须返回 `ROOT_ALREADY_INITIALIZED`。
- 重置 root 密码必须验证创始人邮箱，不允许直接匿名重置。

## 5. 用户管理

| 方法 | 路径 | 状态 | 说明 |
| --- | --- | --- | --- |
| `GET` | `/api/admin/users` | 规划 | 用户表，支持角色、状态、关键词筛选。 |
| `GET` | `/api/admin/users/{userId}` | 规划 | 用户详情，含报名、队伍、项目摘要。 |
| `PATCH` | `/api/admin/users/{userId}/status` | 规划 | 启用或禁用用户。 |
| `POST` | `/api/admin/users/admins` | 规划 | root 创建管理员账号。 |

## 6. 活动和问卷管理

| 方法 | 路径 | 状态 | 说明 |
| --- | --- | --- | --- |
| `GET` | `/api/admin/dashboard` | 规划 | 后台概览统计。 |
| `GET` | `/api/admin/events` | 规划 | 活动列表。 |
| `POST` | `/api/admin/events` | 规划 | 创建活动。 |
| `PUT` | `/api/admin/events/{eventId}` | 规划 | 更新活动基础内容。 |
| `PATCH` | `/api/admin/events/{eventId}/windows` | 规划 | 设置报名窗口、提交窗口、评分/点赞窗口。 |
| `GET` | `/api/admin/events/{eventId}/tracks` | 规划 | 赛道列表。 |
| `POST` | `/api/admin/events/{eventId}/tracks` | 规划 | 创建赛道。 |
| `PUT` | `/api/admin/events/{eventId}/form-fields` | 规划 | 保存报名或作品提交字段。 |
| `GET` | `/api/public/events/current` | 已实现 | 当前公开活动配置。 |
| `GET` | `/api/public/events/{eventId}/form-fields?target=registration` | 规划 | 前台读取动态报名字段。 |

## 7. 报名和 AI 初筛

| 方法 | 路径 | 状态 | 说明 |
| --- | --- | --- | --- |
| `POST` | `/api/public/events/{eventId}/registrations` | 规划 | 参赛者提交报名和自定义问卷。 |
| `GET` | `/api/admin/registrations` | 规划 | 报名列表，支持状态、赛道、AI 标签、关键词筛选。 |
| `GET` | `/api/admin/registrations/{registrationId}` | 规划 | 报名详情，含 payload、成员、AI 结果。 |
| `POST` | `/api/admin/registrations/{registrationId}/ai-screen` | 规划 | 触发 AI 初筛、标签、摘要、乱填风险识别。 |
| `PATCH` | `/api/admin/registrations/{registrationId}/review` | 规划 | 人工审核通过、拒绝、候补。 |
| `POST` | `/api/admin/registrations/batch-review` | 规划 | 批量审核。 |

AI 初筛建议结果：

```json
{
  "qualityScore": 82,
  "spamRisk": "low",
  "tags": ["Agent", "教育", "全栈"],
  "summary": "报名者希望做一个面向实训课程的 Agent 工具。",
  "recommendation": "approve",
  "reasons": ["回答完整", "方向与赛题匹配"]
}
```

## 8. 邮件 Agent

| 方法 | 路径 | 状态 | 说明 |
| --- | --- | --- | --- |
| `GET` | `/api/admin/mail/templates` | 规划 | 邮件模板列表。 |
| `POST` | `/api/admin/mail/templates` | 规划 | 创建模板。 |
| `PUT` | `/api/admin/mail/templates/{templateId}` | 规划 | 更新模板。 |
| `POST` | `/api/admin/registrations/{registrationId}/mail-draft` | 规划 | 基于报名信息生成个性化邮件草稿。 |
| `POST` | `/api/admin/mail/send` | 规划 | 管理员确认后发送。 |
| `GET` | `/api/admin/mail/logs` | 规划 | 邮件发送记录。 |

邮件草稿必须允许人工编辑，发送前不得自动发出。

## 9. 队伍和项目提交

| 方法 | 路径 | 状态 | 说明 |
| --- | --- | --- | --- |
| `GET` | `/api/teams/me` | 规划 | 当前用户队伍。 |
| `POST` | `/api/teams` | 规划 | 创建或加入队伍。 |
| `GET` | `/api/projects/my` | 规划 | 我的作品。 |
| `POST` | `/api/projects` | 规划 | 创建或保存作品。 |
| `PUT` | `/api/projects/{projectId}` | 规划 | 更新作品。 |
| `POST` | `/api/projects/{projectId}/files` | 规划 | 上传 PPT、PDF、HTML、ZIP、MP3、图片、文档等附件。 |
| `POST` | `/api/projects/{projectId}/submit` | 规划 | 正式提交作品。 |

MVP 不支持视频。发现视频文件时返回 `VIDEO_NOT_SUPPORTED_IN_MVP` 或 `FILE_TYPE_NOT_ALLOWED`。

## 10. 后台项目看板和公开展示

| 方法 | 路径 | 状态 | 说明 |
| --- | --- | --- | --- |
| `GET` | `/api/admin/projects` | 规划 | 后台项目看板，支持状态、赛道、关键词、票数排序。 |
| `GET` | `/api/admin/projects/{projectId}` | 规划 | 项目详情和附件列表。 |
| `PATCH` | `/api/admin/projects/{projectId}/status` | 规划 | 修改 draft/submitted/locked/published/archived。 |
| `PATCH` | `/api/admin/projects/{projectId}/showcase` | 规划 | 设置是否公开、精选、排序权重。 |
| `GET` | `/api/public/projects` | 已实现 | 公开作品列表，当前支持 `status`、`pageSize`；后续扩展赛道和排序。 |
| `GET` | `/api/public/projects/{projectId}` | 规划 | 公开作品详情。 |
| `POST` | `/api/public/projects/{projectId}/votes` | 规划 | 登录用户点赞。 |
| `POST` | `/api/public/projects/{projectId}/ratings` | 规划 | 开放评分。 |

## 11. 飞书同步

| 方法 | 路径 | 状态 | 说明 |
| --- | --- | --- | --- |
| `POST` | `/api/admin/feishu/sync` | 规划 | 创建飞书同步任务。 |
| `GET` | `/api/admin/feishu/logs` | 规划 | 同步日志。 |
| `POST` | `/api/admin/feishu/logs/{logId}/retry` | 规划 | 重试失败任务。 |

## 12. 通用响应

成功：

```json
{
  "success": true,
  "errorCode": null,
  "message": "ok",
  "data": {}
}
```

失败：

```json
{
  "success": false,
  "errorCode": "REGISTRATION_DUPLICATED",
  "message": "同一活动下该手机号已报名",
  "data": null
}
```

## 13. 常用错误码

| 错误码 | 含义 |
| --- | --- |
| `UNAUTHORIZED` | 未登录或 token 过期 |
| `FORBIDDEN` | 无权限 |
| `ROOT_ALREADY_INITIALIZED` | root 已初始化，不能重复 bootstrap |
| `ROOT_OWNER_EMAIL_REQUIRED` | root 创始人邮箱未配置 |
| `INVALID_ROOT_OWNER_EMAIL` | 创始人邮箱验证失败 |
| `EVENT_NOT_OPEN` | 活动未开放 |
| `REGISTRATION_CLOSED` | 报名已截止 |
| `REGISTRATION_DUPLICATED` | 重复报名 |
| `AI_SCREENING_FAILED` | AI 初筛失败 |
| `MAIL_DRAFT_FAILED` | 邮件草稿生成失败 |
| `MAIL_SEND_FAILED` | 邮件发送失败 |
| `TEAM_MEMBER_DUPLICATED` | 队员已加入其他队伍 |
| `SUBMISSION_CLOSED` | 提交已截止 |
| `FILE_TYPE_NOT_ALLOWED` | 文件类型不允许 |
| `VIDEO_NOT_SUPPORTED_IN_MVP` | MVP 不支持视频 |
| `PROJECT_NOT_PUBLISHED` | 作品未公开 |
| `VOTE_DUPLICATED` | 重复点赞 |
| `RATING_CLOSED` | 评分已关闭 |
| `RATING_DUPLICATED` | 重复评分 |
| `FEISHU_SYNC_FAILED` | 飞书同步失败 |

## 14. 变更记录

| 版本 | 日期 | 变更 |
| --- | --- | --- |
| v0.4 | 2026-07-08 | 增加 root 后台入口、用户表、报名 AI 初筛、邮件 Agent、项目看板、点赞展示等后台管理 API 规划；标注已实现与规划接口边界。 |
| v0.5 | 2026-07-08 | 更新已实现接口列表（增加 RegistrationController）；标注未实现的关键接口（auth/me、tracks、registrations/my）；更新服务端口说明和联调注意事项。 |
