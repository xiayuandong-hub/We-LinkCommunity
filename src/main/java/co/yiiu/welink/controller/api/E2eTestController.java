package co.yiiu.welink.controller.api;

import co.yiiu.welink.exception.ApiAssert;
import co.yiiu.welink.model.User;
import co.yiiu.welink.service.IUserService;
import co.yiiu.welink.util.Result;
import co.yiiu.welink.util.StringUtil;
import co.yiiu.welink.util.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * E2E 测试专用控制器 - 仅在 e2e profile 下加载
 * <p>
 * 提供验证码绕过和测试数据初始化功能。
 */
@RestController
@RequestMapping("/api/e2e")
@Profile("e2e")
public class E2eTestController {

    @Resource
    private IUserService userService;

    /**
     * 验证码绕过：直接设置 session 验证码
     */
    @GetMapping("/captcha")
    public String captcha(HttpSession session) {
        session.setAttribute("_captcha", "test");
        return "test";
    }

    /**
     * E2E 注册（绕过验证码）
     */
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email");

        ApiAssert.notEmpty(username, "请输入用户名");
        ApiAssert.notEmpty(password, "请输入密码");
        ApiAssert.isTrue(StringUtil.check(password, StringUtil.PASSWORDREGEX), "密码：至少一个大写字母、至少一个小写字母、至少一个数字、至少16位");
        ApiAssert.notEmpty(email, "请输入邮箱");
        ApiAssert.isTrue(StringUtil.check(username, StringUtil.USERNAMEREGEX), "用户名只能为a-z,A-Z,0-9组合且2-16位");
        ApiAssert.isTrue(StringUtil.check(email, StringUtil.EMAILREGEX), "请输入正确的邮箱地址");

        User user = userService.selectByUsername(username);
        ApiAssert.isNull(user, "用户名已存在");
        User emailUser = userService.selectByEmail(email);
        ApiAssert.isNull(emailUser, "这个邮箱已经被注册过了，请更换一个邮箱");

        user = userService.addUser(username, password, null, email, null, null, false);
        return doUserStorage(session, user);
    }

    /**
     * E2E 登录（绕过验证码）
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.get("username");
        String password = body.get("password");

        ApiAssert.notEmpty(username, "请输入用户名");
        ApiAssert.notEmpty(password, "请输入密码");
        User user = userService.selectByUsername(username);
        ApiAssert.notNull(user, "用户不存在");
        ApiAssert.isTrue(new BCryptPasswordEncoder().matches(password, user.getPassword()), "用户名或密码不正确");
        return doUserStorage(session, user);
    }

    private Result doUserStorage(HttpSession session, User user) {
        session.setAttribute("_user", user);
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("token", user.getToken());
        Result result = new Result();
        result.setCode(200);
        result.setDescription("SUCCESS");
        result.setDetail(map);
        return result;
    }
}
