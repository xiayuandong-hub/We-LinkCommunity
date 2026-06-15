import { Page, APIRequestContext } from '@playwright/test';

/**
 * 认证辅助工具
 */

/**
 * 通过 API 登录获取用户 token
 */
export async function getApiToken(request: APIRequestContext, username: string, password: string): Promise<string | null> {
  const response = await request.post('/api/login', {
    data: { username, password, captcha: 'test' },
  });
  if (!response.ok()) return null;
  const body = await response.json();
  return body?.detail?.token || null;
}

/**
 * 通过页面交互登录（设置 session）
 */
export async function loginViaPage(page: Page, username: string, password: string) {
  await page.goto('/login');
  await page.fill('input[name="username"]', username);
  await page.fill('input[name="password"]', password);
  await page.click('button[type="submit"]');
  await page.waitForURL('/');
}
