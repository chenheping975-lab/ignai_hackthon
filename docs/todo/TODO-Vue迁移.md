# TODO-Vue迁移

版本：v1.0
创建日期：2026-07-08
状态：已完成

## Phase 1: 项目初始化

| # | 任务 | 状态 |
|---|------|------|
| 1 | 备份旧文件到 _old/ | ✅ |
| 2 | Vite + Vue 项目初始化 | ✅ |
| 3 | 安装依赖 (vue-router, pinia, axios) | ✅ |
| 4 | 配置 vite.config.js (API 代理) | ✅ |
| 5 | 迁移 styles.css 到 src/assets/css/ | ✅ |
| 6 | 创建 src/api/index.js (axios 封装) | ✅ |
| 7 | 创建 Pinia stores (auth, theme) | ✅ |
| 8 | 创建路由配置 | ✅ |

## Phase 2: 布局与路由

| # | 任务 | 状态 |
|---|------|------|
| 9 | App.vue (根组件) | ✅ |
| 10 | AppHeader.vue | ✅ |
| 11 | AppFooter.vue | ✅ |

## Phase 3: 公开页面

| # | 任务 | 状态 |
|---|------|------|
| 12 | HomeView.vue | ✅ |
| 13 | LoginView.vue | ✅ |
| 14 | RegisterView.vue | ✅ |
| 15 | EventsView.vue | ✅ |
| 16 | EventView.vue | ✅ |
| 17 | ProjectsView.vue | ✅ |

## Phase 4: 后台管理

| # | 任务 | 状态 |
|---|------|------|
| 18 | AdminView.vue | ✅ |

## Phase 5: 清理与测试

| # | 任务 | 状态 |
|---|------|------|
| 19 | 清理旧文件 | ✅ |
| 20 | 验证全链路 | ✅ |
| 21 | Git commit + push | ✅ |
