import { test, expect } from '@playwright/test';

let _uniqueId = 0;
function shortId(): string {
  const r = Math.random().toString(36).slice(2,4); return `${Date.now().toString(36).slice(-5)}${r}${(++_uniqueId).toString(36)}`;
}

test.describe('用户注册', () => {

  test('通过 E2E 注册 API 创建新用户', async ({ request }) => {
    const username = `e2ereg${shortId()}`;

    const response = await request.post('/api/e2e/register', {
      data: {
        username,
        password: 'E2eTestPass1234567',
        email: `${username}@test.com`,
      },
    });

    const body = await response.json();
    expect(body.code).toBe(200);
    expect(body.detail).toHaveProperty('token');
    expect(body.detail.user.username).toBe(username);
  });

  test('使用已存在的用户名注册应显示错误', async ({ request }) => {
    const username = `dupuser${shortId()}`;

    // 先创建用户
    const createRes = await request.post('/api/e2e/register', {
      data: {
        username,
        password: 'E2eTestPass1234567',
        email: `${username}@test.com`,
      },
    });

    const createBody = await createRes.json();
    expect(createBody.code).toBe(200);

    // 再次尝试用相同用户名注册
    const dupRes = await request.post('/api/e2e/register', {
      data: {
        username,
        password: 'AnotherPass1234567',
        email: `other${shortId()}@test.com`,
      },
    });
    const body = await dupRes.json();
    expect(body.code).toBe(201);
    expect(body.description).toContain('用户名已存在');
  });

  test('无效密码格式应返回错误', async ({ request }) => {
    const response = await request.post('/api/e2e/register', {
      data: {
        username: 'shortpwdusr',
        password: '123',
        email: 'shortpwd@test.com',
      },
    });
    const body = await response.json();
    expect(body.code).toBe(201);
    expect(body.description).toContain('密码');
  });
});
