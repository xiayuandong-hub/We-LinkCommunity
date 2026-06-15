import { Page, Locator } from '@playwright/test';

export class TopicManagePage {
  readonly page: Page;
  readonly searchInput: Locator;
  readonly searchButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.searchInput = page.locator('input[name="username"], input[placeholder*="用户名"]');
    this.searchButton = page.locator('button[type="submit"], .btn-search');
  }

  async goto() {
    await this.page.goto('/admin/topic/list');
  }
}
