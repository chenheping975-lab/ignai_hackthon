import { test, expect } from "@playwright/test";

test.describe("Root 后台 MVP 原型", () => {
  test("后台初始加载并完成 root 初始化", async ({ page }) => {
    await page.goto("/root/");
    await expect(page).toHaveTitle(/Root Admin/);
    await expect(page.locator("#dashboard-title")).toBeVisible();

    await page.locator("#root-email").fill("founder@ignai.cn");
    await page.locator("#root-password").fill("ignai-root-demo");
    await page.locator("#root-setup-form button").click();

    await expect(page.locator("#root-status")).toContainText("founder@ignai.cn");
    await expect(page.locator("#toast")).toContainText("root 已初始化");
  });

  test("报名管理：打开详情、AI 初筛、通过并生成邮件", async ({ page }) => {
    await page.goto("/root/");
    await page.locator('[data-admin-view="registrations"]').click();
    await expect(page.locator("#registrations-title")).toBeVisible();

    await page.locator('#registration-table tr[data-registration-id="1"]').click();
    await expect(page.locator("#registration-drawer")).toHaveClass(/open/);
    await expect(page.locator("#drawer-content")).toContainText("Torch Lab");

    await page.locator("#run-ai-screen").click();
    await expect(page.locator("#ai-summary")).toContainText("AI 已补充");

    await page.locator('[data-review-status="approved"]').click();
    await expect(page.locator("#drawer-content")).toContainText("已通过");
    await page.locator('[data-admin-view="mail"]').click();
    await expect(page.locator("#mail-subject")).toHaveValue(/Torch Lab/);
    await expect(page.locator("#mail-body")).toHaveValue(/陈晨/);
  });

  test("活动管理：新增报名问题并同步预览表单", async ({ page }) => {
    await page.goto("/root/");
    await page.locator('[data-admin-view="event"]').click();
    const initial = await page.locator(".field-item").count();

    await page.locator("#add-field-button").click();
    await expect(page.locator(".field-item")).toHaveCount(initial + 1);

    const editor = page.locator(`[data-field-index="${initial}"]`);
    await editor.locator('[data-field-prop="label"]').fill("是否需要现场组队？");
    await editor.locator('[data-field-prop="type"]').selectOption("radio");
    await editor.locator('[data-field-prop="required"]').check();
    await editor.locator('[data-field-prop="options"]').fill("需要\n不需要\n现场再看");

    await expect(page.locator("#form-preview")).toContainText("是否需要现场组队？ *");
    await expect(page.locator("#form-preview")).toContainText("需要");
    await expect(page.locator("#form-preview")).toContainText("不需要");
  });

  test("项目看板：公开项目后赛事预览展示同步变化", async ({ page }) => {
    await page.goto("/root/");
    await page.locator('[data-admin-view="projects"]').click();
    await page.locator('[data-project-id="1"] [data-project-action="publish"]').click();
    await expect(page.locator('[data-project-id="1"]')).toContainText("已公开");

    await page.locator('[data-admin-view="preview"]').click();
    await page.locator("#preview-role").selectOption("leader");
    await page.locator("#preview-phase").selectOption("submission");
    await expect(page.locator("#event-preview-panel")).toContainText("队长");
    await expect(page.locator("#event-preview-panel")).toContainText("当前：提交中");
    await expect(page.locator("#event-preview-panel")).toContainText("Campus Agent Hub");
  });
});
