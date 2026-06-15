import { test, expect } from '@playwright/test';

test.describe('后台管理各页面', () => {

  test('管理员用户管理页可访问', async ({ page }) => {
    const response = await page.goto('/admin/user/list', { timeout: 15000 }).catch(() => null);
    // 即使未登录，页面也应返回（Shiro 会重定向到登录页）
    expect(response).not.toBeNull();
  });

  test('管理员评论管理页可访问', async ({ page }) => {
    const response = await page.goto('/admin/comment/list', { timeout: 15000 }).catch(() => null);
    expect(response).not.toBeNull();
  });

  test('管理员标签管理页可访问', async ({ page }) => {
    const response = await page.goto('/admin/tag/list', { timeout: 15000 }).catch(() => null);
    expect(response).not.toBeNull();
  });

  test('管理员系统配置页可访问', async ({ page }) => {
    const response = await page.goto('/admin/system/edit', { timeout: 15000 }).catch(() => null);
    expect(response).not.toBeNull();
  });
});
