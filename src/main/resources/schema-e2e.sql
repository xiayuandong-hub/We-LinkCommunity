-- E2E 测试用 Schema（H2 MySQL 兼容模式）
-- 简化版：不包含 DATE_ADD/DATE_SUB 别名（E2E 不依赖这些函数）

CREATE TABLE IF NOT EXISTS admin_user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL DEFAULT '',
  password VARCHAR(255) NOT NULL DEFAULT '',
  in_time DATETIME NOT NULL,
  role_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS code (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NULL,
  code VARCHAR(255) NOT NULL DEFAULT '',
  in_time DATETIME NOT NULL,
  expire_time DATETIME NOT NULL,
  email VARCHAR(255) NULL,
  mobile VARCHAR(255) NULL,
  used BIT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS collect (
  topic_id INT NOT NULL,
  user_id INT NOT NULL,
  in_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS comment (
  id INT AUTO_INCREMENT PRIMARY KEY,
  style VARCHAR(50) NULL DEFAULT 'MD',
  content CLOB NOT NULL,
  topic_id INT NOT NULL,
  user_id INT NOT NULL,
  in_time DATETIME NOT NULL,
  comment_id INT NULL,
  up_ids TEXT NULL,
  tg_message_id INT NULL,
  status BIT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS notification (
  id INT AUTO_INCREMENT PRIMARY KEY,
  topic_id INT NOT NULL,
  user_id INT NOT NULL,
  target_user_id INT NOT NULL,
  action VARCHAR(255) NOT NULL DEFAULT '',
  in_time DATETIME NOT NULL,
  read BIT NOT NULL DEFAULT 0,
  content CLOB NULL
);

CREATE TABLE IF NOT EXISTS oauth_user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  oauth_id INT NULL,
  type VARCHAR(255) NOT NULL DEFAULT '',
  login VARCHAR(255) NOT NULL DEFAULT '',
  access_token VARCHAR(255) NOT NULL DEFAULT '',
  in_time DATETIME NOT NULL,
  bio CLOB NULL,
  email VARCHAR(255) NULL,
  user_id INT NOT NULL,
  refresh_token VARCHAR(255) NULL,
  union_id VARCHAR(255) NULL,
  expires_in VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS permission (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL DEFAULT '',
  value VARCHAR(255) NOT NULL DEFAULT '',
  pid INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS role (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL DEFAULT ''
);

CREATE TABLE IF NOT EXISTS role_permission (
  role_id INT NOT NULL,
  permission_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS sensitive_word (
  id INT AUTO_INCREMENT PRIMARY KEY,
  word VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS system_config (
  id INT AUTO_INCREMENT PRIMARY KEY,
  key VARCHAR(255) NULL,
  value VARCHAR(255) NULL,
  description VARCHAR(1000) NOT NULL,
  pid INT NOT NULL DEFAULT 0,
  type VARCHAR(255) NULL DEFAULT '',
  option VARCHAR(255) NULL,
  reboot INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tag (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL DEFAULT '',
  description VARCHAR(1000) NULL,
  icon VARCHAR(255) NULL,
  topic_count INT NOT NULL DEFAULT 0,
  in_time DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS topic (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL DEFAULT '',
  style VARCHAR(50) NULL DEFAULT 'MD',
  content CLOB NULL,
  in_time DATETIME NOT NULL,
  modify_time DATETIME NULL,
  user_id INT NOT NULL,
  comment_count INT NOT NULL DEFAULT 0,
  collect_count INT NOT NULL DEFAULT 0,
  view INT NOT NULL DEFAULT 0,
  top BIT NOT NULL DEFAULT 0,
  good BIT NOT NULL DEFAULT 0,
  up_ids TEXT NULL
);

CREATE TABLE IF NOT EXISTS topic_tag (
  tag_id INT NOT NULL,
  topic_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL DEFAULT '',
  password VARCHAR(255) NULL DEFAULT '',
  avatar VARCHAR(1000) NULL,
  email VARCHAR(255) NULL,
  mobile VARCHAR(255) NULL,
  website VARCHAR(255) NULL,
  bio VARCHAR(1000) NULL,
  score INT NOT NULL DEFAULT 0,
  in_time DATETIME NOT NULL,
  token VARCHAR(255) NOT NULL DEFAULT '',
  telegram_name VARCHAR(255) NULL,
  email_notification BIT NOT NULL DEFAULT 0,
  active BIT NOT NULL DEFAULT 1
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_admin_user_role_id ON admin_user(role_id);
CREATE INDEX IF NOT EXISTS idx_comment_topic_id ON comment(topic_id);
CREATE INDEX IF NOT EXISTS idx_comment_user_id ON comment(user_id);
CREATE INDEX IF NOT EXISTS idx_collect_topic_id ON collect(topic_id);
CREATE INDEX IF NOT EXISTS idx_collect_user_id ON collect(user_id);
CREATE INDEX IF NOT EXISTS idx_notification_topic_id ON notification(topic_id);
CREATE INDEX IF NOT EXISTS idx_notification_user_id ON notification(user_id);
CREATE INDEX IF NOT EXISTS idx_permission_pid ON permission(pid);
CREATE INDEX IF NOT EXISTS idx_role_permission_role_id ON role_permission(role_id);
CREATE INDEX IF NOT EXISTS idx_system_config_key ON system_config(key);
CREATE INDEX IF NOT EXISTS idx_tag_in_time ON tag(in_time);
CREATE INDEX IF NOT EXISTS idx_topic_user_id ON topic(user_id);
CREATE INDEX IF NOT EXISTS idx_user_email ON user(email);

-- Seed data
MERGE INTO role (id, name) KEY(id) VALUES (1, '超级管理员');
MERGE INTO role (id, name) KEY(id) VALUES (2, '审核员');

MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (1, '首页', 'index', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (2, '话题', 'topic', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (3, '评论', 'comment', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (5, '用户', 'user', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (9, '系统', 'system', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (11, '仪表盘', 'index:index', 1);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (12, '话题列表', 'topic:list', 2);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (17, '评论列表', 'comment:list', 3);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (22, '用户列表', 'user:list', 5);

MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 11);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 12);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 17);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 22);

MERGE INTO admin_user (id, username, password, in_time, role_id) KEY(id)
VALUES (1, 'admin', '$2a$10$0F6RXnrQDF8SsOudYk7uhuWlqq3kjPuPm4UGeDCj0gvO8xj2pbZ4y', '2018-11-11 11:11:11', 1);

-- System config
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (2, 'base_url', 'http://localhost:8080', '网站域名', 23, 'url', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (4, 'cookie_domain', 'localhost', 'cookie域名', 23, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (6, 'cookie_name', 'user_token', 'cookie名称', 23, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (8, 'create_topic_score', '10', '发帖奖励积分', 26, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (7, 'create_comment_score', '5', '评论奖励积分', 26, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (15, 'name', 'We-Link E2E测试社区', '站点名称', 23, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (16, 'page_size', '20', '分页大小', 23, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (21, 'upload_path', './static/upload/', '上传路径', 25, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (48, 'theme', 'default', '系统主题', 23, 'select', 'default', 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (1, 'admin_remember_me_max_age', '30', '记住我有效天数', 23, 'number', NULL, 1);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (22, 'up_topic_score', '3', '点赞话题积分', 26, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (19, 'up_comment_score', '3', '点赞评论积分', 26, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (89, 'comment_need_examine', '0', '评论是否需要审核', 23, 'radio', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (93, 'email_mobile_code_count', '5', '验证码发送上限', 23, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (100, 'content_style', 'MD', '输入框语法风格', 23, 'select', 'RICH,MD', '1');
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (5, 'cookie_max_age', '604800', 'cookie有效期', 23, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (3, 'comment_layer', '1', '评论盖楼形式显示', 23, 'radio', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (39, 'search', '0', '搜索开关', 35, 'radio', NULL, 0);

-- Identicon 头像生成所需配置
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (18, 'static_url', 'http://localhost:8080/static/upload/', '静态文件访问地址', 25, 'url', NULL, 0);

-- ElasticSearch 配置（空值=未启用）
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (106, 'elasticsearch_host', '', 'ES服务地址', 35, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (107, 'elasticsearch_port', '', 'ES端口', 35, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (108, 'elasticsearch_index', '', 'ES索引名', 35, 'text', NULL, 0);
