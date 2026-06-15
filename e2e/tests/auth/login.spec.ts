import { test, expect } from '@playwright/test';
import { LoginPage } from '../../pages/LoginPage';

let _uniqueId = 0;
function shortId(): string {
  const r = Math.random().toString(36).slice(2,4); return `${Date.now().toString(36).slice(-5)}${r}${(++_uniqueId).toString(36)}`;
}

const username = `e2elogin${shortId()}`;
const password = 'E2eLoginPass123456';

test.describe('用户登录', () => {

  test.beforeAll(async ({ request }) => {
    const res = await request.post('/api/e2e/register', {
      data: { username, password, email: `${username}@test.com` },
    });
    const body = await res.json();
    expect(body.code).toBe(200);
  });

  test('应该能成功登录到首页', async ({ page }) => {
    const loginPage = new LoginPage(page);

    // 先导航到登录页
    await loginPage.goto();
    // 在页面上下文中设置验证码（确保 session 共享）
    await page.evaluate(() =>
      fetch('/api/e2e/captcha').then(r => r.text())
    );
    await loginPage.login(username, password, 'test');

    const result = await loginPage.waitForLoginResult();
    expect(result.success).toBeTruthy();
  });

  test('错误密码应显示错误信息', async ({ page }) => {
    const loginPage = new LoginPage(page);

    await loginPage.goto();
    await page.evaluate(() =>
      fetch('/api/e2e/captcha').then(r => r.text())
    );
    await loginPage.login(username, 'wrongpassword', 'test');

    const result = await loginPage.waitForLoginResult();
    expect(result.success).toBeFalsy();
    expect(result.error?.length).toBeGreaterThan(0);
  });

  test('未注册用户名应提示用户不存在', async ({ page }) => {
    const loginPage = new LoginPage(page);

    await loginPage.goto();
    await page.evaluate(() =>
      fetch('/api/e2e/captcha').then(r => r.text())
    );
    await loginPage.login('notexist999', 'SomePass123', 'test');

    const result = await loginPage.waitForLoginResult();
    expect(result.success).toBeFalsy();
    expect(result.error).toContain('用户不存在');
  });
});
