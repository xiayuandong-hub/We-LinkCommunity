import { test, expect } from '@playwright/test';

let _uniqueId = 0;
function shortId(): string {
  const r = Math.random().toString(36).slice(2,4);
  return `${Date.now().toString(36).slice(-5)}${r}${(++_uniqueId).toString(36)}`;
}

test.describe('话题详情', () => {

  test('已创建的话题详情页应正常显示', async ({ request, page }) => {
    const username = `vt${shortId()}`;
    const regRes = await request.post('/api/e2e/register', {
      data: { username, password: 'ViewTopicPass1234', email: `${username}@test.com` },
    });
    const regBody = await regRes.json();
    expect(regBody.code).toBe(200);
    const token = regBody.detail.token;

    const topicTitle = `E2E查看话题${shortId()}`;
    const topicRes = await request.post('/api/topic', {
      data: { title: topicTitle, content: '查看话题内容', tags: 'E2E测试' },
      headers: { token },
    });
    const topicBody = await topicRes.json();
    expect(topicBody.code).toBe(200);
    const topicId = topicBody.detail.id;

    await page.goto(`/topic/${topicId}`);
    await expect(page.locator('body')).toContainText(topicTitle, { timeout: 5000 });
  });

  test('不存在的话题应显示错误页', async ({ page }) => {
    await page.goto('/topic/999999');
    // GlobalExceptionHandler 返回错误
    await expect(page.locator('body')).toContainText(/出错了|错误|500|不存在/, { timeout: 5000 });
  });
});
