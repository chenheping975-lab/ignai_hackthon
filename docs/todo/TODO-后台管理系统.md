# TODO - 后台管理系统规划

版本：v0.1
日期：2026-07-08
状态：进行中

## 任务体量判断

本轮是中等偏大的产品与接口规划任务，不直接实现后台代码。交付目标是把后台管理系统的原始需求、功能边界、API 契约、文档结构和版本记录整理到仓库，给后续前后端开发使用。

## 执行清单

| 步骤 | 状态 | 说明 |
| --- | --- | --- |
| 1. 同步并审阅最新后端、数据库、API 文档 | 已完成 | 已同步 `origin/main`；确认当前真实 Controller 为 `/api/auth/*` 和 `/api/public/*`，后台接口仍主要是规划状态。 |
| 2. 创建后台管理 TODO 文档并记录任务体量 | 已完成 | 本文件作为本轮任务看板。 |
| 3. 归档原始需求_后台管理 | 已完成 | 已将用户本轮后台管理需求归档到 `docs/原始需求_后台管理.md`，并做初步归类。 |
| 4. 规划后台管理系统与文档结构 | 已完成 | 已新增 `docs/07-后台管理系统规划.md`，并同步总览、数据库设计、文件夹系统、README 和联调文档入口。 |
| 5. 更新 API 设计与 OpenAPI | 已完成 | 已更新 `docs/03-API端口与接口设计.md` 与 `api/openapi.yaml` 到 v0.4 / 0.4.0。 |
| 6. 校验、提交并推送 | 已完成 | `git diff --check` 通过；OpenAPI YAML 解析通过；`backend ./mvnw -q -DskipTests package` 通过；`frontend npm test -- --reporter=list` 11 项通过。本轮修改将提交并推送到 `main`。 |

## 关键待确认

| 问题 | 当前建议 |
| --- | --- |
| `/root` 是后台页面路径还是 API 前缀？ | 建议 `/root/` 作为后台管理页面入口，API 继续使用已有 `/api/admin/*`，超级管理员相关能力使用 `/api/root/*`。 |
| root 是否需要独立数据库角色？ | MVP 可在 `users.role` 中增加 `root`，或使用 `users.role='admin'` + `system_settings.root_owner_email`。为贴合需求，文档先按 `root/admin/participant` 三类规划。 |
| 创始人邮箱具体是哪一个？ | 文档先使用配置项 `root_owner_email`，不写死真实邮箱。 |
| 邮件发送接哪家服务？ | MVP 只定义草稿、预览、人工确认、发送记录；具体 SMTP/飞书邮箱服务待实现阶段确认。 |
