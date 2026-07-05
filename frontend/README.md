# 前端原型

这是一个无依赖静态原型，主视觉沿用 IGNAI 官网的暖纸底、黑色 CTA、橙红火炬和品牌 IP 色。页面先讲活动故事，再承接黑客松报名、作品、开放评分和飞书同步流程。

## 预览

```bash
cd frontend
python3 -m http.server 5173
```

打开 `http://localhost:5173/`。

## 页面内容

- 首页活动叙事和 IGNAI 官网友链。
- 官网和对标网站友链。
- 核心流程拆解。
- 登录 / 注册后的账号状态、退出、切换账号。
- 参赛流程交互 Demo：报名、作品、评分、飞书。
- 作品展示预览。
- 端口和 API 摘要。

## 联调说明

- 前端请求前缀统一为 `/api`。
- 当前后端已实现 `POST /api/auth/register` 和 `POST /api/auth/login`。
- 活动、报名、作品列表接口还在后端补齐中；前端默认走 Demo fallback，不会在静态预览中自动请求未实现接口。
- 后端补齐后，可在控制台设置 `localStorage.ignai_enable_public_api = "1"`、`localStorage.ignai_enable_registration_api = "1"` 打开对应联调。
