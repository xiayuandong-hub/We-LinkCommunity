-- =============================================================
-- We-Link Community 测试数据库 Schema（H2 兼容版）
-- 用于 Spring Boot 测试环境（application-test.yml）
-- =============================================================
-- 注意：H2 的 MySQL 兼容模式（MODE=MySQL）原生支持
-- curdate()、concat()、date() 等函数
-- 但 date_add() 和 date_sub() 需要自定义别名

-- 为 H2 创建 DATE_ADD 和 DATE_SUB 别名以兼容 MySQL 语法
-- MySQL: date_add(datetime, INTERVAL n unit)
-- 使用预编译的 Java 类（在 H2Functions.java 中定义），避免 H2 内联编译问题
DROP ALIAS IF EXISTS DATE_ADD;
CREATE ALIAS DATE_ADD FOR "co.yiiu.welink.config.H2Functions.dateAdd";
DROP ALIAS IF EXISTS DATE_SUB;
CREATE ALIAS DATE_SUB FOR "co.yiiu.welink.config.H2Functions.dateSub";

-- =============================================================
-- 表结构定义
-- =============================================================

-- admin_user 管理员用户表
CREATE TABLE IF NOT EXISTS admin_user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL DEFAULT '',
  password VARCHAR(255) NOT NULL DEFAULT '',
  in_time DATETIME NOT NULL,
  role_id INT NOT NULL
);

-- code 验证码表
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

-- collect 收藏表
CREATE TABLE IF NOT EXISTS collect (
  topic_id INT NOT NULL,
  user_id INT NOT NULL,
  in_time DATETIME NOT NULL
);

-- comment 评论表
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

-- notification 通知表
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

-- oauth_user OAuth 第三方登录用户表
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

-- permission 权限表
CREATE TABLE IF NOT EXISTS permission (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL DEFAULT '',
  value VARCHAR(255) NOT NULL DEFAULT '',
  pid INT NOT NULL DEFAULT 0
);

-- role 角色表
CREATE TABLE IF NOT EXISTS role (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL DEFAULT ''
);

-- role_permission 角色权限关联表
CREATE TABLE IF NOT EXISTS role_permission (
  role_id INT NOT NULL,
  permission_id INT NOT NULL
);

-- sensitive_word 敏感词表
CREATE TABLE IF NOT EXISTS sensitive_word (
  id INT AUTO_INCREMENT PRIMARY KEY,
  word VARCHAR(255) NOT NULL
);

-- system_config 系统配置表
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

-- tag 标签表
CREATE TABLE IF NOT EXISTS tag (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL DEFAULT '',
  description VARCHAR(1000) NULL,
  icon VARCHAR(255) NULL,
  topic_count INT NOT NULL DEFAULT 0,
  in_time DATETIME NOT NULL
);

-- topic 话题表
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

-- topic_tag 话题标签关联表
CREATE TABLE IF NOT EXISTS topic_tag (
  tag_id INT NOT NULL,
  topic_id INT NOT NULL
);

-- user 用户表
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

-- =============================================================
-- 唯一约束与索引（追加方式，防止重复创建报错）
-- =============================================================

-- ADMIN_USER
CREATE INDEX IF NOT EXISTS idx_admin_user_role_id ON admin_user(role_id);

-- CODE
CREATE INDEX IF NOT EXISTS idx_code_user_id ON code(user_id);

-- COMMENT
CREATE INDEX IF NOT EXISTS idx_comment_topic_id ON comment(topic_id);
CREATE INDEX IF NOT EXISTS idx_comment_user_id ON comment(user_id);

-- COLLECT
CREATE INDEX IF NOT EXISTS idx_collect_topic_id ON collect(topic_id);
CREATE INDEX IF NOT EXISTS idx_collect_user_id ON collect(user_id);

-- NOTIFICATION
CREATE INDEX IF NOT EXISTS idx_notification_topic_id ON notification(topic_id);
CREATE INDEX IF NOT EXISTS idx_notification_user_id ON notification(user_id);
CREATE INDEX IF NOT EXISTS idx_notification_target_user_id ON notification(target_user_id);

-- OAUTH_USER
CREATE INDEX IF NOT EXISTS idx_oauth_user_user_id ON oauth_user(user_id);

-- PERMISSION
CREATE INDEX IF NOT EXISTS idx_permission_pid ON permission(pid);

-- ROLE_PERMISSION
CREATE INDEX IF NOT EXISTS idx_role_permission_role_id ON role_permission(role_id);
CREATE INDEX IF NOT EXISTS idx_role_permission_perm_id ON role_permission(permission_id);

-- SENSITIVE_WORD
CREATE INDEX IF NOT EXISTS idx_sensitive_word ON sensitive_word(word);

-- SYSTEM_CONFIG
CREATE INDEX IF NOT EXISTS idx_system_config_key ON system_config(key);

-- TAG
CREATE INDEX IF NOT EXISTS idx_tag_in_time ON tag(in_time);

-- TOPIC
CREATE INDEX IF NOT EXISTS idx_topic_user_id ON topic(user_id);

-- TOPIC_TAG
CREATE INDEX IF NOT EXISTS idx_topic_tag_tag_id ON topic_tag(tag_id);
CREATE INDEX IF NOT EXISTS idx_topic_tag_topic_id ON topic_tag(topic_id);

-- USER
CREATE INDEX IF NOT EXISTS idx_user_email ON user(email);

-- =============================================================
-- 种子数据：使用 MERGE INTO 确保幂等性
-- 每次执行时，若主键已存在则跳过插入
-- =============================================================

-- role
MERGE INTO role (id, name) KEY(id) VALUES (1, '超级管理员');
MERGE INTO role (id, name) KEY(id) VALUES (2, '审核员');

-- permission
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (1, '首页', 'index', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (2, '话题', 'topic', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (3, '评论', 'comment', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (4, '通知', 'notification', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (5, '用户', 'user', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (6, '验证码', 'code', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (7, '标签', 'tag', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (8, '权限', 'permission', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (9, '系统', 'system', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (10, '后台用户', 'admin_user', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (11, '仪表盘', 'index:index', 1);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (12, '话题列表', 'topic:list', 2);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (13, '话题编辑', 'topic:edit', 2);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (14, '话题删除', 'topic:delete', 2);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (15, '话题加精', 'topic:good', 2);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (16, '话题置顶', 'topic:top', 2);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (17, '评论列表', 'comment:list', 3);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (18, '评论编辑', 'comment:edit', 3);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (19, '评论删除', 'comment:delete', 3);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (20, '通知列表', 'notification:list', 4);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (21, '通知删除', 'notification:delete', 4);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (22, '用户列表', 'user:list', 5);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (23, '用户编辑', 'user:edit', 5);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (24, '用户删除', 'user:delete', 5);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (25, '验证码列表', 'code:list', 6);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (26, '标签列表', 'tag:list', 7);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (27, '标签编辑', 'tag:edit', 7);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (28, '标签删除', 'tag:delete', 7);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (29, '标签同步', 'tag:async', 7);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (30, '权限列表', 'permission:list', 8);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (31, '权限编辑', 'permission:edit', 8);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (32, '权限删除', 'permission:delete', 8);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (33, '角色', 'role', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (34, '日志', 'log', 0);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (35, '角色列表', 'role:list', 33);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (36, '角色编辑', 'role:edit', 33);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (37, '角色删除', 'role:delete', 33);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (38, '系统设置', 'system:edit', 9);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (39, '后台用户列表', 'admin_user:list', 10);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (40, '后台用户编辑', 'admin_user:edit', 10);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (41, '后台用户创建', 'admin_user:add', 10);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (42, '日志列表', 'log:list', 34);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (43, '用户刷新Token', 'user:refresh_token', 5);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (44, '权限添加', 'permission:add', 8);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (45, '索引单个话题', 'topic:index', 2);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (46, '索引全部话题', 'topic:index_all', 2);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (48, '删除索引', 'topic:delete_index', 2);
MERGE INTO permission (id, name, value, pid) KEY(id) VALUES (49, '删除所有话题索引', 'topic:delete_all_index', 2);

-- role_permission
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 11);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 12);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 13);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 14);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 15);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 16);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 17);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 18);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 19);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 20);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 21);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 22);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 23);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 24);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 25);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 26);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 27);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 28);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 29);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 30);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 31);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 32);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 35);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 36);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 37);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 38);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 39);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 40);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 41);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 42);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 43);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 44);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 45);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 46);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 48);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 49);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (1, 56);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 11);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 12);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 13);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 14);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 15);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 16);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 17);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 18);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 19);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 26);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 27);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 28);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 29);
MERGE INTO role_permission (role_id, permission_id) KEY(role_id, permission_id) VALUES (2, 56);

-- admin_user (密码: 123123 -> BCrypt 加密)
MERGE INTO admin_user (id, username, password, in_time, role_id) KEY(id)
VALUES (1, 'admin', '$2a$10$0F6RXnrQDF8SsOudYk7uhuWlqq3kjPuPm4UGeDCj0gvO8xj2pbZ4y', '2018-11-11 11:11:11', 1);

-- system_config (基础配置)
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (1, 'admin_remember_me_max_age', '30', '登录后台记住我功能记住时间，单位：天', 23, 'number', NULL, 1);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (2, 'base_url', 'http://localhost:8080', '网站部署后访问的域名', 23, 'url', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (3, 'comment_layer', '1', '评论盖楼形式显示', 23, 'radio', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (4, 'cookie_domain', 'localhost', '存cookie时用到的域名', 23, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (5, 'cookie_max_age', '604800', 'cookie有效期，单位秒，默认1周', 23, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (6, 'cookie_name', 'user_token', '存cookie时用到的名称', 23, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (7, 'create_comment_score', '5', '发布评论奖励的积分', 26, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (8, 'create_topic_score', '10', '创建话题奖励的积分', 26, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (9, 'delete_comment_score', '5', '删除评论要被扣除的积分', 26, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (10, 'delete_topic_score', '10', '删除话题要被扣除的积分', 26, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (11, 'intro', '<h5>测试社区</h5><p>测试环境</p>', '站点介绍', 23, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (12, 'mail_host', 'smtp.qq.com', '邮箱的smtp服务器地址', 24, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (13, 'mail_password', '', '发送邮件的邮箱密码', 24, 'password', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (14, 'mail_username', 'test@qq.com', '发送邮件的邮箱地址', 24, 'email', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (15, 'name', 'We-Link测试社区', '站点名称', 23, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (16, 'page_size', '20', '分页每页条数', 23, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (18, 'static_url', 'http://localhost:8080/static/upload/', '静态文件访问地址', 25, 'url', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (19, 'up_comment_score', '3', '点赞评论奖励评论作者的积分', 26, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (20, 'upload_avatar_size_limit', '2', '上传头像文件大小，单位MB，默认2MB', 25, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (21, 'upload_path', './static/upload/', '上传文件的路径', 25, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (22, 'up_topic_score', '3', '点赞话题奖励话题作者的积分', 26, 'number', NULL, 0);

-- 补充缺失的系统配置项
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (17, 'websocket', '0', '是否开启websocket功能', 45, 'radio', NULL, 1);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (39, 'search', '0', '是否开启搜索功能', 35, 'radio', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (44, 'topic_view_increase_interval', '600', '同用户浏览同一话题多长时间算一次浏览量', 23, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (89, 'comment_need_examine', '0', '评论是否需要审核', 23, 'radio', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (90, NULL, NULL, '系统代理', 0, NULL, NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (91, 'http_proxy', '', '代理地址', 90, 'text', NULL, 1);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (92, 'http_proxy_port', '', '代理端口', 90, 'number', NULL, 1);

-- 新增配置项：评论/话题输入风格
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (100, 'content_style', 'MD', '发帖或者回复的输入框语法风格', 23, 'select', 'RICH,MD', '1');

-- 补充 Redis 和 ES 配置项（值为空，表示未启用）
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (101, 'redis_host', '', 'redis服务host地址', 27, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (102, 'redis_port', '', 'redis服务端口', 27, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (103, 'redis_password', '', 'redis服务密码', 27, 'password', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (104, 'redis_timeout', '2000', 'redis超时时间', 27, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (105, 'redis_database', '0', 'redis数据库编号', 27, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (106, 'elasticsearch_host', '', 'elasticsearch服务地址', 35, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (107, 'elasticsearch_port', '', 'elasticsearch端口', 35, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (108, 'elasticsearch_index', '', 'elasticsearch索引名', 35, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (109, 'upload_image_size_limit', '5', '上传图片大小限制(MB)', 25, 'number', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (110, 'upload_video_size_limit', '20', '上传视频大小限制(MB)', 25, 'number', NULL, 0);

-- OAuth 配置
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (111, 'oauth_github_client_id', '', 'Github登录 ClientId', 40, 'text', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (112, 'oauth_github_client_secret', '', 'Github登录 ClientSecret', 40, 'password', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (113, 'oauth_github_callback_url', '', 'Github登录回调地址', 40, 'url', NULL, 0);

-- WebSocket 配置
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (114, 'websocket_url', '', 'WebSocket连接地址', 45, 'url', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (115, 'websocket_host', '', 'WebSocket主机名', 45, 'text', NULL, 1);

-- system_config 分组（pid=0）
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (23, NULL, NULL, '基础配置', 0, NULL, NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (24, NULL, NULL, '邮箱配置', 0, NULL, NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (25, NULL, NULL, '上传配置', 0, NULL, NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (26, NULL, NULL, '积分配置', 0, NULL, NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (27, NULL, NULL, 'Redis配置', 0, NULL, NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (35, NULL, NULL, 'Elasticsearch配置', 0, NULL, NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (40, NULL, NULL, 'Github登录配置', 0, '', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (45, NULL, NULL, 'WebSocket', 0, '', NULL, 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (48, 'theme', 'default', '系统主题', 23, 'select', 'default', 0);
MERGE INTO system_config (id, key, value, description, pid, type, option, reboot) KEY(id) VALUES (93, 'email_mobile_code_count', '5', '邮箱或手机验证码每天发送最大条数', 23, 'number', NULL, 0);
