import { test, expect } from '@playwright/test';

let _uniqueId = 0;
function shortId(): string {
  const r = Math.random().toString(36).slice(2,4);
  return `${Date.now().toString(36).slice(-5)}${r}${(++_uniqueId).toString(36)}`;
}

test.describe('API 登录接口', () => {

  test('成功登录应返回 token', async ({ request }) => {
    const username = `apilogin${shortId()}`;

    // 先注册用户
    const regRes = await request.post('/api/e2e/register', {
      data: { username, password: 'ApiLoginPass12345', email: `${username}@test.com` },
    });
    expect((await regRes.json()).code).toBe(200);

    // 登录
    await request.get('/api/e2e/captcha');
    const loginRes = await request.post('/api/login', {
      data: { username, password: 'ApiLoginPass12345', captcha: 'test' },
    });
    const loginBody = await loginRes.json();
    expect(loginBody.code).toBe(200);
    expect(loginBody.detail).toHaveProperty('token');
    expect(loginBody.detail.user.username).toBe(username);
  });

  test('错误密码应返回业务错误', async ({ request }) => {
    const username = `apiloginerr${shortId()}`;

    await request.post('/api/e2e/register', {
      data: { username, password: 'ApiLoginPass12345', email: `${username}@test.com` },
    });

    await request.get('/api/e2e/captcha');
    const loginRes = await request.post('/api/login', {
      data: { username, password: 'wrongpassword', captcha: 'test' },
    });
    const loginBody = await loginRes.json();
    expect(loginBody.code).toBe(201);
  });
});
