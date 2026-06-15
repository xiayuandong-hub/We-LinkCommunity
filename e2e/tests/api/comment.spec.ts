import { test, expect } from '@playwright/test';

let _uniqueId = 0;
function shortId(): string {
  const r = Math.random().toString(36).slice(2,4);
  return `${Date.now().toString(36).slice(-5)}${r}${(++_uniqueId).toString(36)}`;
}

test.describe('API 评论接口', () => {

  test('创建评论应返回成功', async ({ request }) => {
    const username = `apicmt${shortId()}`;
    const regRes = await request.post('/api/e2e/register', {
      data: { username, password: 'ApiCmtPass123456', email: `${username}@test.com` },
    });
    const regBody = await regRes.json();
    expect(regBody.code).toBe(200);
    const token = regBody.detail.token;

    // 创建话题
    const topicTitle = `评论话题${shortId()}`;
    const topicRes = await request.post('/api/topic', {
      data: { title: topicTitle, content: '评论测试', tags: 'E2E测试' },
      headers: { token },
    });
    const topicBody = await topicRes.json();
    expect(topicBody.code).toBe(200);
    const topicId = topicBody.detail.id;

    // 创建评论
    const commentRes = await request.post('/api/comment', {
      data: { content: 'API测试评论内容', topicId: String(topicId) },
      headers: { token },
    });
    const commentBody = await commentRes.json();
    expect(commentBody.code).toBe(200);
    expect(commentBody.detail.content).toBe('API测试评论内容');
    expect(commentBody.detail.topicId).toBe(topicId);
  });
});
