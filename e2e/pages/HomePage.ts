import { Page, Locator } from '@playwright/test';

export class HomePage {
  readonly page: Page;
  readonly topicList: Locator;
  readonly tabAll: Locator;
  readonly tabGood: Locator;
  readonly tabHot: Locator;
  readonly loginLink: Locator;
  readonly registerLink: Locator;

  constructor(page: Page) {
    this.page = page;
    this.topicList = page.locator('.topic-list');
    this.tabAll = page.locator('a[href*="tab=all"]');
    this.tabGood = page.locator('a[href*="tab=good"]');
    this.tabHot = page.locator('a[href*="tab=hot"]');
    this.loginLink = page.locator('a[href="/login"]');
    this.registerLink = page.locator('a[href="/register"]');
  }

  async goto() {
    await this.page.goto('/');
  }

  async clickTab(tab: 'all' | 'good' | 'hot') {
    switch (tab) {
      case 'all':
        await this.tabAll.click();
        break;
      case 'good':
        await this.tabGood.click();
        break;
      case 'hot':
        await this.tabHot.click();
        break;
    }
  }

  async getTopicTitles(): Promise<string[]> {
    return this.page.locator('.topic-item .title').allTextContents();
  }

  async clickFirstTopic() {
    await this.page.locator('.topic-item .title').first().click();
  }
}
