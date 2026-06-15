import { Page, Locator } from '@playwright/test';

export class TopicCreatePage {
  readonly page: Page;
  readonly titleInput: Locator;
  readonly contentTextarea: Locator;
  readonly tagsInput: Locator;
  readonly submitButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.titleInput = page.locator('input[name="title"]');
    this.contentTextarea = page.locator('textarea[name="content"], .editor-content');
    this.tagsInput = page.locator('input[name="tags"]');
    this.submitButton = page.locator('button[type="submit"]');
  }

  async goto() {
    await this.page.goto('/topic/create');
  }

  async createTopic(title: string, content: string, tags: string[]) {
    await this.titleInput.fill(title);
    await this.contentTextarea.fill(content);
    if (tags.length > 0) {
      await this.tagsInput.fill(tags.join(','));
    }
    await this.submitButton.click();
  }
}
