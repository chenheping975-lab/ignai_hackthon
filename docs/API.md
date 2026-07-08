# IGNAI Hackathon 前后端联调接口文档

版本：v0.4
更新日期：2026-07-08

本文档只覆盖当前前端静态页面需要调用的接口。后端可使用 Spring Boot + MyBatis + Lombok + MySQL 实现。

后台管理系统、root 入口、报名 AI 初筛、邮件 Agent、项目看板和点赞展示的完整 API 规划见：

- `docs/03-API端口与接口设计.md`
- `api/openapi.yaml`

> 说明：文档里的“请求类 / 响应类”是给 Controller 使用的 DTO / VO，不一定等于数据库实体类。真正操作数据库时，再由 Service 把 Request DTO 转成 Entity。

## 1. 通用约定

### 1.1 基础地址

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

### 1.2 响应格式

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

后端可以先使用非泛型 Result：

```java
public class Result {
    private boolean success;
    private String errorCode;
    private String message;
    private Object data;
}
```

### 1.3 登录态

使用 JWT Bearer。登录成功后，前端保存 `accessToken` 到 `localStorage`，后续需要登录的接口携带：

```http
Authorization: Bearer <accessToken>
```

### 1.4 后端类对应总览

| 前端接口 | 推荐 Controller | 请求类 | 响应 data 类型 | 涉及实体类 | 涉及数据表 |
| --- | --- | --- | --- | --- | --- |
| `POST /api/auth/register` | `AuthController` | `RegisterRequest` | `UserVO` | `Users` | `users` |
| `POST /api/auth/login` | `AuthController` | `LoginRequest` | `LoginResponse` | `Users` | `users` |
| `GET /api/auth/me` | `AuthController` | 无 | `UserVO` | `Users` | `users` |
| `GET /api/public/events/current` | `PublicController` | 无 | `EventVO` | `Events` | `events` |
| `GET /api/public/projects` | `PublicController` | 查询参数 | `ProjectListResponse` | `Projects` | `projects` |
| `POST /api/public/events/{eventId}/registrations` | `RegistrationsController` | `RegistrationRequest` | `RegistrationVO` | `Registrations` | `registrations` |

> 如果你生成出来的实体类名字是 `User`、`Event`、`Project`、`Registration` 这种单数形式，就按你项目实际类名来。截图里你的 Controller 是复数形式，所以这里按 `Users / Events / Projects / Registrations` 写。

## 2. 认证接口

### 2.1 注册

```http
POST /api/auth/register
Content-Type: application/json
```

后端对应关系：

| 项目 | 建议值 |
| --- | --- |
| 推荐 Controller | `AuthController` |
| 请求类 | `RegisterRequest` |
| 响应 data 类型 | `UserVO` |
| 涉及实体类 | `Users` |
| 涉及数据表 | `users` |
| 主要 Service | `AuthService` 或 `UsersService` |

请求 JSON：

```json
{
  "name": "张三",
  "phone": "18812345678",
  "email": "zhangsan@example.com",
  "password": "123456"
}
```

请求字段：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `name` | string | 是 | 姓名 |
| `phone` | string | 是 | 手机号，全局唯一 |
| `email` | string | 否 | 邮箱 |
| `password` | string | 是 | 密码，后端必须加密存储 |

`RegisterRequest` 建议字段：

```java
public class RegisterRequest {
    private String name;
    private String phone;
    private String email;
    private String password;
}
```

业务逻辑建议：

1. 根据 `phone` 查询 `users` 表，判断手机号是否已注册。
2. 创建 `Users` 实体对象。
3. 将 `password` 加密后保存，不要明文入库。
4. 调用 `usersMapper.insert(users)`。
5. 返回 `UserVO`，不要返回密码字段。

成功响应：

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

`UserVO` 建议字段：

```java
public class UserVO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String role;
}
```

### 2.2 登录

```http
POST /api/auth/login
Content-Type: application/json
```

后端对应关系：

| 项目 | 建议值 |
| --- | --- |
| 推荐 Controller | `AuthController` |
| 请求类 | `LoginRequest` |
| 响应 data 类型 | `LoginResponse` |
| 涉及实体类 | `Users` |
| 涉及数据表 | `users` |
| 主要 Service | `AuthService` 或 `UsersService` |

请求 JSON：

```json
{
  "account": "18812345678",
  "password": "123456"
}
```

请求字段：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `account` | string | 是 | 手机号或邮箱 |
| `password` | string | 是 | 密码 |

`LoginRequest` 建议字段：

```java
public class LoginRequest {
    private String account;
    private String password;
}
```

业务逻辑建议：

1. 根据 `account` 查询 `users` 表，可以匹配手机号或邮箱。
2. 判断用户是否存在。
3. 校验密码哈希。
4. 生成 JWT。
5. 返回 `LoginResponse`。

成功响应：

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

`LoginResponse` 建议字段：

```java
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private UserVO user;
}
```

### 2.3 当前用户

```http
GET /api/auth/me
Authorization: Bearer <accessToken>
```

后端对应关系：

| 项目 | 建议值 |
| --- | --- |
| 推荐 Controller | `AuthController` |
| 请求类 | 无，用户 ID 从 JWT 中解析 |
| 响应 data 类型 | `UserVO` |
| 涉及实体类 | `Users` |
| 涉及数据表 | `users` |
| 主要 Service | `AuthService` 或 `UsersService` |

业务逻辑建议：

1. 从请求头 `Authorization` 解析 JWT。
2. 从 JWT 中取出当前用户 ID。
3. 根据用户 ID 查询 `users` 表。
4. 返回 `UserVO`，不要返回密码字段。

成功响应：

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

后端对应关系：

| 项目 | 建议值 |
| --- | --- |
| 推荐 Controller | `EventsController` |
| 请求类 | 无 |
| 响应 data 类型 | `EventVO` |
| 涉及实体类 | `Events` |
| 涉及数据表 | `events` |
| 主要 Service | `EventsService` |

业务逻辑建议：

1. 从 `events` 表查询当前正在展示的活动。
2. 如果有多个活动，可以按 `startTime`、`status` 或 `isCurrent` 字段筛选。
3. 返回前端首页需要展示的活动信息。

成功响应：

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

`EventVO` 建议字段：

```java
public class EventVO {
    private Long id;
    private String title;
    private String location;
    private String description;
    private Boolean registrationOpen;
    private LocalDateTime registrationDeadline;
}
```

前端使用字段：

| 字段 | 说明 |
| --- | --- |
| `id` | 提交报名时使用 |
| `title` | 可用于动态替换首页标题 |
| `description` | 可用于动态替换首页说明 |
| `location` | 可用于动态替换城市 |
| `registrationOpen` | 可用于判断是否允许报名 |

### 3.2 公开作品列表

```http
GET /api/public/projects?status=submitted&pageSize=2
```

后端对应关系：

| 项目 | 建议值 |
| --- | --- |
| 推荐 Controller | `ProjectsController` |
| 请求类 | 查询参数：`status`、`pageSize` |
| 响应 data 类型 | `ProjectListResponse` |
| 涉及实体类 | `Projects` |
| 涉及数据表 | `projects` |
| 可能关联实体类 | `Tracks` |
| 可能关联数据表 | `tracks` |
| 主要 Service | `ProjectsService` |

请求参数：

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `status` | string | 否 | 前端当前传 `submitted` |
| `pageSize` | number | 否 | 前端当前传 `2` |

业务逻辑建议：

1. 从 `projects` 表查询公开展示的作品。
2. 如果项目表里只有 `trackId`，可以关联 `tracks` 表查出 `trackName`。
3. 返回最多 `pageSize` 条。
4. 如果没有数据，返回空列表即可，前端会保留原静态作品卡片。

成功响应：

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
        "coverUrl": "./assets/img/local-global-embers.png"
      }
    ],
    "total": 1
  }
}
```

`ProjectListResponse` 建议字段：

```java
public class ProjectListResponse {
    private List<ProjectVO> list;
    private Long total;
}
```

`ProjectVO` 建议字段：

```java
public class ProjectVO {
    private Long id;
    private String title;
    private Long trackId;
    private String trackName;
    private String tagline;
    private String description;
    private String coverUrl;
}
```

如果后端未启动或返回空列表，前端保留静态作品卡片。

## 4. 报名接口

### 4.1 提交报名

当前前端不改原页面，不新增复杂表单。用户登录后点击首页 `#apply` 区域里的报名按钮，前端会调用当前用户信息提交个人报名。

```http
POST /api/registrations
Authorization: Bearer <accessToken>
Content-Type: application/json
```

后端对应关系：

| 项目 | 建议值 |
| --- | --- |
| 推荐 Controller | `RegistrationsController` |
| 请求类 | `RegistrationRequest` |
| 响应 data 类型 | `RegistrationVO` |
| 主要涉及实体类 | `Registrations` |
| 主要涉及数据表 | `registrations` |
| 可能关联实体类 | `Users`、`Events`、`RegistrationMembers`、`Tracks` |
| 可能关联数据表 | `users`、`events`、`registration_members`、`tracks` |
| 主要 Service | `RegistrationsService` |

请求 JSON：

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

请求字段：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `eventId` | number | 是 | 活动 ID，来自 `/api/public/events/current` |
| `registrationType` | string | 是 | 当前前端固定传 `individual` |
| `teamName` | string/null | 否 | 当前前端固定传 `null` |
| `contactName` | string | 是 | 来自 `/api/auth/me` |
| `contactPhone` | string | 是 | 来自 `/api/auth/me` |
| `contactEmail` | string/null | 否 | 来自 `/api/auth/me` |
| `trackId` | number/null | 否 | 当前前端固定传 `null` |

`RegistrationRequest` 建议字段：

```java
public class RegistrationRequest {
    private Long eventId;
    private String registrationType;
    private String teamName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Long trackId;
}
```

业务逻辑建议：

1. 从 JWT 中解析当前 `userId`。
2. 根据 `eventId` 查询 `events` 表，确认活动存在且允许报名。
3. 检查同一用户是否已经报名，建议限制 `eventId + userId` 唯一。
4. 创建 `Registrations` 实体。
5. 如果你的表设计有报名成员表，再创建 `RegistrationMembers` 记录。
6. 返回 `RegistrationVO`。

成功响应：

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
    "teamName": null,
    "contactName": "张三",
    "contactPhone": "18812345678",
    "contactEmail": "zhangsan@example.com",
    "trackId": null,
    "status": "pending",
    "createdAt": "2026-07-05T16:00:00"
  }
}
```

`RegistrationVO` 建议字段：

```java
public class RegistrationVO {
    private Long id;
    private Long eventId;
    private Long userId;
    private String registrationType;
    private String teamName;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Long trackId;
    private String status;
    private LocalDateTime createdAt;
}
```

重复报名响应：

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
- `/api/registrations` 建议限制同一个 `eventId + userId` 只能报名一次。
- Controller 里接收的是 Request DTO，Mapper 操作的是数据库 Entity，返回给前端的是 VO / Response DTO。
- 自动生成的 `UsersController`、`RegistrationsController` 等可以保留；登录注册建议单独建 `AuthController`，业务含义更清楚。
