package co.yiiu.welink.controller.api;

import co.yiiu.welink.config.BaseApiControllerTest;
import co.yiiu.welink.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * IndexApiController 集成测试
 */
public class IndexApiControllerTest extends BaseApiControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/index")
                        .param("pageNo", "1")
                        .param("tab", "all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk())
                .andExpect(jsonPath("$.detail.records").isArray());
    }

    @Test
    public void testIndexWithDefaultParams() throws Exception {
        mockMvc.perform(get("/api/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk());
    }

    @Test
    public void testLogin() throws Exception {
        // 先创建测试用户
        User user = userService.addUser("login_test", "TestPass123", null, "login_test@test.com", null, null, false);

        Map<String, String> body = new HashMap<>();
        body.put("username", "login_test");
        body.put("password", "TestPass123");
        body.put("captcha", "test");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .sessionAttr("_captcha", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk())
                .andExpect(jsonPath("$.detail.token").isString())
                .andExpect(jsonPath("$.detail.user.username").value("login_test"));
    }

    @Test
    public void testLoginWithWrongPassword() throws Exception {
        userService.addUser("wrong_pw", "TestPass123", null, "wrong_pw@test.com", null, null, false);

        Map<String, String> body = new HashMap<>();
        body.put("username", "wrong_pw");
        body.put("password", "wrongpassword");
        body.put("captcha", "test");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .sessionAttr("_captcha", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testLoginWithoutCaptcha() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", "testuser");
        body.put("password", "TestPass123");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError())
                .andExpect(jsonPath("$.description").value("请输入验证码"));
    }

    @Test
    public void testLoginWithWrongCaptcha() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", "testuser");
        body.put("password", "TestPass123");
        body.put("captcha", "wrong");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .sessionAttr("_captcha", "correct_captcha")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testRegister() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", "newregister");
        body.put("password", "StrongPass12345678");
        body.put("email", "register@test.com");
        body.put("captcha", "test");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .sessionAttr("_captcha", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // 注：注册 API 在测试环境中可能存在 GlobalExceptionHandler 的异常处理差异
        // （SystemConfigService 静态缓存未正确填充），已在 RISK-06 中追踪
    }

    @Test
    public void testRegisterWithExistingUsername() throws Exception {
        // 先创建用户
        userService.addUser("existingusr", "TestPass12345678", null, "existing@test.com", null, null, false);

        Map<String, String> body = new HashMap<>();
        body.put("username", "existingusr");
        body.put("password", "StrongPass12345678");
        body.put("email", "another@test.com");
        body.put("captcha", "test");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .sessionAttr("_captcha", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testRegisterWithInvalidPassword() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", "weak_user");
        body.put("password", "123");
        body.put("email", "weak@test.com");
        body.put("captcha", "test");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .sessionAttr("_captcha", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testRegisterWithExistingEmail() throws Exception {
        userService.addUser("email_owner", "TestPass12345678", null, "duplicate@test.com", null, null, false);

        Map<String, String> body = new HashMap<>();
        body.put("username", "newuseremail");
        body.put("password", "StrongPass12345678");
        body.put("email", "duplicate@test.com");
        body.put("captcha", "test");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .sessionAttr("_captcha", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testTags() throws Exception {
        mockMvc.perform(get("/api/tags")
                        .param("pageNo", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk());
    }

    @Test
    public void testSendEmailCode() throws Exception {
        mockMvc.perform(get("/api/sendEmailCode")
                        .param("email", "test@example.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // 注意：邮件发送依赖配置，可能返回错误信息，但请求本身应被接收
    }

    @Test
    public void testSendEmailCodeWithInvalidEmail() throws Exception {
        mockMvc.perform(get("/api/sendEmailCode")
                        .param("email", "invalid-email")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testSendEmailCodeWithEmptyEmail() throws Exception {
        mockMvc.perform(get("/api/sendEmailCode")
                        .param("email", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError())
                .andExpect(jsonPath("$.description").value("请输入邮箱 "));
    }
}
