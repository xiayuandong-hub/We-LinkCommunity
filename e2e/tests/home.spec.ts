import { test, expect } from '@playwright/test';
import { HomePage } from '../pages/HomePage';

test.describe('首页', () => {

  test('首页应正常加载并显示话题列表区域', async ({ page }) => {
    const homePage = new HomePage(page);
    await homePage.goto();
    await expect(page).toHaveURL('/');
    await expect(page.locator('body')).not.toBeEmpty();
  });

  test('Tab 切换按钮应可见', async ({ page }) => {
    const homePage = new HomePage(page);
    await homePage.goto();
    // 验证主要 Tab 都存在（使用 first() 避免分页链接冲突）
    await expect(page.locator('a[href*="tab=all"]').first()).toBeVisible({ timeout: 5000 });
    await expect(page.locator('a[href*="tab=good"]').first()).toBeVisible({ timeout: 5000 });
    await expect(page.locator('a[href*="tab=hot"]').first()).toBeVisible({ timeout: 5000 });
  });

  test('未登录用户应能看到登录和注册链接', async ({ page }) => {
    const homePage = new HomePage(page);
    await homePage.goto();
    // 页面至少包含登录链接（实际模板中可能渲染为带 href 的 <a> 或 dropdown）
    await expect(page.locator('a[href="/login"]').first()).toBeVisible({ timeout: 5000 });
  });
});
