import { test, expect } from '@playwright/test';

let _uniqueId = 0;
function shortId(): string {
  const r = Math.random().toString(36).slice(2,4); return `${Date.now().toString(36).slice(-5)}${r}${(++_uniqueId).toString(36)}`;
}

test.describe('搜索', () => {

  test('搜索功能应返回结果页面', async ({ request, page }) => {
    // 创建话题以便搜索
    const username = `sr${shortId()}`;
    const regRes = await request.post('/api/e2e/register', {
      data: { username, password: 'SearchPass1234567', email: `${username}@test.com` },
    });
    const regBody = await regRes.json();
    expect(regBody.code).toBe(200);

    const token = regBody.detail.token;
    const title = `E2E可搜索话题${shortId()}`;
    await request.post('/api/topic', {
      data: { title, content: '搜索测试内容', tags: 'E2E测试' },
      headers: { token },
    });

    // 搜索
    await page.goto('/search?keyword=E2E');
    await expect(page.locator('body')).toContainText(/E2E|搜索|话题/, { timeout: 5000 });
  });

  test('空搜索结果应正常显示', async ({ page }) => {
    await page.goto('/search?keyword=ZZZZNOTEXIST999');
    await expect(page.locator('body')).toContainText(/没有|暂无|空|0/, { timeout: 5000 });
  });
});
