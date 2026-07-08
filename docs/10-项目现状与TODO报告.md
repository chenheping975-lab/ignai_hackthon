# 项目现状与 TODO 报告

版本：v1.0
日期：2026-07-08
状态：自测完成

---

## 一、项目目标

搭建一个黑客松活动管理平台，覆盖活动全生命周期：

```
活动创建 → 报名收集 → AI 初筛 → 人工审核 → 邮件通知 → 项目提交 → 作品展示 → 投票
```

**核心价值：** 后台动态管理活动内容，前台实时展示；报名信息自动归类，审核后个性化邮件通知。

---

## 二、技术栈现状

| 层 | 技术 | 状态 |
|----|------|------|
| 前端 | Vue 3 + Vite + Vue Router + Pinia | ✅ 已迁移，7 个页面可用 |
| 后端 | Spring Boot 3.5.16 + MyBatis + Java 17 | ✅ 运行正常 |
| 数据库 | MySQL 8（远程 118.89.62.40） | ✅ 连接正常 |
| 认证 | JWT Bearer Token | ✅ 注册/登录可用 |
| 代理 | Vite proxy /api → localhost:8080 | ✅ 前后端通信正常 |

---

## 三、已实现功能（自测通过）

### 3.1 参赛者端

| 功能 | 前端页面 | 后端接口 | 验证结果 |
|------|----------|----------|----------|
| 用户注册 | RegisterView.vue | POST /api/auth/register | ✅ 可用 |
| 用户登录 | LoginView.vue | POST /api/auth/login | ✅ 可用，返回 JWT + 用户信息 |
| 活动列表 | EventsView.vue | GET /api/public/events | ✅ 可用 |
| 活动详情 | EventView.vue | GET /api/public/events/current | ✅ 可用 |
| 报名提交 | EventView.vue 表单 | POST /api/registrations | ✅ 可用（需登录） |
| 作品展示 | ProjectsView.vue | GET /api/public/projects | ✅ 可用（当前无数据） |
| 点赞 | ProjectsView.vue | — | ⚠️ 前端 localStorage 实现，未对接后端 |

### 3.2 前端基础设施

| 功能 | 说明 | 状态 |
|------|------|------|
| SPA 路由 | 7 个页面，懒加载 | ✅ |
| 全局状态 | Pinia auth + theme store | ✅ |
| 深色/浅色主题 | 主题切换 + 系统偏好检测 | ✅ |
| 响应式布局 | 移动端适配 | ✅ |
| API 层 | axios 封装，自动附带 JWT | ✅ |
| Mock 数据 | 活动 + 项目 mock 数据 | ✅ |

### 3.3 后台管理（前端 UI 存在，数据为 mock）

| 功能 | 前端 | 后端 | 状态 |
|------|------|------|------|
| 仪表盘 | AdminView.vue 概览面板 | 无接口 | ⚠️ 硬编码数字 |
| 报名列表 | AdminView.vue 表格 + 筛选 | 无接口 | ⚠️ 硬编码数据 |
| 报名审核 | AdminView.vue 详情抽屉 | 无接口 | ⚠️ 仅本地状态 |
| 活动管理 | AdminView.vue 占位 | 无接口 | ❌ 未实现 |
| 项目看板 | AdminView.vue 占位 | 无接口 | ❌ 未实现 |
| 邮件管理 | AdminView.vue 占位 | 无接口 | ❌ 未实现 |

---

## 四、差距分析

### 4.1 后端缺失接口

| 优先级 | 接口 | 用途 | 影响 |
|--------|------|------|------|
| **P0** | GET /api/auth/me | 登录后获取当前用户信息 | 前端已调用，404 |
| **P0** | GET /api/registrations/my | 用户查看自己的报名状态 | 前端已调用，404 |
| **P0** | POST /api/root/bootstrap | root 账号初始化 | 后台入口不存在 |
| **P0** | POST /api/root/login | root 登录 | 后台入口不存在 |
| **P0** | GET /api/admin/registrations | 后台报名列表 | 后台核心功能 |
| **P0** | GET /api/admin/registrations/{id} | 报名详情 | 后台核心功能 |
| **P0** | PATCH /api/admin/registrations/{id}/review | 审核通过/拒绝 | 后台核心功能 |
| **P1** | POST/PUT /api/admin/events | 活动 CRUD | 活动管理 |
| **P1** | GET /api/admin/users | 用户列表 | 用户管理 |
| **P1** | POST /api/admin/mail/send | 发送邮件 | 邮件通知 |
| **P2** | POST /api/projects | 项目提交 | 活动后流程 |
| **P2** | POST /api/projects/{id}/vote | 投票 | 互动功能 |
| **P2** | GET /api/public/events/{id}/tracks | 赛道信息 | 前端已调用 |

### 4.2 前端缺失功能

| 优先级 | 功能 | 说明 |
|--------|------|------|
| **P0** | AdminView 对接真实 API | 当前全部硬编码 mock |
| **P0** | 报名状态查看页 | 用户登录后查看自己的报名结果 |
| **P1** | 项目提交表单 | 参赛者上传作品 |
| **P1** | 邮件管理面板 | 模板编辑 + 发送记录 |
| **P2** | 投票功能对接后端 | 当前仅 localStorage |

### 4.3 基础设施缺失

| 优先级 | 项目 | 说明 |
|--------|------|------|
| **P1** | 文件上传接口 | 后端无 Multipart 配置，无上传接口 |
| **P1** | 文件存储目录 | 本地 /uploads/ 或云 OSS |
| **P2** | 错误处理统一 | 后端异常直接返回 500，缺少友好错误信息 |
| **P2** | 数据库密码环境变量化 | 当前硬编码在 application.properties |

---

## 五、TODO 清单

### Phase 1：打通基础链路（P0）

| # | 任务 | 负责 | 预估 |
|---|------|------|------|
| 1 | 后端实现 GET /api/auth/me | 后端 | 0.5h |
| 2 | 后端实现 GET /api/registrations/my | 后端 | 1h |
| 3 | 后端实现 POST /api/root/bootstrap（一次性初始化） | 后端 | 1h |
| 4 | 后端实现 POST /api/root/login（独立 JWT） | 后端 | 1h |
| 5 | 后端实现 GET /api/admin/registrations（列表+筛选+分页） | 后端 | 2h |
| 6 | 后端实现 GET /api/admin/registrations/{id}（详情） | 后端 | 0.5h |
| 7 | 后端实现 PATCH /api/admin/registrations/{id}/review（审核） | 后端 | 1h |
| 8 | 前端 AdminView 对接上述 API 替换 mock 数据 | 前端 | 2h |
| 9 | 前端新增报名状态页面（/my/registrations） | 前端 | 1h |

### Phase 2：活动与邮件管理（P1）

| # | 任务 | 负责 | 预估 |
|---|------|------|------|
| 10 | 后端实现 POST/PUT /api/admin/events（活动 CRUD） | 后端 | 2h |
| 11 | 后端实现 GET /api/admin/users（用户列表） | 后端 | 1h |
| 12 | 后端实现邮件模板 CRUD（/api/admin/mail-templates） | 后端 | 2h |
| 13 | 后端实现 POST /api/admin/registrations/{id}/mail-draft | 后端 | 2h |
| 14 | 后端实现 POST /api/admin/mail/send | 后端 | 1h |
| 15 | 后端配置文件上传（Multipart + /uploads/） | 后端 | 1h |
| 16 | 前端活动管理面板对接 API | 前端 | 2h |
| 17 | 前端邮件管理面板对接 API | 前端 | 2h |

### Phase 3：项目提交与展示（P2）

| # | 任务 | 负责 | 预估 |
|---|------|------|------|
| 18 | 后端实现 POST /api/projects（项目提交+文件上传） | 后端 | 2h |
| 19 | 后端实现 GET /api/admin/projects（后台项目列表） | 后端 | 1h |
| 20 | 后端实现 PATCH /api/admin/projects/{id}/showcase | 后端 | 1h |
| 21 | 后端实现 POST /api/projects/{id}/vote（投票） | 后端 | 1h |
| 22 | 后端实现 GET /api/public/events/{id}/tracks | 后端 | 0.5h |
| 23 | 前端项目提交页面 | 前端 | 2h |
| 24 | 前端投票功能对接后端 | 前端 | 1h |
| 25 | 前端项目看板对接 API | 前端 | 2h |

### Phase 4：收尾与优化（P3）

| # | 任务 | 负责 | 预估 |
|---|------|------|------|
| 26 | 后端异常统一处理（@ControllerAdvice） | 后端 | 1h |
| 27 | 数据库密码改为环境变量 | 后端 | 0.5h |
| 28 | 前端错误提示优化（toast 通知） | 前端 | 1h |
| 29 | 全链路集成测试 | 测试 | 2h |
| 30 | 部署文档编写 | 文档 | 1h |

---

## 六、时间线建议

| 阶段 | 内容 | 预估工时 | 建议完成 |
|------|------|----------|----------|
| Phase 1 | 基础链路打通 | ~10h | 第 1 周 |
| Phase 2 | 活动与邮件管理 | ~12h | 第 2 周 |
| Phase 3 | 项目提交与展示 | ~10h | 第 3 周 |
| Phase 4 | 收尾优化 | ~5h | 第 3 周 |

**总预估：~37 小时**

---

## 七、变更记录

| 版本 | 日期 | 变更 |
|------|------|------|
| v1.0 | 2026-07-08 | 自测完成后输出，覆盖目标、现状、差距、TODO |
