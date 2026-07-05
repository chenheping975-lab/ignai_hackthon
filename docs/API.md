# IGNAI Hackathon 前后端联调接口文档

本文档只覆盖当前前端静态页面需要调用的接口。后端可使用 Spring Boot + MyBatis + Lombok + MySQL 实现。

## 1. 通用约定

### 基础地址

前端统一请求相对路径：

```text
/api
```

本地联调时建议通过 Nginx 或 Spring Boot 静态资源代理，让：

```text
http://localhost:5173/api/*
```

转发到：

```text
http://localhost:8080/api/*
```

### 响应格式

所有接口返回：

```json
{
  "success": true,
  "errorCode": null,
  "message": "ok",
  "data": {}
}
```

失败示例：

```json
{
  "success": false,
  "errorCode": "UNAUTHORIZED",
  "message": "登录已失效，请重新登录",
  "data": null
}
```

### 登录态

使用 JWT Bearer。登录成功后，前端保存 `accessToken` 到 `localStorage`，后续需要登录的接口携带：

```http
Authorization: Bearer <accessToken>
```

## 2. 认证接口

### 2.1 注册

```http
POST /api/auth/register
Content-Type: application/json
```

请求：

```json
{
  "name": "张三",
  "phone": "18812345678",
  "email": "zhangsan@example.com",
  "password": "123456"
}
```

字段：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `name` | string | 是 | 姓名 |
| `phone` | string | 是 | 手机号，全局唯一 |
| `email` | string | 否 | 邮箱 |
| `password` | string | 是 | 密码，后端必须加密存储 |

成功：

```json
{
  "success": true,
  "errorCode": null,
  "message": "ok",
  "data": {
    "id": 1,
    "name": "张三",
    "phone": "18812345678",
    "email": "zhangsan@example.com",
    "role": "participant"
  }
}
```

### 2.2 登录

```http
POST /api/auth/login
Content-Type: application/json
```

请求：

```json
{
  "account": "18812345678",
  "password": "123456"
}
```

`account` 支持手机号或邮箱。

成功：

```json
{
  "success": true,
  "errorCode": null,
  "message": "ok",
  "data": {
    "accessToken": "jwt-token",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "user": {
      "id": 1,
      "name": "张三",
      "phone": "18812345678",
      "email": "zhangsan@example.com",
      "role": "participant"
    }
  }
}
```

### 2.3 当前用户

```http
GET /api/auth/me
Authorization: Bearer <accessToken>
```

成功：

```json
{
  "success": true,
  "errorCode": null,
  "message": "ok",
  "data": {
    "id": 1,
    "name": "张三",
    "phone": "18812345678",
    "email": "zhangsan@example.com",
    "role": "participant"
  }
}
```

## 3. 首页公开接口

### 3.1 当前活动

```http
GET /api/public/events/current
```

成功：

```json
{
  "success": true,
  "errorCode": null,
  "message": "ok",
  "data": {
    "id": 1,
    "title": "IGNAI AI Skillathon",
    "location": "长沙",
    "description": "连接创造者，点燃一场 AI 作品现场。",
    "registrationOpen": true,
    "registrationDeadline": "2026-08-01T23:59:59"
  }
}
```

前端使用字段：

| 字段 | 说明 |
| --- | --- |
| `id` | 提交报名时使用 |
| `title` | 可用于动态替换首页标题 |
| `description` | 可用于动态替换首页说明 |
| `location` | 可用于动态替换城市 |
| `registrationOpen` | 可用于显示报名状态 |

### 3.2 公开作品列表

```http
GET /api/public/projects?status=submitted&pageSize=2
```

成功：

```json
{
  "success": true,
  "errorCode": null,
  "message": "ok",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "Campus Agent Hub",
        "trackId": 1,
        "trackName": "Agent Tools",
        "tagline": "面向高校实训的课程 Agent 与作品沉淀平台。",
        "description": "项目详细介绍",
        "coverUrl": "./assets/img/local-global-embers.png",
        "averageScore": 4.7,
        "ratingCount": 128
      }
    ],
    "total": 1
  }
}
```

如果后端未启动或返回空列表，前端保留静态作品卡片。

## 4. 报名接口

### 4.1 提交报名

当前前端不改原页面，不新增表单。用户登录后点击首页 `#apply` 区域里的报名按钮，前端会调用当前用户信息提交个人报名。

```http
POST /api/registrations
Authorization: Bearer <accessToken>
Content-Type: application/json
```

请求：

```json
{
  "eventId": 1,
  "registrationType": "individual",
  "teamName": null,
  "contactName": "张三",
  "contactPhone": "18812345678",
  "contactEmail": "zhangsan@example.com",
  "trackId": null
}
```

字段：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `eventId` | number | 是 | 活动 ID |
| `registrationType` | string | 是 | 当前前端固定传 `individual` |
| `teamName` | string/null | 否 | 当前前端固定传 `null` |
| `contactName` | string | 是 | 来自 `/api/auth/me` |
| `contactPhone` | string | 是 | 来自 `/api/auth/me` |
| `contactEmail` | string/null | 否 | 来自 `/api/auth/me` |
| `trackId` | number/null | 否 | 当前前端固定传 `null` |

成功：

```json
{
  "success": true,
  "errorCode": null,
  "message": "ok",
  "data": {
    "id": 10,
    "eventId": 1,
    "userId": 1,
    "registrationType": "individual",
    "contactName": "张三",
    "contactPhone": "18812345678",
    "contactEmail": "zhangsan@example.com",
    "status": "pending",
    "createdAt": "2026-07-05T16:00:00"
  }
}
```

重复报名：

```json
{
  "success": false,
  "errorCode": "REGISTRATION_DUPLICATED",
  "message": "你已经提交过报名",
  "data": null
}
```

## 5. 错误码建议

| errorCode | HTTP | 说明 |
| --- | --- | --- |
| `PARAM_INVALID` | 400 | 参数不合法 |
| `UNAUTHORIZED` | 401 | 未登录或 token 无效 |
| `FORBIDDEN` | 403 | 无权限 |
| `ACCOUNT_EXISTS` | 409 | 手机号或邮箱已注册 |
| `ACCOUNT_PASSWORD_ERROR` | 401 | 账号或密码错误 |
| `EVENT_NOT_FOUND` | 404 | 活动不存在 |
| `REGISTRATION_CLOSED` | 400 | 报名未开放或已截止 |
| `REGISTRATION_DUPLICATED` | 409 | 重复报名 |
| `INTERNAL_ERROR` | 500 | 服务器内部错误 |

## 6. 后端实现提醒

- 密码必须保存哈希值，不要明文保存。
- JWT 至少包含用户 ID、角色、过期时间。
- `/api/auth/me` 返回的用户信息需要包含 `name`、`phone`、`email`，因为前端报名提交会用这些字段。
- `/api/registrations` 建议限制同一个 `eventId + userId` 或 `eventId + contactPhone` 只能报名一次。
