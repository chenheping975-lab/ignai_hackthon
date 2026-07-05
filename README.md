# IGNAI AI Skillathon Platform

面向 IGNAI 线下 AI Skillathon / Hackathon 的活动官网、报名提交、作品展示与赛事管理平台。

项目目标不是再做一个重后台系统，而是让一场黑客松顺滑发生：前台讲清楚活动故事，后台承接报名、组队、审核、通知、提交、开放评分、作品曝光和飞书备份。

## 当前状态

- 前端首页已完成第一版视觉原型，沿用 IGNAI 官网的火炬、橙红渐变、暖纸底、黑色 CTA 和品牌水印。
- 后端已提供 Java Web / Servlet / JSP / MySQL 的实训版工程骨架。
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
```

后端编译检查：

```bash
cd backend/java-web
mvn -q -DskipTests package
```

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
| 后端实训栈 | Java Web / Servlet / JSP |
| 分层结构 | Control / Service / DAO / Model |
| 数据库 | MySQL 8 |
| 接口文档 | OpenAPI 3 |
| 文件存储 | 本地文件备份，数据库保存 metadata |
| 外部集成 | AI service、飞书 service、邮件 service 可插拔 |

## 端口规划

| 服务 | 端口 / 路径 | 说明 |
| --- | --- | --- |
| 前端预览 | `5173` | 当前静态页面服务 |
| Java Web | `8080` | Tomcat / Servlet 容器 |
| MySQL | `3306` | 业务数据库 |
| API 前缀 | `/api` | 后端接口统一前缀 |
| 文件目录 | `backend/java-web/src/main/webapp/uploads` | MVP 本地附件备份目录 |

## 目录结构

```text
ignai_hackthon/
  frontend/                 # IGNAI 品牌风格前端原型
  backend/java-web/          # Java Web 实训栈后端骨架
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

- 首页先讲活动故事，再承接平台能力。
- 视觉优先沿用 IGNAI IP：火炬、橙红渐变、暖纸底、真实行动感。
- 对标站只参考流程和作品曝光结构，不直接复刻视觉。
- 本地数据库是主数据源，飞书是备份和协作视图。
- AI 只做辅助整理、摘要和草稿，不替代管理员决策。
- MVP 先跑通单场赛事闭环，再扩展多场活动和更完整后台。
