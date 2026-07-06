# Todo · 前端按钮自动化测试

> 版本：v1 · 创建于 2026-07-06
> 目标：为 `frontend/index.html` 关键按钮与交互建立可重复运行的自动化测试，覆盖明暗切换、hero CTA、nav 锚点、demo tabs、评分、提交流程。
> 技术栈：Playwright Test（@playwright/test，Chromium headless）

## 任务分析

- 项目为纯静态 HTML/CSS/JS，无构建系统、无现有测试。
- Node v22.16.0 + npx 10.9.2 已就绪。
- 选项对比：Cypress（重、需 GUI）、jsdom（无法测真实 DOM/动画）、Playwright Test（业界标准、内置 webServer、headless、对静态站零侵入）。**选 Playwright Test**。
- 业务代码不动，只在 `frontend/` 下新增 `package.json`、`playwright.config.ts`、`tests/`、`.gitignore`、`TASKS/`。

## 测试用例清单

| # | 用例 | 期望 |
|---|------|------|
| 1 | 页面初始加载 | `<html>` 默认带 `dark` class，title 正确 |
| 2 | 主题切换按钮 | 点击后 html class 在 dark/light 间切换，localStorage 写入 `ignai-theme` |
| 3 | 主题持久化 | reload 后主题保持 |
| 4 | hero "提交报名" | 点击后页面滚动到 `#apply` |
| 5 | hero "查看作品" | 点击后滚动到 `#showcase` |
| 6 | nav 锚点 | 点击 "为什么/行动/流程/作品" 平滑滚动到对应 section |
| 7 | demo-tabs 切换 | 4 个 tab 依次点击，panel 内容更新、active class 切换、`is-switching` 动画类被触发 |
| 8 | 评分按钮 | 点击 `data-score="4"` 后 `#score-demo-text` 文本更新、按钮 active |
| 9 | 未登录提交报名 | 点击 `#submit-registration-button` 后 `#apply-feedback` 显示 error 类与提示文本 |
| 10 | Reveal 入场 | 滚动后 `[data-reveal]` 元素加上 `is-visible` |
| 11 | 跑马灯连续性 | `.signal-strip div` 子元素数量翻倍（脚本复制了一份） |

## 步骤

- [x] 1. 创建 Todo 文档（本文）
- [x] 2. 初始化 `package.json` + 安装 `@playwright/test`
- [x] 3. 写 `playwright.config.ts`（webServer 拉起静态服务、chromium headless）
- [x] 4. 写 `tests/buttons.spec.ts` 覆盖上表 11 个用例
- [x] 5. 加 `.gitignore`（排除 `node_modules/`、`test-results/`、`playwright-report/`）
- [x] 6. 跑 `npx playwright test`，所有用例通过（11/11 全绿，耗时 ~21s）
- [x] 7. 更新 `frontend/README.md` 加 v0.3.0 更新日志 + 测试运行说明
- [x] 8. commit + push

## 进度记录

- 2026-07-06 · v1 · 创建 Todo，开始执行
- 2026-07-06 · v2 · 初次跑测试 4/11 通过；定位 4 类根因（系统颜色方案、smooth scroll 超时、未登录提交实际跳转、IntersectionObserver 中间帧跳过）
- 2026-07-06 · v3 · 修复 4 处后 11/11 全绿

