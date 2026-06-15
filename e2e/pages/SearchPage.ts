import { Page, Locator } from '@playwright/test';

export class SearchPage {
  readonly page: Page;
  readonly searchInput: Locator;
  readonly searchButton: Locator;
  readonly results: Locator;
  readonly emptyMessage: Locator;

  constructor(page: Page) {
    this.page = page;
    this.searchInput = page.locator('input[name="keyword"], .search-input');
    this.searchButton = page.locator('button[type="submit"], .search-btn, a.search-btn');
    this.results = page.locator('.topic-item, .search-result-item, .row .topic');
    this.emptyMessage = page.locator('.empty-message, .no-data, .text-muted:has-text("没有")');
  }

  async goto() {
    await this.page.goto('/search');
  }

  async search(keyword: string) {
    await this.page.goto(`/search?keyword=${encodeURIComponent(keyword)}`);
  }

  async getResultCount(): Promise<number> {
    return this.results.count();
  }
}
