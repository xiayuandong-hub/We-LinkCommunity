import { Page, Locator } from '@playwright/test';

export class RegisterPage {
  readonly page: Page;
  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly emailInput: Locator;
  readonly captchaInput: Locator;
  readonly submitButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.usernameInput = page.locator('input[name="username"]');
    this.passwordInput = page.locator('input[name="password"]');
    this.emailInput = page.locator('input[name="email"]');
    this.captchaInput = page.locator('input[name="captcha"]');
    this.submitButton = page.locator('button[type="submit"]');
  }

  async goto() {
    await this.page.goto('/register');
  }

  async register(username: string, password: string, email: string, captcha?: string) {
    await this.usernameInput.fill(username);
    await this.passwordInput.fill(password);
    await this.emailInput.fill(email);
    if (captcha) {
      await this.captchaInput.fill(captcha);
    }
    await this.submitButton.click();
  }

  async getErrorMessage(): Promise<string> {
    return this.page.locator('.error-message, .alert-danger, .text-danger').textContent().then(t => t || '');
  }
}
