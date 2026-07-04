# Java Web 后端骨架

本目录是实训版后端结构，贴合课程里的 Servlet / JSP / JDBC / MVC。

## 建议运行环境

- JDK 8+
- Maven 3.8+
- Tomcat 9
- MySQL 8

## 分层

```text
control/        Servlet 控制器，负责请求解析和响应
service/        业务接口
service/impl/   业务实现
dao/            数据访问接口
dao/impl/       JDBC 实现
model/          实体和枚举
utils/          DB、JSON、文件类型校验等工具
integration/    AI、飞书、邮件等外部适配
```

## 接口入口

- `GET /api/health`
- `GET /api/public/events/current`
- `POST /api/public/events/{eventId}/registrations`
- `PATCH /api/admin/registrations/{id}/review`
- `POST /api/projects`
- `POST /api/public/projects/{id}/ratings`
- `POST /api/admin/feishu/sync`

完整接口见 `../../api/openapi.yaml`。

## 下一步实现顺序

1. 接入 `database/schema.sql`。
2. 完成管理员登录和 session。
3. 完成活动配置、表单字段配置。
4. 完成报名提交和报名审核。
5. 完成 AI 标签 mock、邮件草稿、飞书导出 mock。
6. 完成项目提交、文件上传、开放评分。
