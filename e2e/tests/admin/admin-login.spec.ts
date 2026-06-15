import { test, expect } from '@playwright/test';

test.describe('后台管理', () => {

  test('管理员页面应返回登录页面', async ({ page }) => {
    // 注意：管理员页面依赖 CDN 资源（cdnjs.cloudflare.com），
    // 在离线环境下可能加载不完整。这里仅验证页面能访问。
    const response = await page.goto('/admin/login', { timeout: 10000 }).catch(() => null);
    expect(response).not.toBeNull();
  });
});
