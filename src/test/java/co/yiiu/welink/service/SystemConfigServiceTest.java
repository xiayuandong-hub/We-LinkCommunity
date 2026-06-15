package co.yiiu.welink.service;

import co.yiiu.welink.config.BaseServiceTest;
import co.yiiu.welink.model.SystemConfig;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * SystemConfigService 单元测试
 */
public class SystemConfigServiceTest extends BaseServiceTest {

    @Test
    public void testSelectAllConfig() {
        Map<String, String> config = systemConfigService.selectAllConfig();
        assertNotNull(config);
        // 基础配置项应该存在
        assertTrue(config.containsKey("base_url"));
        assertTrue(config.containsKey("name"));
        assertTrue(config.containsKey("page_size"));
        assertTrue(config.containsKey("create_topic_score"));
        assertTrue(config.containsKey("create_comment_score"));

        // 验证默认值
        assertEquals("http://localhost:8080", config.get("base_url"));
        assertEquals("We-Link测试社区", config.get("name"));
        assertEquals("20", config.get("page_size"));
        assertEquals("10", config.get("create_topic_score"));
        assertEquals("5", config.get("create_comment_score"));
        assertEquals("3", config.get("up_topic_score"));
        assertEquals("3", config.get("up_comment_score"));
        assertEquals("5", config.get("create_comment_score"));
        assertEquals("10", config.get("delete_topic_score"));
        assertEquals("5", config.get("delete_comment_score"));
    }

    @Test
    public void testSelectAll() {
        Map<String, Object> configGroups = systemConfigService.selectAll();
        assertNotNull(configGroups);
        assertFalse(configGroups.isEmpty());
        // selectAll 以分组形式返回，key 为组描述
        assertTrue(configGroups.containsKey("基础配置"));
    }

    @Test
    public void testSelectByKey() {
        SystemConfig config = systemConfigService.selectByKey("base_url");
        assertNotNull(config);
        assertEquals("base_url", config.getKey());
        assertEquals("http://localhost:8080", config.getValue());
    }

    @Test
    public void testSelectByKeyNotFound() {
        SystemConfig config = systemConfigService.selectByKey("nonexistent_key");
        assertNull(config);
    }

    @Test
    public void testSelectAllConfigWithoutPassword() {
        Map<String, String> config = systemConfigService.selectAllConfigWithoutPassword();
        assertNotNull(config);
        // 不包含密码类型配置
        SystemConfig mailPassword = systemConfigService.selectByKey("mail_password");
        if (mailPassword != null) {
            assertFalse(config.containsKey("mail_password"));
        }
    }
}
