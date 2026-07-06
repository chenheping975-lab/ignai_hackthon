import { defineConfig, devices } from "@playwright/test";

/**
 * Playwright 配置
 * - webServer: 测试启动前自动拉起 python http.server 5173
 * - 测试文件: tests/*.spec.ts
 * - 浏览器: chromium headless
 */
export default defineConfig({
  testDir: "./tests",
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  workers: 1,
  reporter: [["list"], ["html", { open: "never" }]],
  timeout: 20_000,
  expect: { timeout: 5_000 },

  use: {
    baseURL: "http://127.0.0.1:5173",
    trace: "on-first-retry",
    screenshot: "only-on-failure",
    actionTimeout: 8_000,
    navigationTimeout: 10_000,
    // 强制系统颜色方案为 dark，让预闪脚本稳定走 dark 分支（避免跟随系统导致初始 class 不确定）
    colorScheme: "dark",
  },

  projects: [
    {
      name: "chromium",
      use: { ...devices["Desktop Chrome"] },
    },
  ],

  webServer: {
    command: "python3 -m http.server 5173 --bind 127.0.0.1",
    url: "http://127.0.0.1:5173/",
    reuseExistingServer: !process.env.CI,
    timeout: 15_000,
    cwd: __dirname,
  },
});
