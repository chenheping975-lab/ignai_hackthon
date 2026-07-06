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

## 更新日志

### v0.2.0 — 2026-07-06

对标 IGNAI 官网 ignai 主题，升级双主题 token 体系与全场动效。

- 新增明暗双主题：默认 `dark`，header 右上角加 theme-toggle 按钮，localStorage 持久化 + 首次访问跟随 `prefers-color-scheme`，预闪脚本避免 reload 白屏。
- 全场 Reveal 入场动效（参考官网 `Reveal.js`）：opacity 0→1、y 18→0、brightness/contrast/blur 滤镜过渡，0.8s `cubic-bezier(0.22,1,0.36,1)`，IntersectionObserver once + threshold 0.18。
- 微交互升级：卡片 hover 升起 + radial 光晕跟随鼠标、按钮 hover 抬起 + 阴影、demo-tab 切换 panel 淡入、跑马灯首尾拼接无空隙 + hover 暂停 + 左右羽化遮罩。
- Hero 装饰动效：火炬摇曳（torch-flicker）、渐变光晕浮动（hero-gradient-drift）、品牌框架呼吸（hero-frame-breathe）。
- 暗色视觉差异化：hero 用官网同款 radial(255,122,24) + radial(93,169,255) + 深底渐变；主按钮用橙渐变 `linear-gradient(135deg,#ff7a18→#ff9a3c)`。
- 加 `prefers-reduced-motion` 降级，无障碍友好。

涉及文件：`index.html`、`assets/css/styles.css`、`assets/js/app.js`。

### v0.1.0 — 2026-07-04

初始静态原型：暖纸底视觉、活动叙事、参赛流程交互 Demo（报名/作品/评分/飞书）、作品展示预览、auth 状态联动。
