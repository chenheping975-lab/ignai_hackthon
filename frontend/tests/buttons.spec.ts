import { test, expect, type Page } from "@playwright/test";

/**
 * IGNAI Skillathon 前端按钮 / 交互自动化测试
 * 覆盖：主题切换、hero CTA、nav 锚点、demo tabs、评分、提交、reveal、marquee
 */

const ROUTE = "/";

async function gotoHome(page: Page) {
  await page.goto(ROUTE);
  // 等 hero 入场完成，避免后续 DOM 测量抢跑
  await page.waitForSelector(".hero h1");
}

/* -----------------------------------------------
 * 1. 页面初始加载
 * --------------------------------------------- */
test("页面初始加载：默认 dark、title 正确", async ({ page }) => {
  await gotoHome(page);
  await expect(page).toHaveTitle(/IGNAI AI Skillathon/);
  expect(await page.locator("html").getAttribute("class") ?? "").toContain("dark");
});

/* -----------------------------------------------
 * 2. 主题切换按钮
 * --------------------------------------------- */
test("主题切换按钮：点击在 dark/light 间切换并写入 localStorage", async ({ page }) => {
  await gotoHome(page);

  const html = page.locator("html");
  const toggle = page.locator("#theme-toggle");

  // 初始为 dark
  await expect(html).toHaveClass(/dark/);

  // 点击 → light
  await toggle.click();
  await expect(html).toHaveClass(/light/);
  await expect(html).not.toHaveClass(/dark/);
  const afterFirst = await page.evaluate(() => localStorage.getItem("ignai-theme"));
  expect(afterFirst).toBe("light");

  // 再点击 → dark
  await toggle.click();
  await expect(html).toHaveClass(/dark/);
  const afterSecond = await page.evaluate(() => localStorage.getItem("ignai-theme"));
  expect(afterSecond).toBe("dark");
});

/* -----------------------------------------------
 * 3. 主题持久化
 * --------------------------------------------- */
test("主题持久化：reload 后保留 localStorage 中的主题", async ({ page }) => {
  await gotoHome(page);
  await page.evaluate(() => localStorage.setItem("ignai-theme", "light"));
  await page.reload();
  await expect(page.locator("html")).toHaveClass(/light/);
});

/* -----------------------------------------------
 * 4. hero "提交报名"
 * --------------------------------------------- */
test("hero 提交报名：点击后滚动到 #apply", async ({ page }) => {
  await gotoHome(page);
  await page.locator(".hero-actions .primary-button").click();
  // smooth scroll，等滚动停下来
  await expect.poll(
    async () =>
      await page.locator("#apply").evaluate((el) => Math.abs(el.getBoundingClientRect().top)),
    { timeout: 5_000, intervals: [200] }
  ).toBeLessThan(120);
});

/* -----------------------------------------------
 * 5. hero "查看作品"
 * --------------------------------------------- */
test("hero 查看作品：点击后滚动到 #showcase", async ({ page }) => {
  await gotoHome(page);
  await page.locator(".hero-actions .outline-button").click();
  await expect.poll(
    async () =>
      await page.locator("#showcase").evaluate((el) => Math.abs(el.getBoundingClientRect().top)),
    { timeout: 5_000, intervals: [200] }
  ).toBeLessThan(120);
});

/* -----------------------------------------------
 * 6. nav 锚点
 * --------------------------------------------- */
test("nav 锚点：点击「流程」滚动到 #journey", async ({ page }) => {
  await gotoHome(page);
  await page.locator('.nav-links a[href="#journey"]').click();
  await expect.poll(
    async () =>
      await page.locator("#journey").evaluate((el) => Math.abs(el.getBoundingClientRect().top)),
    { timeout: 5_000, intervals: [200] }
  ).toBeLessThan(120);
});

/* -----------------------------------------------
 * 7. demo-tabs 切换
 * --------------------------------------------- */
test("demo-tabs：4 个 tab 切换面板内容并触发动画", async ({ page }) => {
  await gotoHome(page);
  // 先滚到面板，确保 reveal 完成
  await page.locator("#apply").scrollIntoViewIfNeeded();

  const tabs = ["signup", "project", "rating", "feishu"] as const;
  for (const tab of tabs) {
    const btn = page.locator(`[data-demo-tab="${tab}"]`);
    await btn.click();
    // active class
    await expect(btn).toHaveClass(/active/);
    // panel 触发 is-switching 动画类
    await expect(page.locator("#demo-panel")).toHaveClass(/is-switching/);
  }
  // 切回 signup 后表单存在
  await page.locator('[data-demo-tab="signup"]').click();
  await expect(page.locator("#demo-signup-form")).toBeVisible();
});

/* -----------------------------------------------
 * 8. 评分按钮
 * --------------------------------------------- */
test("评分按钮：点击 4 分后更新状态文案", async ({ page }) => {
  await gotoHome(page);
  await page.locator("#apply").scrollIntoViewIfNeeded();
  await page.locator('[data-demo-tab="rating"]').click();
  await page.locator('[data-score="4"]').click();

  await expect(page.locator('[data-score="4"]')).toHaveClass(/active/);
  await expect(page.locator("#score-demo-text")).toContainText(/4/);
});

/* -----------------------------------------------
 * 9. 未登录提交报名（app.js 行为：直接跳转 login.html）
 * --------------------------------------------- */
test("未登录提交报名：跳转到 login.html", async ({ page }) => {
  await gotoHome(page);
  await page.evaluate(() => localStorage.clear());
  await page.reload();
  await page.locator("#apply").scrollIntoViewIfNeeded();
  await page.locator("#submit-registration-button").click();
  await expect(page).toHaveURL(/login\.html/);
});

/* -----------------------------------------------
 * 10. Reveal 入场
 * --------------------------------------------- */
test("reveal：滚动后绝大多数 [data-reveal] 元素加上 is-visible", async ({ page }) => {
  await gotoHome(page);
  // 用平滑滚动让每个元素都经过视口（瞬间 scrollTo 会跳过 IntersectionObserver 中间帧）
  await page.evaluate(() => {
    document.documentElement.style.scrollBehavior = "smooth";
    window.scrollTo({ top: document.body.scrollHeight, behavior: "smooth" });
  });
  await page.waitForTimeout(2500);
  // 触发剩余元素：滚回顶部再到中段
  await page.evaluate(() => window.scrollTo({ top: 0, behavior: "smooth" }));
  await page.waitForTimeout(1500);

  const total = await page.locator("[data-reveal]").count();
  const visible = await page.locator("[data-reveal].is-visible").count();
  expect(total).toBeGreaterThan(0);
  // 平滑滚动后允许少数边缘元素未触发，但至少 75% 要触发
  expect(visible / total).toBeGreaterThanOrEqual(0.75);
});

/* -----------------------------------------------
 * 11. 跑马灯连续性
 * --------------------------------------------- */
test("marquee：signal-strip 子元素被复制实现无缝循环", async ({ page }) => {
  await gotoHome(page);
  const count = await page.locator(".signal-strip > div > span").count();
  // app.js 复制了一份，原始 span 6 个 → 复制后 12 个
  expect(count).toBeGreaterThanOrEqual(12);
});
