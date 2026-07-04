# IGNAI Hackathon Management Platform

IGNAI 黑客松管理平台用于支撑线下 AI Skillathon / Hackathon 的完整流程：活动配置、报名组队、AI 标签、审核通知、项目提交、开放评分、投票互动、作品展示、飞书备份。

## 官网与对标网站

| 类型 | 地址 | 在本项目中的关系 |
| --- | --- | --- |
| IGNAI 官网 | https://www.ignai.cn/ | 仅作为友链、品牌视觉和首页内容参考，不强制接入官网文章、成员、记录等模块。本地参考工程：`/Users/mac/qianzhu Vault/project/IGN AI 官网` |
| 对标网站 | https://aihacktour.com/products | 重点参考作品展示、活动页、项目卡片、线上曝光和用户互动路径。MVP 不照搬视频能力，不引入视频上传。 |

## MVP 决策

- 独立黑客松平台，和官网保持友链关系。
- Java Web / Servlet / JSP / MySQL 能胜任实训版开发。
- MVP 先支持单场赛事，不做多租户 SaaS。
- 撤掉“评委”独立角色，改为开放评分：参赛者、现场观众或公开访客都可以按规则评分。
- MVP 不引入视频上传、视频外链、视频转码、视频在线播放。
- 报名、作品、附件 metadata 本地保存；飞书作为备份和协作面板。

## 目录

```text
ignai_hackthon/
  frontend/                 # 无依赖静态前端原型
  backend/java-web/          # Java Web 实训栈后端骨架
  database/schema.sql        # MySQL 8 建表脚本
  api/openapi.yaml           # 后端 API 设计
  docs/                      # PRD、概要设计、详细设计、PDF
  tasks/                     # 研发任务入口
```

## 本地预览

前端原型：

```bash
cd frontend
python3 -m http.server 5173
```

打开 `http://localhost:5173/`。

后端建议端口：

- 前端静态预览：`5173`
- Java Web 后端：`8080`
- MySQL：`3306`
- 本地文件备份目录：`backend/java-web/src/main/webapp/uploads`
- API 前缀：`/api`

## 研发入口

- [概要设计](docs/01-概要设计.md)
- [数据库设计](docs/02-详细设计-数据库设计.md)
- [API 端口与接口设计](docs/03-API端口与接口设计.md)
- [业务逻辑判断](docs/04-业务逻辑判断.md)
- [OpenAPI](api/openapi.yaml)
- [MySQL Schema](database/schema.sql)
- [研发 PDF](docs/pdf/黑客松管理平台-研发说明书-v0.3.pdf)
