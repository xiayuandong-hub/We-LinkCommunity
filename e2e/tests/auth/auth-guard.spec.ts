import { test, expect } from '@playwright/test';
import { LoginPage } from '../../pages/LoginPage';

let _uniqueId = 0;
function shortId(): string {
  const r = Math.random().toString(36).slice(2,4);
  return `${Date.now().toString(36).slice(-5)}${r}${(++_uniqueId).toString(36)}`;
}

test.describe('权限守卫', () => {

  test('未登录用户访问 /settings 应重定向到 /login', async ({ page }) => {
    await page.goto('/settings');
    await expect(page).toHaveURL(/\/login/, { timeout: 5000 });
  });

  test('未登录用户访问 /topic/create 应重定向到 /login', async ({ page }) => {
    await page.goto('/topic/create');
    await expect(page).toHaveURL(/\/login/, { timeout: 5000 });
  });

  test('已登录用户可访问受保护页面', async ({ request }) => {
    const username = `guardusr${shortId()}`;
    const res = await request.post('/api/e2e/register', {
      data: { username, password: 'GuardTestPass12345', email: `${username}@test.com` },
    });
    const body = await res.json();
    expect(body.code).toBe(200);
    expect(body.detail).toHaveProperty('token');
  });
});
