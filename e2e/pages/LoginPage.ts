import { Page, Locator } from '@playwright/test';

export class LoginPage {
  readonly page: Page;
  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly captchaInput: Locator;
  readonly loginButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.usernameInput = page.locator('#username');
    this.passwordInput = page.locator('#password');
    this.captchaInput = page.locator('#captcha');
    this.loginButton = page.locator('#login_btn');
  }

  async goto() {
    await this.page.goto('/login');
  }

  async login(username: string, password: string, captcha?: string) {
    await this.usernameInput.fill(username);
    await this.passwordInput.fill(password);
    if (captcha) {
      await this.captchaInput.fill(captcha);
    }
    // 点击登录按钮后，页面通过 AJAX 提交
    this.loginButton.click();
  }

  async isLoggedIn(): Promise<boolean> {
    return this.page.locator('.user-avatar, .user-menu').first().isVisible().catch(() => false);
  }

  async waitForLoginResult(): Promise<{ success: boolean; error?: string }> {
    // 登录通过 AJAX 处理：成功时 700ms 后跳转到 "/"，失败时显示 layer.msg toast
    // layer.msg 生成的 DOM 结构为 .layui-layer-msg > .layui-layer-content
    const result = await Promise.race([
      this.page.waitForURL('/', { timeout: 8000 }).then(() => ({ success: true })),
      this.page.waitForSelector('.layui-layer-msg .layui-layer-padding', { timeout: 8000 })
        .then(el => el.textContent())
        .then(text => {
          // 登录成功也会先显示 suc toast，所以需要区分
          if (text && text.includes('登录成功')) {
            return { success: true, error: undefined };
          }
          return { success: false, error: text || '未知错误' };
        }),
    ]);
    return result;
  }
}
