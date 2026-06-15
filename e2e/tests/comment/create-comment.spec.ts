import { test, expect } from '@playwright/test';

let _uniqueId = 0;
function shortId(): string {
  const r = Math.random().toString(36).slice(2,4); return `${Date.now().toString(36).slice(-5)}${r}${(++_uniqueId).toString(36)}`;
}

test.describe('评论', () => {

  test('创建评论并通过 API 验证', async ({ request }) => {
    const username = `co${shortId()}`;
    const regRes = await request.post('/api/e2e/register', {
      data: { username, password: 'CmtTestPass12345', email: `${username}@test.com` },
    });
    const regBody = await regRes.json();
    expect(regBody.code).toBe(200);
    const token = regBody.detail.token;

    // 创建话题（唯一标题）
    const topicTitle = `E2E评论测试${shortId()}`;
    const topicRes = await request.post('/api/topic', {
      data: { title: topicTitle, content: '评论测试用的话题', tags: 'E2E测试' },
      headers: { token },
    });
    const topicBody = await topicRes.json();
    expect(topicBody.code).toBe(200);
    const topicId = topicBody.detail.id;

    // 创建评论
    const commentRes = await request.post('/api/comment', {
      data: { content: '这是一条E2E测试评论', topicId: String(topicId) },
      headers: { token },
    });
    const commentBody = await commentRes.json();
    expect(commentBody.code).toBe(200);
    expect(commentBody.detail.content).toBe('这是一条E2E测试评论');
    expect(commentBody.detail.topicId).toBe(topicId);
  });
});
