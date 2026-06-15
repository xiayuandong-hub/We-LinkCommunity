package co.yiiu.welink.config;

import co.yiiu.welink.model.User;
import co.yiiu.welink.service.ICollectService;
import co.yiiu.welink.service.ICommentService;
import co.yiiu.welink.service.INotificationService;
import co.yiiu.welink.service.ITopicService;
import co.yiiu.welink.service.IUserService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * API Controller 集成测试基类
 * <p>
 * 提供：
 * - MockMvc 用于模拟 HTTP 请求
 * - 预先创建的测试用户（setup 中自动创建）
 * - 带认证的请求辅助方法
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseApiControllerTest {

    @Resource
    protected MockMvc mockMvc;

    @Resource
    protected IUserService userService;

    @Resource
    protected ITopicService topicService;

    @Resource
    protected ICommentService commentService;

    @Resource
    protected ICollectService collectService;

    @Resource
    protected INotificationService notificationService;

    /** 测试用户的 token（API 认证用） */
    protected String userToken;

    /** 测试用户的 ID */
    protected Integer userId;

    /** Mock HttpSession（页面认证用） */
    protected MockHttpSession session;

    @Before
    public void setupBaseUser() {
        // 每个测试方法执行前创建独立的测试用户，确保数据隔离
        String username = "api_test_" + System.nanoTime();
        User user = userService.addUser(
                username,
                "TestPass12345678",
                null,
                username + "@test.com",
                "API测试用户",
                null,
                false
        );
        userToken = user.getToken();
        userId = user.getId();
        session = new MockHttpSession();
        session.setAttribute("_user", user);
    }

    // ========== 辅助方法 ==========

    /**
     * 对请求添加 API Token 认证头
     */
    protected MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.header("token", userToken);
    }

    /**
     * 对请求添加 Session 认证（模拟页面登录状态）
     */
    protected MockHttpServletRequestBuilder withSession(MockHttpServletRequestBuilder requestBuilder) {
        return requestBuilder.session(session);
    }

    /**
     * 执行 GET 请求并添加认证头
     */
    protected ResultActions performGetWithAuth(String urlTemplate, Object... uriVars) throws Exception {
        return mockMvc.perform(withAuth(
                MockMvcRequestBuilders.get(urlTemplate, uriVars)
                        .accept(MediaType.APPLICATION_JSON))
        );
    }

    /**
     * 执行 POST 请求并添加认证头（JSON body）
     */
    protected ResultActions performPostWithAuth(String urlTemplate, String jsonContent) throws Exception {
        return mockMvc.perform(withAuth(
                MockMvcRequestBuilders.post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .accept(MediaType.APPLICATION_JSON))
        );
    }

    /**
     * 断言接口返回成功（code=200）
     */
    protected static ResultMatcher isOk() {
        return jsonPath("$.code").value(200);
    }

    /**
     * 断言接口返回错误（code=201）
     */
    protected static ResultMatcher isError() {
        return jsonPath("$.code").value(201);
    }
}
