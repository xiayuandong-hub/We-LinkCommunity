import { Page, Locator } from '@playwright/test';

export class DashboardPage {
  readonly page: Page;
  readonly topicCount: Locator;
  readonly commentCount: Locator;
  readonly userCount: Locator;
  readonly tagCount: Locator;

  constructor(page: Page) {
    this.page = page;
    this.topicCount = page.locator('.info-box:has-text("话题") .info-box-number, .small-box:has-text("话题")');
    this.commentCount = page.locator('.info-box:has-text("评论") .info-box-number, .small-box:has-text("评论")');
    this.userCount = page.locator('.info-box:has-text("用户") .info-box-number, .small-box:has-text("用户")');
    this.tagCount = page.locator('.info-box:has-text("标签") .info-box-number, .small-box:has-text("标签")');
  }

  async goto() {
    await this.page.goto('/admin/index');
  }
}
