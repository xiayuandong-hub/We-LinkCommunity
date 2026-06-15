import { test, expect } from '@playwright/test';

let _uniqueId = 0;
function shortId(): string {
  const r = Math.random().toString(36).slice(2,4); return `${Date.now().toString(36).slice(-5)}${r}${(++_uniqueId).toString(36)}`;
}

test.describe('话题发布', () => {

  test('创建话题并验证详情页', async ({ request, page }) => {
    // 创建用户并获取 token
    const username = `tp${shortId()}`;
    const regRes = await request.post('/api/e2e/register', {
      data: { username, password: 'TopicPass12345678', email: `${username}@test.com` },
    });
    const regBody = await regRes.json();
    expect(regBody.code).toBe(200);
    const token = regBody.detail.token;

    // 通过 API 创建话题
    const topicRes = await request.post('/api/topic', {
      data: { title: `E2E测试${shortId()}`, content: '这是由自动化测试创建的话题内容', tags: 'E2E测试' },
      headers: { token },
    });
    const topicBody = await topicRes.json();
    expect(topicBody.code).toBe(200);
    const topicId = topicBody.detail.id;

    // 验证话题详情页
    await page.goto(`/topic/${topicId}`);
    await expect(page.locator('body')).toContainText('E2E测试', { timeout: 5000 });
  });
});
