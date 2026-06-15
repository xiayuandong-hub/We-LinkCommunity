/**
 * E2E 测试数据配置
 */

export const TEST_USERS = {
  /** 预置测试用户（通过 seed 脚本创建） */
  existing: {
    username: 'playwright_test',
    password: 'Playwright123',
    email: 'playwright_test@test.com',
  },
  /** 每次测试动态生成的前缀 */
  dynamicPrefix: 'e2e_',
};

export const TEST_ADMIN = {
  username: 'admin',
  password: '123123',
};

export const TEST_TOPIC = {
  title: 'E2E测试话题标题',
  content: '这是一条由Playwright自动化测试创建的话题内容。',
  tags: 'E2E测试',
};

export const TEST_COMMENT = {
  content: '这是一条由Playwright自动化测试创建的评论内容。',
};
