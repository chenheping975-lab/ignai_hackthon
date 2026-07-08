import { test, expect, type Page } from "@playwright/test";

const ROUTE = "/";

async function gotoHome(page: Page) {
  await page.goto(ROUTE);
  await page.waitForSelector(".hero h1");
}

test("页面初始加载：默认 dark、title 正确", async ({ page }) => {
  await gotoHome(page);
  await expect(page).toHaveTitle(/IGNAI AI Skillathon/);
  expect((await page.locator("html").getAttribute("class")) ?? "").toContain("dark");
});

test("主题切换按钮：点击在 dark/light 间切换并写入 localStorage", async ({ page }) => {
  await gotoHome(page);

  const html = page.locator("html");
  const toggle = page.locator("#theme-toggle");

  await expect(html).toHaveClass(/dark/);
  await toggle.click();
  await expect(html).toHaveClass(/light/);
  await expect(html).not.toHaveClass(/dark/);
  expect(await page.evaluate(() => localStorage.getItem("ignai-theme"))).toBe("light");

  await toggle.click();
  await expect(html).toHaveClass(/dark/);
  expect(await page.evaluate(() => localStorage.getItem("ignai-theme"))).toBe("dark");
});

test("主题持久化：reload 后保留 localStorage 中的主题", async ({ page }) => {
  await gotoHome(page);
  await page.evaluate(() => localStorage.setItem("ignai-theme", "light"));
  await page.reload();
  await expect(page.locator("html")).toHaveClass(/light/);
});

test("hero CTA：点击后滚动到活动列表", async ({ page }) => {
  await gotoHome(page);
  await page.locator(".hero-actions .primary-button").click();
  await expect
    .poll(
      async () => await page.locator("#events").evaluate((el) => Math.abs(el.getBoundingClientRect().top)),
      { timeout: 5_000, intervals: [200] },
    )
    .toBeLessThan(120);
});

test("nav 锚点：点击「活动」滚动到活动列表", async ({ page }) => {
  await gotoHome(page);
  await page.locator('.nav-links a[href="#events"]').click();
  await expect
    .poll(
      async () => await page.locator("#events").evaluate((el) => Math.abs(el.getBoundingClientRect().top)),
      { timeout: 5_000, intervals: [200] },
    )
    .toBeLessThan(120);
});

test("首页活动卡片：进入单场活动详情", async ({ page }) => {
  await gotoHome(page);
  await expect(page.locator(".event-card")).toHaveCount(3);
  await expect(page.locator(".event-card").first().getByRole("link", { name: "作品平台" })).toHaveCount(0);
  await page.locator(".event-card").first().getByRole("link", { name: "进入活动" }).click();
  await expect(page).toHaveURL(/event\.html\?id=ignai-ai-skillathon/);
  await expect(page.locator("#event-title")).toContainText("IGNAI AI Skillathon");
});

test("reveal：滚动后多数 [data-reveal] 元素加上 is-visible", async ({ page }) => {
  await gotoHome(page);
  await page.evaluate(() => {
    document.documentElement.style.scrollBehavior = "smooth";
    window.scrollTo({ top: document.body.scrollHeight, behavior: "smooth" });
  });
  await page.waitForTimeout(2500);
  await page.evaluate(() => window.scrollTo({ top: 0, behavior: "smooth" }));
  await page.waitForTimeout(1500);

  const total = await page.locator("[data-reveal]").count();
  const visible = await page.locator("[data-reveal].is-visible").count();
  expect(total).toBeGreaterThan(0);
  expect(visible / total).toBeGreaterThanOrEqual(0.75);
});

test("marquee：signal-strip 子元素被复制实现无缝循环", async ({ page }) => {
  await gotoHome(page);
  const count = await page.locator(".signal-strip > div > span").count();
  expect(count).toBeGreaterThanOrEqual(12);
});
