package co.yiiu.welink;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 应用上下文启动测试
 * 验证 Spring Boot 应用能成功启动，所有 Bean 正确加载
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class WeLinkApplicationTests {

    @Test
    public void contextLoads() {
        // 只需验证应用上下文能成功启动即可
    }
}
