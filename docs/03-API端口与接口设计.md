# API 端口与接口设计

OpenAPI 文件见 `api/openapi.yaml`。

## 1. 服务端口

| 服务 | 端口 / 路径 | 说明 |
| --- | --- | --- |
| 前端预览 | `http://localhost:5173/` | 静态前端原型 |
| 后端服务 | `http://localhost:8080/` | Java Web / Tomcat |
| API 前缀 | `/api` | 所有 JSON 接口 |
| 文件上传 | `/api/projects/{id}/files` | 上传项目附件 |
| 文件访问 | `/uploads/*` 或 `/api/files/{id}` | MVP 可用静态路径，正式建议受控下载 |
| MySQL | `localhost:3306` | 数据库 |

## 2. 接口分组

### 2.1 公共活动接口

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/public/events/current` | 获取当前活动首页配置 |
| `GET` | `/api/public/events/{eventId}/tracks` | 获取赛道 |
| `GET` | `/api/public/events/{eventId}/form-fields?target=registration` | 获取报名字段 |
| `POST` | `/api/public/events/{eventId}/registrations` | 提交报名 |

### 2.2 后台活动配置

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/admin/login` | 管理员登录 |
| `POST` | `/api/admin/logout` | 管理员退出 |
| `GET` | `/api/admin/events` | 活动列表 |
| `POST` | `/api/admin/events` | 创建活动 |
| `PUT` | `/api/admin/events/{eventId}` | 更新活动 |
| `POST` | `/api/admin/events/{eventId}/tracks` | 新增赛道 |
| `PUT` | `/api/admin/events/{eventId}/form-fields` | 保存表单字段 |

### 2.3 报名审核

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/admin/registrations?status=pending` | 报名列表 |
| `GET` | `/api/admin/registrations/{id}` | 报名详情 |
| `POST` | `/api/admin/registrations/{id}/ai-tags` | 触发 AI 标签 |
| `PATCH` | `/api/admin/registrations/{id}/review` | 审核通过、拒绝、候补 |
| `POST` | `/api/admin/registrations/batch-review` | 批量审核 |

### 2.4 邮件 Agent

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/admin/mail/templates` | 模板列表 |
| `POST` | `/api/admin/mail/templates` | 创建模板 |
| `POST` | `/api/admin/mail/drafts` | 生成邮件草稿 |
| `POST` | `/api/admin/mail/send` | 人工确认后发送邮件 |
| `GET` | `/api/admin/mail/logs` | 邮件发送记录 |

### 2.5 队伍和作品

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/teams/me` | 查看当前队伍 |
| `POST` | `/api/teams` | 创建或加入队伍 |
| `GET` | `/api/projects/my` | 我的作品 |
| `POST` | `/api/projects` | 创建 / 保存作品 |
| `PUT` | `/api/projects/{projectId}` | 更新作品 |
| `POST` | `/api/projects/{projectId}/files` | 上传附件 |
| `POST` | `/api/projects/{projectId}/submit` | 正式提交作品 |

### 2.6 作品展示、评分和投票

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `GET` | `/api/public/projects` | 作品列表 |
| `GET` | `/api/public/projects/{projectId}` | 作品详情 |
| `POST` | `/api/public/projects/{projectId}/ratings` | 开放评分 |
| `POST` | `/api/public/projects/{projectId}/votes` | 投票 |

### 2.7 飞书同步

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| `POST` | `/api/admin/feishu/sync` | 创建同步任务 |
| `GET` | `/api/admin/feishu/logs` | 同步日志 |
| `POST` | `/api/admin/feishu/logs/{logId}/retry` | 重试失败任务 |

## 3. 通用响应

成功：

```json
{
  "success": true,
  "data": {},
  "message": "ok"
}
```

失败：

```json
{
  "success": false,
  "errorCode": "REGISTRATION_DUPLICATED",
  "message": "同一活动下该手机号已报名"
}
```

## 4. 常用错误码

| 错误码 | 含义 |
| --- | --- |
| `UNAUTHORIZED` | 未登录或 session 过期 |
| `FORBIDDEN` | 无权限 |
| `EVENT_NOT_OPEN` | 活动未开放 |
| `REGISTRATION_CLOSED` | 报名已截止 |
| `REGISTRATION_DUPLICATED` | 重复报名 |
| `TEAM_MEMBER_DUPLICATED` | 队员已加入其他队伍 |
| `SUBMISSION_CLOSED` | 提交已截止 |
| `FILE_TYPE_NOT_ALLOWED` | 文件类型不允许 |
| `VIDEO_NOT_SUPPORTED_IN_MVP` | MVP 不支持视频 |
| `RATING_CLOSED` | 评分已关闭 |
| `RATING_DUPLICATED` | 重复评分 |
| `FEISHU_SYNC_FAILED` | 飞书同步失败 |
