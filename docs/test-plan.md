# We-Link Community 测试方案文档

> 版本: 1.0  
> 最后更新: 2026-06-12  
> 项目: We-Link Community (Spring Boot 2.2.2 + MyBatis-Plus + MySQL)

---

## 目录

1. [概述](#1-概述)
2. [测试架构全景](#2-测试架构全景)
3. [第一阶段：单元测试与集成测试（JUnit + SpringBoot）](#3-第一阶段单元测试与集成测试junit--springboot)
   - [3.1 技术选型与依赖](#31-技术选型与依赖)
   - [3.2 H2 内存数据库配置](#32-h2-内存数据库配置)
   - [3.3 测试分层策略](#33-测试分层策略)
   - [3.4 测试覆盖清单](#34-测试覆盖清单)
   - [3.5 测试基类设计](#35-测试基类设计)
   - [3.6 测试编写规范](#36-测试编写规范)
4. [第二阶段：端到端自动化测试（Playwright）](#4-第二阶段端到端自动化测试playwright)
   - [4.1 技术选型与环境](#41-技术选型与环境)
   - [4.2 测试目录结构](#42-测试目录结构)
   - [4.3 测试场景覆盖](#43-测试场景覆盖)
   - [4.4 测试用例设计](#44-测试用例设计)
   - [4.5 数据准备与清理](#45-数据准备与清理)
5. [第三阶段：边界与补充测试](#5-第三阶段边界与补充测试)
6. [CI/CD 集成（GitHub Actions）](#6-cicd-集成github-actions)
7. [测试质量指标与验收标准](#7-测试质量指标与验收标准)
8. [附录](#8-附录)

---

## 1. 概述

### 1.1 项目背景

We-Link Community 是一个基于 Java/Spring Boot 的开源论坛系统，提供用户注册登录、话题发布评论、标签分类、后台管理等功能。项目当前仅有 2 个测试文件，缺乏系统性的测试覆盖。

### 1.2 测试目标

- **单元测试**：验证所有 Service 层和 Util 工具类的核心业务逻辑
- **集成测试**：验证 Controller 层 API 接口的正确性，测试与数据库的交互
- **E2E 测试**：通过浏览器自动化模拟真实用户操作流程
- **质量门槛**：核心模块测试覆盖率 ≥ 80%，确保主要功能无回归

### 1.3 总体策略

| 阶段 | 内容 | 时间建议 |
|------|------|----------|
| **第一阶段** | Service 层单元测试 + API 集成测试 | 优先实施 |
| **第二阶段** | Playwright E2E 测试 | 第一阶段完成后 |
| **第三阶段** | 边界测试、安全测试、异常场景补充 | 前两阶段稳定后 |

---

## 2. 测试架构全景

```
we-link/
├── src/
│   ├── main/java/co/yiiu/welink/        # 生产代码
│   └── test/java/co/yiiu/welink/        # 测试代码
│       ├── WeLinkApplicationTests.java   # 应用上下文启动测试
│       ├── config/                       # 测试配置
│       │   └── TestConfig.java
│       ├── util/                         # Util 工具类测试
│       │   ├── StringUtilTest.java
│       │   ├── DateUtilTest.java
│       │   ├── MD5UtilTest.java
│       │   ├── SensitiveWordUtilTest.java
│       │   ├── BCryptTest.java
│       │   └── MarkdownUtilTest.java
│       ├── service/                      # Service 层单元测试
│       │   ├── UserServiceTest.java
│       │   ├── TopicServiceTest.java
│       │   ├── CommentServiceTest.java
│       │   ├── TagServiceTest.java
│       │   ├── CollectServiceTest.java
│       │   ├── NotificationServiceTest.java
│       │   ├── CodeServiceTest.java
│       │   ├── AdminUserServiceTest.java
│       │   ├── SensitiveWordServiceTest.java
│       │   └── SystemConfigServiceTest.java
│       ├── controller/                   # Controller 集成测试
│       │   ├── api/
│       │   │   ├── IndexApiControllerTest.java
│       │   │   ├── TopicApiControllerTest.java
│       │   │   ├── CommentApiControllerTest.java
│       │   │   ├── UserApiControllerTest.java
│       │   │   ├── CollectApiControllerTest.java
│       │   │   └── NotificationApiControllerTest.java
│       │   └── front/
│       │       ├── IndexControllerTest.java
│       │       ├── TopicControllerTest.java
│       │       ├── UserControllerTest.java
│       │       └── CommentControllerTest.java
│       └── resources/                    # 测试资源
│           └── application-test.yml      # 测试配置文件
│
├── e2e/                                  # Playwright E2E 测试
│   ├── package.json
│   ├── playwright.config.ts
│   ├── tsconfig.json
│   ├── fixtures/                         # 测试夹具 (数据准备)
│   │   └── test-data.ts
│   ├── pages/                            # Page Object 模型
│   │   ├── HomePage.ts                   # 首页（话题列表/Tab切换）
│   │   ├── LoginPage.ts                  # 登录页（表单/AJAX提交）
│   │   ├── RegisterPage.ts               # 注册页（表单提交）
│   │   ├── TopicDetailPage.ts            # 话题详情（显示/评论提交）
│   │   ├── TopicCreatePage.ts            # 创建话题
│   │   ├── SearchPage.ts                 # 搜索页
│   │   └── admin/
│   │       ├── AdminLoginPage.ts         # 管理员登录
│   │       ├── DashboardPage.ts          # 后台仪表盘
│   │       └── TopicManagePage.ts        # 后台话题管理
│   ├── tests/                            # 测试用例
│   │   ├── api/                          # API 接口验证
│   │   │   ├── login.spec.ts             # 登录 API（成功/失败）
│   │   │   ├── topic.spec.ts             # 话题 API（详情/点赞）
│   │   │   └── comment.spec.ts           # 评论 API（创建）
│   │   ├── auth/
│   │   │   ├── register.spec.ts          # 注册（成功/重复/校验）
│   │   │   ├── login.spec.ts             # 登录（成功/失败/不存在）
│   │   │   └── logout.spec.ts            # 登出
│   │   ├── home.spec.ts                  # 首页加载/Tab切换
│   │   ├── topic/
│   │   │   ├── create-topic.spec.ts      # 创建话题
│   │   │   └── view-topic.spec.ts        # 话题详情/不存在
│   │   ├── comment/
│   │   │   └── create-comment.spec.ts    # 评论创建+断言
│   │   ├── user/
│   │   │   └── profile.spec.ts           # 用户个人页/话题列表
│   │   ├── search.spec.ts                # 搜索（有结果/空结果）
│   │   ├── tags.spec.ts                  # 标签页
│   │   └── admin/
│   │       ├── admin-login.spec.ts       # 管理员登录页可达
│   │       └── topic-manage.spec.ts      # 后台话题管理页可达
│   └── utils/
│       └── auth.ts                       # 认证辅助函数
│
├── .github/
│   └── workflows/
│       └── test.yml                      # GitHub Actions 测试流水线
```

---

## 3. 第一阶段：单元测试与集成测试（JUnit + SpringBoot）

### 3.1 技术选型与依赖

项目 `pom.xml` 中已包含 `spring-boot-starter-test`，需要新增 `H2` 依赖：

```xml
<!-- 在 pom.xml 的 <dependencies> 中添加 -->

<!-- H2 内存数据库（测试用） -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito（spring-boot-starter-test 已包含，如版本不够可覆盖） -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- JSON Path（API 测试断言用） -->
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
    <scope>test</scope>
</dependency>
```

### 3.2 H2 内存数据库配置

创建 `src/test/resources/application-test.yml`：

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:we_link_test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password:
  flyway:
    enabled: false
  h2:
    console:
      enabled: true

# 使用 MyBatis-Plus 自动建表（通过 Mapper XML 或实体扫描）
mybatis-plus:
  global-config:
    banner: false
  configuration:
    # 测试时开启 SQL 日志便于调试
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# 站点测试配置
site:
  datasource_driver: org.h2.Driver
  datasource_url: jdbc:h2:mem:we_link_test;MODE=MySQL
  datasource_username: sa
  datasource_password:
```

**H2 兼容性说明**：
- 使用 `MODE=MySQL` 模拟 MySQL 行为
- MyBatis-Plus 的 `idType.AUTO` 在 H2 中正常工作
- 部分 MySQL 特有函数需要处理（如 `DATE_FORMAT`），在测试 SQL 中做适配

#### 数据初始化方案

对于需要 schema 初始化的测试，有两种可选方案：

**方案 A：SQL 脚本初始化（推荐）**

创建 `src/test/resources/schema.sql` 和 `src/test/resources/data.sql`：

```sql
-- schema.sql - 表结构定义（H2 兼容语法）
CREATE TABLE IF NOT EXISTS user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  password VARCHAR(255),
  avatar VARCHAR(500),
  email VARCHAR(100),
  mobile VARCHAR(20),
  website VARCHAR(500),
  bio VARCHAR(500),
  score INT DEFAULT 0,
  token VARCHAR(255),
  email_notification BOOLEAN DEFAULT TRUE,
  active BOOLEAN DEFAULT TRUE,
  in_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  telegram_name VARCHAR(100)
);

-- ... 其他表类似
```

**方案 B：Flyway 迁移脚本初始化**

如果启用 Flyway（`spring.flyway.enabled=true`），将 `src/main/resources/db/migration` 下的 SQL 迁移脚本用于 H2。需要确保所有迁移脚本兼容 H2 语法。

### 3.3 测试分层策略

| 层次 | 测试类型 | 技术方案 | 依赖 |
|------|----------|----------|------|
| **Util 工具类** | 纯单元测试 | `@Test` (无 Spring 上下文) | 无 |
| **Service 层** | 单元测试 | `@SpringBootTest` + H2 内存库 | Mapper 操作真实数据库 |
| **Controller API** | 集成测试 | `@WebMvcTest` 或 `@SpringBootTest` + `MockMvc` / `TestRestTemplate` | 完整上下文 |
| **Controller 页面** | 集成测试 | `@SpringBootTest` + `MockMvc` | 完整上下文 |

#### 3.3.1 Util 工具类测试（无 Spring 上下文）

纯静态工具类，不需要 Spring 容器，使用 JUnit 直接测试：

```java
public class SensitiveWordUtilTest {

    @Test
    public void testReplaceSensitiveWord() {
        // 准备
        String text = "这是一段包含敏感词的文本";

        // 执行
        String result = SensitiveWordUtil.replaceSensitiveWord(text, "*", SensitiveWordUtil.MinMatchType);

        // 断言
        assertNotNull(result);
        assertFalse(result.contains("敏感词"));
    }
}
```

#### 3.3.2 Service 层测试（H2 真实数据库交互）

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Resource
    private IUserService userService;

    @After
    public void tearDown() {
        // 每个测试后清理数据，保持隔离
        // 或使用 @Transactional 自动回滚
    }

    @Test
    @Transactional  // 测试完成后自动回滚
    public void testRegister() {
        // 执行注册
        User user = userService.addUser(
            "testuser", "Password123", null,
            "test@example.com", "bio", "https://example.com", false
        );

        // 断言
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("testuser", user.getUsername());
        assertTrue(user.getActive());
    }

    @Test
    @Transactional
    public void testSelectByUsername() {
        // 准备：先注册一个用户
        userService.addUser("findme", "Password123", null, "find@test.com", null, null, false);

        // 执行：按用户名查找
        User found = userService.selectByUsername("findme");

        // 断言
        assertNotNull(found);
        assertEquals("findme", found.getUsername());
    }

    @Test
    @Transactional
    public void testDeleteUser() {
        // 准备
        User user = userService.addUser("todelete", "Password123", null, "del@test.com", null, null, false);

        // 执行
        userService.deleteUser(user.getId());

        // 断言
        assertNull(userService.selectById(user.getId()));
    }
}
```

#### 3.3.3 Controller API 集成测试（MockMvc）

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class IndexApiControllerTest {

    @Resource
    private MockMvc mockMvc;

    @Resource
    private IUserService userService;

    private String userToken;

    @Before
    public void setup() {
        // 初始化测试用户
        User user = userService.addUser(
            "apitest", "Password123", null,
            "api@test.com", null, null, false
        );
        userToken = user.getToken();
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/index")
                .param("pageNo", "1")
                .param("tab", "all")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").isMap());
    }

    @Test
    public void testLogin() throws Exception {
        // 先获取验证码
        mockMvc.perform(get("/api/login")
                .param("username", "apitest")
                .param("password", "Password123")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
```

### 3.4 测试覆盖清单

#### 3.4.1 Util 工具类测试

| 类名 | 测试方法数 | 关键测试点 |
|------|-----------|-----------|
| `StringUtilTest` | ≥6 | 随机字符串生成、正则校验（密码/邮箱/用户名/手机号）、空值处理 |
| `DateUtilTest` | ≥4 | 日期格式化（多种模式）、时间戳转换、时区处理 |
| `MD5UtilTest` | ≥3 | MD5 编码、一致性校验、不同输入输出 |
| `SensitiveWordUtilTest` | ≥5 | 敏感词替换、最小/最大匹配模式、无敏感词输入 |
| `MarkdownUtilTest` | ≥4 | Markdown→HTML 渲染、表格/链接/代码块渲染 |
| `JsonUtilTest` | ≥3 | JSON 序列化/反序列化、集合类型处理 |
| `IpUtilTest` | ≥2 | IPv4/IPv6 解析、X-Forwarded-For 处理 |
| `CookieUtilTest` | ≥2 | Cookie 设置/读取/清除 |
| `HashUtilTest` | ≥2 | 哈希计算、一致性 |
| `BCryptTest` | ≥3 | 密码编码、密码匹配验证、不同 salt 输出不同 |

#### 3.4.2 Service 层测试

| 服务类 | 测试方法数 | 关键测试点 |
|--------|-----------|-----------|
| `UserServiceTest` | ≥8 | **注册**（创建用户/积分初始化/token 生成）、**查询**（按用户名/邮箱/手机号/token/ID）、**更新**（用户信息/积分）、**删除**（级联清理）、**活跃用户排行** |
| `TopicServiceTest` | ≥8 | **发布话题**（标签关联/积分增加/ES 索引）、**查询**（分页/按标签/id/关键词）、**更新**（浏览量/内容/标签变更）、**删除**（级联清理/积分扣减/索引删除）、**点赞/取消点赞**（积分变动） |
| `CommentServiceTest` | ≥6 | **创建评论**（话题评论数+1/通知/积分）、**查询**（按话题/用户）、**删除**（评论数-1）、**点赞** |
| `TagServiceTest` | ≥5 | **创建标签**（去重/slug 生成）、**标签话题数统计**、**按热度排序**、**标签关联查询** |
| `CollectServiceTest` | ≥4 | **收藏/取消收藏**、**话题收藏数统计**、**查询用户收藏** |
| `NotificationServiceTest` | ≥4 | **通知生成**（评论/@用户）、**通知已读**、**按用户清理** |
| `CodeServiceTest` | ≥5 | **验证码生成/校验**（邮箱/手机号）、**过期处理**、**发送频率限制** |
| `SystemConfigServiceTest` | ≥3 | **配置 CRUD**、**缓存机制**、**默认值** |
| `SensitiveWordServiceTest` | ≥3 | **敏感词 CRUD**、**加载到缓存** |
| `AdminUserServiceTest` | ≥5 | **管理员登录认证**（Shiro）、**角色权限验证**、**管理员 CRUD** |
| `RoleServiceTest` | ≥3 | **角色 CRUD**、**权限关联** |

#### 3.4.3 Controller API 集成测试

| 控制器 | 测试方法数 | 关键测试点 |
|--------|-----------|-----------|
| `IndexApiControllerTest` | ≥8 | 首页列表、登录/注册/手机号登录、验证码发送、标签列表、文件上传、用户名唯一性校验 |
| `TopicApiControllerTest` | ≥6 | 话题列表、话题详情、创建话题、更新话题、删除话题、点赞 |
| `CommentApiControllerTest` | ≥5 | 评论列表、创建评论、删除评论、评论点赞 |
| `UserApiControllerTest` | ≥5 | 用户信息、个人资料更新、积分排行、通知列表 |
| `CollectApiControllerTest` | ≥4 | 收藏/取消收藏、收藏列表 |
| `NotificationApiControllerTest` | ≥3 | 通知列表、标记已读 |
| `SettingsApiControllerTest` | ≥2 | 系统设置接口 |

#### 3.4.4 异常与边界场景

| 类别 | 测试场景 |
|------|----------|
| **参数校验** | 用户名空值、密码不符合规则、邮箱格式错误、分页参数越界 |
| **权限校验** | 未登录访问需登录接口、修改他人话题/评论、非管理员访问管理接口 |
| **资源不存在** | 话题不存在、用户不存在、标签不存在 |
| **重复操作** | 重复注册用户名/邮箱、重复收藏、重复点赞 |
| **频率限制** | 验证码发送太频繁 |
| **安全场景** | XSS 过滤、SQL 注入尝试、敏感词替换 |

### 3.5 测试基类设计

创建基类以减少重复代码：

```java
// BaseServiceTest.java - Service 层测试基类
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional  // 每个测试自动回滚
public abstract class BaseServiceTest {

    @Before
    public void baseSetup() {
        // 通用初始化逻辑
    }

    // 辅助方法：快速创建一个测试用户
    protected User createTestUser(String username) {
        // ...
    }

    // 辅助方法：快速创建一个测试话题
    protected Topic createTestTopic(User user, String title) {
        // ...
    }
}
```

```java
// BaseApiControllerTest.java - API 控制器测试基类
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseApiControllerTest {

    @Resource
    protected MockMvc mockMvc;

    @Resource
    protected IUserService userService;

    protected String userToken;

    @Before
    public void setupUser() {
        User user = userService.addUser(
            "test_" + UUID.randomUUID().toString().substring(0, 6),
            "TestPass123", null, "test@test.com", null, null, false
        );
        userToken = user.getToken();
    }

    // 执行带认证的请求
    protected ResultActions authenticatedRequest(MockHttpServletRequestBuilder requestBuilder) {
        return mockMvc.perform(requestBuilder
            .header("Authorization", "Bearer " + userToken)
            .sessionAttr("_user", userService.selectByToken(userToken)));
    }

    // JSON 格式断言
    protected ResultMatcher okResult() {
        return jsonPath("$.code").value(200);
    }
}
```

### 3.6 测试编写规范

1. **命名规范**：
   - 类名：`{被测类名}Test.java`
   - 方法名：`test{被测方法}_{场景}_{期望结果}`
   - 示例：`testRegister_ValidInput_Success`

2. **断言模式**：遵循 Arrange - Act - Assert (AAA) 模式

3. **数据隔离**：
   - Service 测试使用 `@Transactional` 实现自动回滚
   - MockMvc 测试保持独立数据，测试间不共享状态
   - Util 测试不依赖任何外部状态

4. **Mock 策略**：
   - Service 层内部测试：真实数据库（H2）+ 真实 Mapper，不 Mock
   - 对外部依赖（ES、Redis、OSS、短信/邮件）：Mock 或使用 `@MockBean`
   - Controller 层：通过 MockMvc 注入完整上下文，Service 层使用真实实现

5. **覆盖率目标**：
   - Service 层核心方法覆盖率 ≥ 85%
   - Util 工具类覆盖率 ≥ 90%
   - Controller API 接口覆盖率 ≥ 80%
   - 总行覆盖率 ≥ 70%

---

## 4. 第二阶段：端到端自动化测试（Playwright）

### 4.1 技术选型与环境

- **框架**：Playwright + TypeScript
- **浏览器**：Chromium（默认）+ Firefox + WebKit/Safari — 三浏览器并行覆盖
- **Node.js**：≥ 18.x
- **测试运行器**：Playwright Test Runner
- **测试数据**：通过 E2E 专用 API (`/api/e2e/`) 创建，H2 内存数据库

### 4.2 测试目录结构（已在架构图中展示）

### 4.3 测试场景覆盖

> 以下场景已实现（✅）和待实现（⬜）的状态标记

#### 4.3.1 核心用户流程

| # | 场景 | 步骤 | 断言 | 状态 |
|---|------|------|------|------|
| **UC-01** | 注册 → API 创建 → 话题 → 评论 → 登出 | 通过 API 和页面交互模拟 | 每一步操作成功且数据一致 | ✅ 已实现 |
| **UC-02** | 搜索话题 | 搜索关键词 | 结果列表中包含匹配项 | ✅ 已实现 |
| **UC-03** | 未登录用户访问限制页面 | 直接访问 /settings, /topic/create | 重定向到登录页 | ✅ 已实现 |

#### 4.3.2 前台页面测试

| # | 页面 | 测试点 | 状态 |
|---|------|--------|------|
| **P-01** | 首页 | 页面加载、Tab 切换按钮(all/good/hot)、未用户登录链接 | ✅ 已实现 |
| **P-02** | 话题详情 | 已创建话题的内容渲染、不存在话题的错误页 | ✅ 已实现 |
| **P-03** | 标签页 | 标签页面加载 | ✅ 已实现 |
| **P-04** | 用户个人页 | 用户信息、话题列表 | ✅ 已实现 |
| **P-05** | 搜索页 | 关键词搜索有结果、空搜索正常显示 | ✅ 已实现 |

#### 4.3.3 后台管理流程

| # | 场景 | 步骤 | 状态 |
|---|------|------|------|
| **A-01** | 管理员登录页可达 | 访问 /admin/login | ✅ 已实现 |
| **A-02** | 话题管理页可达 | 后台话题列表页 | ✅ 已实现 |
| **A-03** | 用户管理页可达 | 后台用户列表页 | ✅ 已实现 |
| **A-04** | 评论管理页可达 | 后台评论列表页 | ✅ 已实现 |
| **A-05** | 标签管理页可达 | 后台标签列表页 | ✅ 已实现 |
| **A-06** | 系统配置页可达 | 后台系统设置页 | ✅ 已实现 |
| **A-07** | 全量管理后台可达 | 用户/评论/标签/系统/权限 | ✅ 已实现 |

#### 4.3.4 API 接口验证

| # | 接口 | 验证点 | 状态 |
|---|------|--------|------|
| **API-01** | POST /api/login | 成功返回 token、错误密码返回错误 | ✅ 已实现 |
| **API-02** | POST /api/register | 成功注册、重复用户名报错 | ✅ 已实现 |
| **API-03** | GET /api/topic/{id} | 话题详情返回数据 | ✅ 已实现 |
| **API-04** | GET /api/topic/{id}/vote | 不同用户点赞成功 | ✅ 已实现 |
| **API-05** | POST /api/comment | 创建评论返回数据与话题 ID 一致 | ✅ 已实现 |
| **API-06** | GET /api/index | 分页参数、接口可达 | ✅ 已实现 |

### 4.4 测试用例设计

#### 4.4.1 Page Object 模型示例

```typescript
// e2e/pages/LoginPage.ts
import { Page } from '@playwright/test';

export class LoginPage {
  constructor(private page: Page) {}

  async goto() {
    await this.page.goto('/login');
  }

  async login(username: string, password: string) {
    await this.page.fill('input[name="username"]', username);
    await this.page.fill('input[name="password"]', password);
    await this.page.click('button[type="submit"]');
  }

  async getErrorMessage() {
    return this.page.textContent('.error-message');
  }

  async isLoggedIn() {
    return this.page.isVisible('.user-avatar');
  }
}
```

#### 4.4.2 核心测试用例示例

```typescript
// e2e/tests/auth/register.spec.ts
import { test, expect } from '@playwright/test';
import { RegisterPage } from '../../pages/RegisterPage';

test.describe('用户注册', () => {

  test('成功注册新用户', async ({ page }) => {
    const registerPage = new RegisterPage(page);
    const username = `testuser_${Date.now()}`;

    await registerPage.goto();
    await registerPage.register(username, 'Password123', `${username}@test.com`);

    // 注册成功后应跳转首页
    await expect(page).toHaveURL('/');
    await expect(page.locator('.user-avatar')).toBeVisible();
  });

  test('使用已存在的用户名注册应报错', async ({ page }) => {
    const registerPage = new RegisterPage(page);
    const username = 'existing_user';

    // 先通过 API 创建一个用户
    // ...

    await registerPage.goto();
    await registerPage.register(username, 'Password123', 'other@test.com');

    // 应显示用户名已存在的错误
    await expect(page.locator('.error-message')).toContainText('用户名已存在');
  });

  test('注册表单校验', async ({ page }) => {
    const registerPage = new RegisterPage(page);
    await registerPage.goto();

    // 密码不符合规则
    await page.fill('input[name="password"]', '123');
    await page.click('button[type="submit"]');
    await expect(page.locator('.error-message')).toBeVisible();
  });
});
```

```typescript
// e2e/tests/topic/create-topic.spec.ts
import { test, expect } from '@playwright/test';
import { LoginPage } from '../../pages/LoginPage';
import { TopicCreatePage } from '../../pages/TopicCreatePage';

test.describe('话题发布', () => {

  test.beforeEach(async ({ page }) => {
    // 先登录
    const loginPage = new LoginPage(page);
    await loginPage.goto();
    await loginPage.login('testuser', 'Password123');
  });

  test('成功发布话题', async ({ page }) => {
    const topicPage = new TopicCreatePage(page);
    const title = `测试话题标题 ${Date.now()}`;
    const content = '这是测试话题的内容，用于验证发布功能';

    await topicPage.goto();
    await topicPage.createTopic(title, content, ['测试标签']);

    // 应跳转到话题详情页
    await expect(page.locator('h1')).toContainText(title);
  });

  test('空标题不能发布', async ({ page }) => {
    const topicPage = new TopicCreatePage(page);
    await topicPage.goto();
    await topicPage.createTopic('', '内容', []);

    // 页面应停留在创建页且显示错误
    await expect(page).toHaveURL('/topic/create');
  });
});
```

#### 4.4.3 API 接口测试示例

```typescript
// e2e/tests/api/login.spec.ts
import { test, expect } from '@playwright/test';

test.describe('API 登录接口', () => {

  test('POST /api/login - 成功登录', async ({ request }) => {
    const response = await request.post('/api/login', {
      data: {
        username: 'testuser',
        password: 'Password123',
        captcha: 'test'
      }
    });

    const body = await response.json();
    expect(response.ok()).toBeTruthy();
    expect(body.code).toBe(200);
    expect(body.data).toHaveProperty('token');
    expect(body.data.user).toHaveProperty('username', 'testuser');
  });

  test('POST /api/login - 错误密码', async ({ request }) => {
    const response = await request.post('/api/login', {
      data: {
        username: 'testuser',
        password: 'wrongpassword',
        captcha: 'test'
      }
    });

    const body = await response.json();
    expect(body.code).not.toBe(200);
  });
});
```

#### 4.4.4 API 接口测试示例

```typescript
// e2e/tests/api/comment.spec.ts
import { test, expect } from '@playwright/test';

test.describe('API 评论接口', () => {

  test('创建评论应返回成功', async ({ request }) => {
    // 1. 注册用户获取 token
    const regRes = await request.post('/api/e2e/register', {
      data: { username: 'apicmt001', password: 'ApiCmtPass123456', email: 'apicmt@test.com' },
    });
    const { token } = (await regRes.json()).detail;

    // 2. 创建话题
    const topicRes = await request.post('/api/topic', {
      data: { title: '评论话题', content: '测试', tags: 'E2E测试' },
      headers: { token },
    });
    const topicId = (await topicRes.json()).detail.id;

    // 3. 创建评论并验证
    const commentRes = await request.post('/api/comment', {
      data: { content: 'API测试评论', topicId: String(topicId) },
      headers: { token },
    });
    const commentBody = await commentRes.json();
    expect(commentBody.code).toBe(200);
    expect(commentBody.detail.content).toBe('API测试评论');
    expect(commentBody.detail.topicId).toBe(topicId);
  });
});
```

#### 4.4.5 首页测试示例

```typescript
// e2e/tests/home.spec.ts
import { test, expect } from '@playwright/test';

test.describe('首页', () => {

  test('首页应正常加载', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('body')).not.toBeEmpty();
  });

  test('Tab 切换按钮应可见', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('a[href*="tab=all"]').first()).toBeVisible();
    await expect(page.locator('a[href*="tab=good"]').first()).toBeVisible();
    await expect(page.locator('a[href*="tab=hot"]').first()).toBeVisible();
  });
});
```

### 4.5 数据准备与清理

**策略**：使用独立的测试数据库，每个测试文件使用唯一前缀的用户名/邮箱，避免数据冲突。测试运行结束后，清理创建的测试数据。

```typescript
// e2e/utils/db.ts
import mysql from 'mysql2/promise';

const pool = mysql.createPool({
  host: 'localhost',
  user: 'root',
  password: '123456',
  database: 'we_link_test',
  waitForConnections: true,
});

export async function cleanupTestData(prefix: string) {
  const conn = await pool.getConnection();
  try {
    // 删除该前缀的测试用户相关数据
    await conn.execute(
      'DELETE FROM user WHERE username LIKE ?',
      [`${prefix}%`]
    );
  } finally {
    conn.release();
  }
}
```

---

## 5. 第三阶段：边界与补充测试

### 5.1 边界值测试

| 模块 | 边界场景 |
|------|----------|
| 用户名 | 2 字符最短、16 字符最长、包含特殊字符 |
| 话题标题 | 空标题、超长标题、纯空格标题 |
| 话题内容 | 超大 HTML 内容、纯 Markdown、空内容 |
| 分页 | pageNo=0、pageNo=超大值、pageSize 默认值 |
| 文件上传 | 空文件、超大文件、非图片格式、并发上传 |

### 5.2 安全测试

| 类型 | 测试点 |
|------|--------|
| XSS | 话题内容/标题注入 `<script>`、评论注入 HTML |
| SQL 注入 | 用户名/搜索词注入 `' OR 1=1--` |
| CSRF | 跨站请求伪造防护 |
| 权限绕过 | 直接访问管理 URL、修改 URL 中的资源 ID |
| 敏感信息泄露 | 错误信息中是否包含 SQL/堆栈信息 |

### 5.3 并发测试

| 场景 | 测试方法 |
|------|----------|
| 同一用户重复注册 | 并发调用注册接口 |
| 热门话题同时点赞 | 多线程点赞同一话题 |
| 同时收藏/取消 | 快速交替操作 |
| 评论 + 删除话题 | 并发操作的一致性 |

---

## 6. CI/CD 集成（GitHub Actions）

创建 `.github/workflows/test.yml`：

```yaml
name: Test Pipeline

on:
  push:
    branches: [ master, dev ]
  pull_request:
    branches: [ master ]

jobs:
  # ==== Job 1: Java 单元测试 & 集成测试 ====
  java-test:
    name: Java Unit & Integration Tests
    runs-on: ubuntu-latest

    services:
      mysql:
        image: mysql:5.7
        env:
          MYSQL_DATABASE: we_link_test
          MYSQL_ROOT_PASSWORD: testpass
        ports:
          - 3306:3306
        options: >-
          --health-cmd "mysqladmin ping -h localhost"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 10

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 8
        uses: actions/setup-java@v4
        with:
          java-version: '8'
          distribution: 'temurin'
          cache: maven

      - name: Initialize DB schema
        run: |
          mysql -h 127.0.0.1 -u root -ptestpass we_link_test < src/main/resources/db/migration/init.sql || true

      - name: Run Unit & Integration Tests
        run: mvn test -Ptesting

      - name: Generate JaCoCo Coverage Report
        run: mvn jacoco:report

      - name: Upload Coverage Report
        uses: actions/upload-artifact@v4
        with:
          name: coverage-report
          path: target/site/jacoco/

  # ==== Job 2: Playwright E2E 测试 ====
  playwright-test:
    name: Playwright E2E Tests
    runs-on: ubuntu-latest
    needs: java-test  # 等待应用编译完成

    services:
      mysql:
        image: mysql:5.7
        env:
          MYSQL_DATABASE: we_link
          MYSQL_ROOT_PASSWORD: root
        ports:
          - 3306:3306
        options: >-
          --health-cmd "mysqladmin ping -h localhost"
          --health-interval 10s
          --health-timeout 5s
          --health-retries 10

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 8
        uses: actions/setup-java@v4
        with:
          java-version: '8'
          distribution: 'temurin'
          cache: maven

      - name: Build & Start Application
        run: |
          mvn clean package -DskipTests
          # 后台启动应用，等待就绪
          java -jar target/we-link.jar --spring.profiles.active=prod &
          sleep 30

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: 18

      - name: Install Playwright Dependencies
        working-directory: ./e2e
        run: |
          npm ci
          npx playwright install chromium --with-deps

      - name: Run Playwright Tests
        working-directory: ./e2e
        run: npx playwright test

      - name: Upload Playwright Report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: playwright-report
          path: e2e/playwright-report/
```

**Maven POM 中新增配置**：

```xml
<!-- JaCoCo 覆盖率插件 -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>

<!-- Maven Failsafe 插件（集成测试） -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>2.22.2</version>
    <executions>
        <execution>
            <goals><goal>integration-test</goal><goal>verify</goal></goals>
        </execution>
    </executions>
</plugin>
```

### 6.1 环境变量配置

在 GitHub Actions 的 Secrets 中配置测试环境变量：

| 变量名 | 说明 |
|--------|------|
| `TEST_DB_URL` | 测试数据库连接地址 |
| `TEST_DB_USER` | 数据库用户名 |
| `TEST_DB_PASS` | 数据库密码 |
| `PLAYWRIGHT_BASE_URL` | E2E 测试的应用地址 |

---

## 7. 测试质量指标与验收标准

### 7.1 量化指标

| 指标 | 目标值 | 测量工具 |
|------|--------|----------|
| 代码行覆盖率 | ≥ 70% | JaCoCo |
| Service 层覆盖率 | ≥ 85% | JaCoCo |
| Util 层覆盖率 | ≥ 90% | JaCoCo |
| Controller 层覆盖率 | ≥ 80% | JaCoCo |
| E2E 核心场景通过率 | 100% | Playwright |
| 测试用例总数（Java） | ≥ 80 个 | Surefire 报告（当前 254 个） |
| E2E 测试用例数 | ≥ 20 个 | Playwright 报告（当前 26 个） |

### 7.2 质量门禁（Quality Gate）

1. **PR 检查级别**：
   - 🔴 **阻塞**：核心测试失败、覆盖率低于 60%
   - 🟡 **警告**：覆盖率 60%-70%、边界测试缺失
   - 🟢 **通过**：所有检查通过

2. **持续监控**：
   - 每次 Push/PR 自动运行全部单元测试 + 集成测试
   - 每日凌晨运行完整 E2E 测试套件
   - 每次发版前运行全量测试

### 7.3 测试报告

- Java 测试：Maven Surefire 报告 + JaCoCo HTML 覆盖率报告
- Playwright：HTML 报告（包含截图、视频、trace 文件）
- CI 中所有报告保存为 Actions Artifact，保留 30 天

---

## 8. 附录

### 8.1 依赖版本汇总

| 依赖 | 版本 | 用途 |
|------|------|------|
| JUnit 4 | 4.12 (Spring Boot 默认) | 单元测试框架 |
| Spring Boot Test | 2.2.2.RELEASE | 集成测试支持 |
| H2 | 1.4.200 | 内存数据库 |
| Mockito | 3.x (Spring Boot 默认) | Mock 框架 |
| JaCoCo | 0.8.11 | 覆盖率统计 |
| Playwright | latest | E2E 测试框架 |
| Maven Surefire | 2.22.2 | 单元测试运行器 |
| Maven Failsafe | 2.22.2 | 集成测试运行器 |

### 8.2 参考资源

- [Spring Boot Testing Documentation](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/html/spring-boot-features.html#boot-features-testing)
- [Playwright Documentation](https://playwright.dev/docs/intro)
- [JaCoCo Usage Guide](https://www.jacoco.org/jacoco/trunk/doc/)
- [H2 Database Documentation](http://www.h2database.com/html/main.html)

### 8.3 快速启动测试

```bash
# 1. 运行所有 Java 测试（单元+集成）
mvn test

# 2. 生成覆盖率报告
mvn jacoco:report
open target/site/jacoco/index.html

# 3. 仅运行某个测试类
mvn test -Dtest=UserServiceTest

# 4. 启动 E2E 测试应用（H2 内存数据库，无需 MySQL）
java -jar target/we-link.jar --spring.profiles.active=e2e &
# 等待应用启动完成（约 6 秒）

# 5. 运行 Playwright E2E 测试
cd e2e
npm install
npx playwright test

# 6. 仅运行特定 E2E 测试文件
npx playwright test tests/auth/login.spec.ts

# 7. 带 UI 交互模式查看
npx playwright test --ui

# 8. 查看 E2E 测试报告
npx playwright show-report

# 9. 本地启动开发数据库（可选）
docker-compose up -d mysql
```