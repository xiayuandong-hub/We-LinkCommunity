import { Page, Locator } from '@playwright/test';

export class TopicDetailPage {
  readonly page: Page;
  readonly title: Locator;
  readonly content: Locator;
  readonly tags: Locator;
  readonly commentInput: Locator;
  readonly submitCommentBtn: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('h1, .topic-title');
    this.content = page.locator('.topic-content, .md-content');
    this.tags = page.locator('.tag-item, .topic-tag');
    this.commentInput = page.locator('textarea[name="content"], .comment-editor');
    this.submitCommentBtn = page.locator('button[type="submit"], #comment_btn');
  }

  async goto(topicId: number) {
    await this.page.goto(`/topic/${topicId}`);
  }

  async getTitle(): Promise<string> {
    return this.title.textContent() || '';
  }

  async addComment(content: string) {
    await this.commentInput.fill(content);
    await this.submitCommentBtn.click();
  }
}
