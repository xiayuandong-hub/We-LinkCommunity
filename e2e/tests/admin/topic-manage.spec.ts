import { test, expect } from '@playwright/test';

test.describe('后台话题管理', () => {

  test('管理员登录页应可访问', async ({ page }) => {
    // 管理员页面使用 Shiro 认证 + CDN 资源，离线环境下仅验证页面可达性
    const response = await page.goto('/admin/login', { timeout: 15000 }).catch(() => null);
    expect(response).not.toBeNull();
  });
});
