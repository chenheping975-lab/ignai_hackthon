import { test, expect } from "@playwright/test";

test.describe("前台活动链路 MVP", () => {
  test("单场活动页：展示状态、填写报名并进入作品平台", async ({ page }) => {
    await page.goto("/event.html?id=ignai-ai-skillathon");

    await expect(page.locator("#event-title")).toContainText("IGNAI AI Skillathon");
    await expect(page.locator("#event-status-rail")).toContainText("报名开放");
    await expect(page.locator("#event-track-grid")).toContainText("Agent 工具");

    await page.locator('input[name="name"]').fill("陈晨");
    await page.locator('input[name="phone"]').fill("13800138000");
    await page.locator('select[name="track"]').selectOption("Agent 工具");
    await page.locator('textarea[name="problem"]').fill("想做一个帮助实训课程沉淀作品的 Agent。");
    await page.locator("#event-submit-button").click();

    await expect(page.locator("#event-form-feedback")).toContainText("报名信息已写入 mock 数据");
    const stored = await page.evaluate(() =>
      localStorage.getItem("ignai_event_registration:ignai-ai-skillathon"),
    );
    expect(stored).toContain("陈晨");

    await page.locator("#event-project-hero-link").click();
    await expect(page).toHaveURL(/projects\.html\?event=ignai-ai-skillathon/);
    await expect(page.locator("#projects-title")).toContainText("作品平台");
  });

  test("作品平台：按赛道筛选、排序并点赞", async ({ page }) => {
    await page.goto("/projects.html?event=ignai-ai-skillathon");

    await expect(page.locator(".project-showcase-card")).toHaveCount(4);
    await page.getByRole("button", { name: "Agent 工具" }).click();
    await expect(page.locator(".project-showcase-card")).toHaveCount(1);
    await expect(page.locator(".project-showcase-card")).toContainText("Campus Agent Hub");

    await page.locator("#project-sort-select").selectOption("votes");
    await page.locator('[data-like-project="campus-agent-hub"]').click();
    await expect(page.locator('[data-like-project="campus-agent-hub"]')).toContainText("已点赞");

    await page.getByRole("button", { name: "全部" }).click();
    await expect(page.locator(".project-showcase-card")).toHaveCount(4);
  });
});
