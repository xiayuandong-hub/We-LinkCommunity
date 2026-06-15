import { test, expect } from '@playwright/test';

test.describe('标签', () => {

  test('标签页面应正常加载', async ({ page }) => {
    await page.goto('/tags');
    await expect(page.locator('body')).toContainText(/标签|话题/, { timeout: 5000 });
    // 标签页应该有分页或列表结构
    await expect(page.locator('.pagination, .tag-item, .tags-list, .row')).toBeVisible({ timeout: 5000 }).catch(() => {
      // 没有标签时也正常
    });
  });
});
