import { Page, Locator } from '@playwright/test';

export class AdminLoginPage {
  readonly page: Page;
  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly codeInput: Locator;
  readonly submitButton: Locator;
  readonly errorText: Locator;

  constructor(page: Page) {
    this.page = page;
    this.usernameInput = page.locator('#username');
    this.passwordInput = page.locator('#password');
    this.codeInput = page.locator('#code');
    this.submitButton = page.locator('button[type="submit"]');
    this.errorText = page.locator('.text-red');
  }

  async goto() {
    await this.page.goto('/admin/login');
  }

  async login(username: string, password: string, code?: string) {
    await this.usernameInput.fill(username);
    await this.passwordInput.fill(password);
    if (code) {
      await this.codeInput.fill(code);
    }
    await this.submitButton.click();
  }

  async isLoggedIn(): Promise<boolean> {
    return this.page.url().includes('/admin/index');
  }
}
