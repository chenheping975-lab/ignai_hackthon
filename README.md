# IGNAI AI Skillathon Platform

面向 IGNAI 线下 AI Skillathon / Hackathon 的活动官网、报名提交、作品展示与赛事管理平台。

项目目标不是再做一个重后台系统，而是让一场黑客松顺滑发生：前台讲清楚活动故事，后台承接报名、组队、审核、通知、提交、开放评分、作品曝光和飞书备份。

## 当前状态

- 前端已拆成三层链路：社区活动首页、单场黑客松活动页、单场作品平台，沿用 IGNAI 官网的火炬、橙红渐变、暖纸底、黑色 CTA 和品牌水印。
- `/root/` 后台管理静态 MVP 已完成，支持概览、活动管理、可编辑报名问卷、报名管理、项目看板、赛事预览和邮件中心的 mock 交互。
- 后端已同步为 Spring Boot / MyBatis / MySQL 工程，当前已接入注册、登录、当前活动和公开作品列表；后台管理接口已完成 v0.4 规划，待实现。
- 数据库、OpenAPI、概要设计、业务逻辑判断和研发 PDF 已整理到 `docs/`、`database/`、`api/`。
- MVP 阶段不引入评委独立角色，不引入视频上传。

## 本地启动

前端静态服务：

```bash
cd frontend
python3 -m http.server 5173
```

打开：

```text
http://localhost:5173/
http://localhost:5173/event.html?id=ignai-ai-skillathon
http://localhost:5173/projects.html?event=ignai-ai-skillathon
http://localhost:5173/root/
```

后端编译检查：

```bash
cd backend
./mvnw -q -DskipTests package
```

后端本地运行建议：

```bash
cd backend
./mvnw spring-boot:run
```

> 当前仓库未提交 `application.properties`，首次运行前需要按本机 MySQL 配置补齐 `spring.datasource.*`，数据库名可使用 `database/schema.sql` 中的 `ignai_hackthon`。

## 官网与参考站

| 类型 | 地址 | 本项目如何使用 |
| --- | --- | --- |
| IGNAI 官网 | https://www.ignai.cn/ | 作为主视觉、品牌 IP、内容语气和首页叙事来源 |
| 本地官网工程 | `/Users/mac/qianzhu Vault/project/IGN AI 官网` | 复用 logo、火炬、橙红渐变、暖纸底和品牌资产 |
| AI Hack Tour | https://aihacktour.com/ | 只参考黑客松流程、作品展示和曝光路径，不复用其紫绿视觉风格 |

## 产品边界

平台和 IGNAI 官网保持友链关系。黑客松平台可以复用官网品牌资产和社区叙事，但不强行接入官网文章、成员、记录等内容模块。

MVP 包含：

- 活动首页
- 单场黑客松活动页
- 单场黑客松作品平台
- 个人 / 团队报名
- 动态报名字段配置
- AI 标签和报名摘要
- 管理员审核
- 邮件草稿生成与人工确认发送
- 队伍管理
- 作品提交
- 作品展示
- 开放评分和投票
- 本地数据 / 文件备份
- 飞书 Base / Drive / 导出同步记录

MVP 不包含：

- 独立评委账号、评委分配、评委专属评分后台
- 视频上传、视频外链、视频转码、视频在线播放
- 多租户 SaaS
- 完整官网 CMS
- AI 自动替代管理员做最终审核或评分

## 技术栈

| 层级 | 方案 |
| --- | --- |
| 前端原型 | HTML / CSS / JavaScript |
| 后端实训栈 | Spring Boot 3 / MyBatis / Java 17 |
| 分层结构 | Controller / Service / Mapper / POJO |
| 数据库 | MySQL 8 |
| 接口文档 | OpenAPI 3 |
| 文件存储 | 本地文件备份，数据库保存 metadata |
| 外部集成 | AI service、飞书 service、邮件 service 可插拔 |

## 端口规划

| 服务 | 端口 / 路径 | 说明 |
| --- | --- | --- |
| 前端预览 | `5173` | 当前静态页面服务 |
| 单场活动页 | `/event.html?id=...` | 当前活动报名、赛道、状态和提交要求 |
| 单场作品平台 | `/projects.html?event=...` | 当前活动作品展示、筛选、点赞 |
| 后台入口 | `/root/` | 规划中的管理后台页面入口 |
| Spring Boot API | `8080` | 默认后端服务端口 |
| MySQL | `3306` | 业务数据库 |
| API 前缀 | `/api` | 后端接口统一前缀 |
| 文件目录 | `backend/uploads` | MVP 本地附件备份目录，后续由后端配置落盘位置 |

## 目录结构

```text
ignai_hackthon/
  frontend/                 # IGNAI 品牌风格前端原型
  backend/                  # Spring Boot + MyBatis 后端工程
  database/schema.sql        # MySQL 8 建表脚本
  api/openapi.yaml           # OpenAPI 接口设计
  docs/                      # PRD、概要设计、详细设计、研发 PDF
  scripts/                   # 文档生成脚本
  tasks/                     # 研发任务入口
```

## 核心业务流

```text
活动配置
-> 报名 / 组队
-> AI 标签与摘要
-> 管理员审核
-> 邮件草稿与发送
-> 项目提交
-> 开放评分 / 投票
-> 作品展示
-> 飞书备份
```

## 关键文档

- [项目方案总览](docs/00-项目方案总览.md)
- [概要设计](docs/01-概要设计.md)
- [数据库设计](docs/02-详细设计-数据库设计.md)
- [API 端口与接口设计](docs/03-API端口与接口设计.md)
- [业务逻辑判断](docs/04-业务逻辑判断.md)
- [文件夹系统](docs/05-文件夹系统.md)
- [迭代计划](docs/06-迭代计划.md)
- [后台管理系统规划](docs/07-后台管理系统规划.md)
- [后台管理与赛事预览 UI 梳理](docs/08-后台管理与赛事预览UI梳理.md)
- [后台管理原始需求](docs/原始需求_后台管理.md)
- [后台管理 TODO](docs/TODO-后台管理系统.md)
- [UI 信息架构 TODO](docs/TODO-UI信息架构.md)
- [PRD v0.2](docs/prd/黑客松管理平台-PRD-v0.2.md)
- [研发说明书 PDF](docs/pdf/黑客松管理平台-研发说明书-v0.3.pdf)
- [OpenAPI](api/openapi.yaml)
- [MySQL Schema](database/schema.sql)

## 建议开发顺序

1. 管理员登录与 session。
2. 活动配置、赛道配置、报名字段配置。
3. 个人 / 团队报名。
4. 报名列表、筛选、审核状态流转。
5. AI 标签和摘要 mock。
6. 邮件模板、AI 邮件草稿、人工确认发送。
7. 队伍管理。
8. 项目提交与附件上传。
9. 作品展示页。
10. 开放评分与投票。
11. 飞书 CSV / JSON 导出。
12. 飞书 Base / Drive 自动同步。

## 设计原则

- 首页传达活动精神和活动入口；报名、提交、作品展示跟随单场活动页展开。
- 视觉优先沿用 IGNAI IP：火炬、橙红渐变、暖纸底、真实行动感。
- 对标站只参考流程和作品曝光结构，不直接复刻视觉。
- 本地数据库是主数据源，飞书是备份和协作视图。
- AI 只做辅助整理、摘要和草稿，不替代管理员决策。
- MVP 先跑通单场赛事闭环，再扩展多场活动和更完整后台。
