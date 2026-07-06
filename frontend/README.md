# 前端原型

这是一个无依赖静态原型，主视觉沿用 IGNAI 官网的暖纸底、黑色 CTA、橙红火炬和品牌 IP 色。页面先讲活动故事，再承接黑客松报名、作品、开放评分和飞书同步流程。

## 预览

```bash
cd frontend
python3 -m http.server 5173
```

打开 `http://localhost:5173/`。

## 自动化测试

按钮 / 交互自动化测试基于 [Playwright Test](https://playwright.dev)，覆盖主题切换、hero CTA、nav 锚点、demo tabs、评分、提交流程、Reveal 入场、跑马灯 11 个用例。

```bash
cd frontend
npm install                 # 首次：装 @playwright/test
npx playwright install chromium   # 首次：下载 chromium headless
npm test                    # 跑全部用例（headless）
npm run test:headed         # 有头模式，便于肉眼观察
npm run test:report         # 打开 HTML 报告
```

Playwright 的 `webServer` 会自动拉起 `python3 -m http.server 5173`，无需手动启动。

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

### v0.5.0 — 2026-07-06

Hero slogan 重写：从抽象社区宣言换成主张 + 收益型文案。

- eyebrow：`CHANGSHA AI COMMUNITY · AI SKILLATHON` → `IGNITE BEFORE AGI`（把社区主张从 brand-stage 装饰提升为主 slogan）。
- h1：`连接创造者，点燃一场 AI 作品现场。` → `行动创造价值，人链接人。`
- lead：从"IGNAI 把长沙的学生、开发者、设计师、创作者和创业者聚到一起..."（团队视角）改为"两天一夜的 IGNAI 黑客松：你带来一个在意的问题，带走能跑的 Demo、能继续合作的同伴、和被讨论过的作品。"（用户收益视角）。
- brand-stage 装饰卡：`IGNITE BEFORE AGI` → `LOCAL ROOTS · GLOBAL SIGNAL`，副标加"长沙 · 线下黑客松"，避免和 hero eyebrow 重复。

涉及文件：`index.html`、`README.md`。

### v0.4.0 — 2026-07-06

#apply 段落重设计：从"幕后管理 + 参赛者操作台"混合视角，收敛为纯参赛者旅程。

- 标题：`BEHIND THE STAGE` → `YOUR 48 HOURS`；h2 改为"从报名到展示，你只需要走完这四步。"
- system-flow：6 格（报名/AI 标签/审核通知/作品提交/开放评分/飞书备份）→ 4 格（报名/现场冲刺/提交作品/展示评分）。AI 标签、审核通知、飞书备份迁到独立后台。
- demo-console：tabs 从 4 个（报名/作品/评分/飞书）→ 3 个（报名/作品/评分）；头部文案"Interactive Demo / 参赛流程控制台"改为"参赛流程预览 / 先看一眼你将体验什么"；状态"未登录预览"改为"游客预览"。
- 去掉所有技术 badge（draft / public / sync / drive / retry / 1-5 / live / 可编辑 / 无视频 / Demo）。
- apply-actions："提交报名 Demo" → "提交报名"；"登录 / 切换账号" → 状态化（"登录"或"切换账号"）；feedback 文案从"登录后可提交报名；未连接后端时会进入本地 Demo 模式"改为"先登录或注册账号，就能完成报名。"
- 测试同步：demo-tabs 用例从 4 tab 改 3 tab，11/11 通过。

涉及文件：`index.html`、`assets/css/styles.css`、`assets/js/app.js`、`tests/buttons.spec.ts`、`TASKS/apply-section-redesign.md`、`README.md`。

### v0.3.0 — 2026-07-06

新增按钮 / 交互自动化测试，零侵入业务代码。

- 引入 Playwright Test（`@playwright/test`）作为 devDependency，新增 `package.json`、`playwright.config.ts`、`.gitignore`、`TASKS/` 目录。
- `tests/buttons.spec.ts` 覆盖 11 个用例：页面初始加载、主题切换 + localStorage 持久化、hero CTA 滚动、nav 锚点、demo-tabs 切换 + 动画、评分按钮、未登录提交跳转、Reveal 入场、跑马灯拼接。
- `playwright.config.ts` 内置 webServer，自动拉起静态服务；强制 `colorScheme: 'dark'` 让预闪脚本稳定走 dark 分支。
- README 增加测试运行说明（`npm test` / `npm run test:headed` / `npm run test:report`）。

涉及文件：`package.json`、`playwright.config.ts`、`.gitignore`、`tests/buttons.spec.ts`、`TASKS/button-automated-test.md`、`README.md`。

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
