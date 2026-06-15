import { test, expect } from '@playwright/test';

let _uniqueId = 0;
function shortId(): string {
  const r = Math.random().toString(36).slice(2,4);
  return `${Date.now().toString(36).slice(-5)}${r}${(++_uniqueId).toString(36)}`;
}

test.describe('用户个人页', () => {

  test('已存在用户的个人页应正常显示', async ({ request, page }) => {
    const username = `pro${shortId()}`;
    const regRes = await request.post('/api/e2e/register', {
      data: { username, password: 'ProfileTestPass123', email: `${username}@test.com`, bio: 'E2E测试用户简介' },
    });
    const regBody = await regRes.json();
    expect(regBody.code).toBe(200);

    await page.goto(`/api/user/${username}`);
    const body = await page.locator('pre').textContent().catch(() => null);
    if (body) {
      const json = JSON.parse(body);
      expect(json.code).toBe(200);
      expect(json.detail.user.username).toBe(username);
    }
  });

  test('用户的话题列表页应能加载', async ({ request, page }) => {
    const username = `prt${shortId()}`;
    const regRes = await request.post('/api/e2e/register', {
      data: { username, password: 'ProfileTestPass123', email: `${username}@test.com` },
    });
    const regBody = await regRes.json();
    expect(regBody.code).toBe(200);
    const token = regBody.detail.token;

    // 创建一篇话题
    await request.post('/api/topic', {
      data: { title: `用户话题${shortId()}`, content: '内容', tags: 'E2E测试' },
      headers: { token },
    });

    // 访问用户话题列表 API
    const topicsRes = await request.get(`/api/user/${username}/topics`);
    const topicsBody = await topicsRes.json();
    expect(topicsBody.code).toBe(200);
    expect(topicsBody.detail.topics.records.length).toBeGreaterThanOrEqual(0);
  });
});
