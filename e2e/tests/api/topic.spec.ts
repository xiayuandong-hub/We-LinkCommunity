import { test, expect } from '@playwright/test';

let _uniqueId = 0;
function shortId(): string {
  const r = Math.random().toString(36).slice(2,4);
  return `${Date.now().toString(36).slice(-5)}${r}${(++_uniqueId).toString(36)}`;
}

test.describe('API 话题接口', () => {

  test('话题详情应返回数据', async ({ request }) => {
    const username = `apitp${shortId()}`;
    const regRes = await request.post('/api/e2e/register', {
      data: { username, password: 'ApiTopicPass1234', email: `${username}@test.com` },
    });
    const regBody = await regRes.json();
    expect(regBody.code).toBe(200);
    const token = regBody.detail.token;

    // 先创建话题
    const topicTitle = `API话题${shortId()}`;
    const createRes = await request.post('/api/topic', {
      data: { title: topicTitle, content: 'API话题内容', tags: 'E2E测试' },
      headers: { token },
    });
    const createBody = await createRes.json();
    expect(createBody.code).toBe(200);
    const topicId = createBody.detail.id;

    // 查询话题详情
    const detailRes = await request.get(`/api/topic/${topicId}`);
    const detailBody = await detailRes.json();
    expect(detailBody.code).toBe(200);
    expect(detailBody.detail.topic.title).toBe(topicTitle);
  });

  test('话题点赞应返回成功', async ({ request }) => {
    const username = `apivote${shortId()}`;
    const regRes = await request.post('/api/e2e/register', {
      data: { username, password: 'ApiVotePass12345', email: `${username}@test.com` },
    });
    const regBody = await regRes.json();
    expect(regBody.code).toBe(200);

    // 创建第二个用户作为话题作者
    const authorName = `avta${shortId()}`;
    const authorRes = await request.post('/api/e2e/register', {
      data: { username: authorName, password: 'ApiVotePass12345', email: `${authorName}@test.com` },
    });
    const authorBody = await authorRes.json();
    expect(authorBody.code).toBe(200);
    const authorToken = authorBody.detail.token;

    // 作者创建话题
    const topicTitle = `点赞话题${shortId()}`;
    const createRes = await request.post('/api/topic', {
      data: { title: topicTitle, content: '点赞测试', tags: 'E2E测试' },
      headers: { token: authorToken },
    });
    const createBody = await createRes.json();
    expect(createBody.code).toBe(200);
    const topicId = createBody.detail.id;

    // 第一个用户点赞
    const voteRes = await request.get(`/api/topic/${topicId}/vote`, {
      headers: { token: regBody.detail.token },
    });
    const voteBody = await voteRes.json();
    expect(voteBody.code).toBe(200);
  });
});
