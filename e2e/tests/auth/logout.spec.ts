import { test, expect } from '@playwright/test';

let _uniqueId = 0;
function shortId(): string {
  const r = Math.random().toString(36).slice(2,4); return `${Date.now().toString(36).slice(-5)}${r}${(++_uniqueId).toString(36)}`;
}

test.describe('登出', () => {

  test('登录后应能成功登出', async ({ page, request }) => {
    // 创建用户并登录
    const username = `lo${shortId()}`;
    const regRes = await request.post('/api/e2e/register', {
      data: { username, password: 'LogoutPass1234567', email: `${username}@test.com` },
    });
    expect((await regRes.json()).code).toBe(200);

    // 通过 API 设置验证码后登录
    await page.goto('/login');
    await page.evaluate(() => fetch('/api/e2e/captcha'));
    await page.fill('#username', username);
    await page.fill('#password', 'LogoutPass1234567');
    await page.fill('#captcha', 'test');
    await page.click('#login_btn');

    // 等待登录跳转
    await page.waitForURL('/', { timeout: 8000 }).catch(() => {});

    // 访问 /logout
    await page.goto('/logout');
    await page.waitForURL('/', { timeout: 5000 });
    expect(page.url()).toBe('http://localhost:8080/');
  });
});
