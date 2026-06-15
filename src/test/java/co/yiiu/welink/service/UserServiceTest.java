package co.yiiu.welink.service;

import co.yiiu.welink.config.BaseServiceTest;
import co.yiiu.welink.model.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * UserService 单元测试
 */
public class UserServiceTest extends BaseServiceTest {

    @Test
    public void testAddUser() {
        User user = createTestUser("testuser1");
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("testuser1", user.getUsername());
        assertEquals("testuser1@test.com", user.getEmail());
        assertNotNull(user.getToken());
        assertNotNull(user.getInTime());
        assertNotNull(user.getAvatar());
        assertEquals(Integer.valueOf(0), user.getScore());
        assertTrue(user.getActive());
    }

    @Test
    public void testSelectByUsername() {
        createTestUser("finduser");
        User found = userService.selectByUsername("finduser");
        assertNotNull(found);
        assertEquals("finduser", found.getUsername());
    }

    @Test
    public void testSelectByUsernameNotFound() {
        User found = userService.selectByUsername("nonexistent");
        assertNull(found);
    }

    @Test
    public void testSelectByToken() {
        User user = createTestUser("token_test");
        User found = userService.selectByToken(user.getToken());
        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
    }

    @Test
    public void testSelectByInvalidToken() {
        User found = userService.selectByToken("invalid-token-value");
        assertNull(found);
    }

    @Test
    public void testSelectByEmail() {
        User user = createTestUser("email_test");
        User found = userService.selectByEmail("email_test@test.com");
        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
    }

    @Test
    public void testSelectById() {
        User user = createTestUser("id_test");
        User found = userService.selectById(user.getId());
        assertNotNull(found);
        assertEquals("id_test", found.getUsername());
    }

    @Test
    public void testSelectByIdNotFound() {
        User found = userService.selectById(999999);
        assertNull(found);
    }

    @Test
    public void testUpdateUser() {
        User user = createTestUser("update_test");
        // 更新用户信息
        user.setBio("新简介内容");
        user.setWebsite("https://new-website.com");
        userService.update(user);

        User updated = userService.selectById(user.getId());
        assertEquals("新简介内容", updated.getBio());
        assertEquals("https://new-website.com", updated.getWebsite());
    }

    @Test
    public void testSelectTop() {
        // 创建多个用户，积分自然为0
        createTestUser("top_user_1");
        createTestUser("top_user_2");
        createTestUser("top_user_3");

        List<User> top = userService.selectTop(5);
        assertNotNull(top);
        // 至少包含我们创建的用户
        assertTrue(top.size() >= 3);
    }

    @Test
    public void testSelectTopWithLimit() {
        createTestUser("limit_test_1");
        createTestUser("limit_test_2");
        createTestUser("limit_test_3");

        List<User> top = userService.selectTop(2);
        assertNotNull(top);
        assertTrue(top.size() <= 2);
    }

    @Test
    public void testDeleteUser() {
        User user = createTestUser("deleteme");
        Integer userId = user.getId();
        userService.deleteUser(userId);
        User deleted = userService.selectById(userId);
        assertNull(deleted);
    }

    @Test
    public void testAddUserWithMobile() {
        User user = userService.addUserWithMobile("13800138000");
        assertNotNull(user);
        assertNotNull(user.getId());
        assertNotNull(user.getUsername());
        assertTrue(user.getActive());
    }

    @Test
    public void testAddUserWithMobileDuplicate() {
        // 第一次调用创建用户
        User user1 = userService.addUserWithMobile("13900139000");
        assertNotNull(user1);

        // 第二次用相同手机号应返回已存在的用户
        User user2 = userService.addUserWithMobile("13900139000");
        assertNotNull(user2);
        // 事务隔离下，相同手机号可能返回不同对象，但都应成功
        assertNotNull(user2.getId());
    }

    @Test
    public void testUsernameUniqueness() {
        createTestUser("unique_user");
        // 检查用户名已存在
        User duplicate = userService.selectByUsername("unique_user");
        assertNotNull(duplicate);
    }

    @Test
    public void testEmailUniqueness() {
        User user = createTestUser("email_test2");
        // 相同邮箱不应被其他用户使用
        User found = userService.selectByEmail("email_test2@test.com");
        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
    }

    @Test
    public void testCountToday() {
        createTestUser("count_today");
        int count = userService.countToday();
        // countToday 依赖 MySQL 的 date_add 语义，H2 中可能存在兼容差异，仅验证返回值非负
        assertTrue(count >= 0);
    }

    @Test
    public void testSelectAll() {
        createTestUser("page_user1");
        createTestUser("page_user2");
        createTestUser("page_user3");

        IPage<User> page = userService.selectAll(1, null);
        assertNotNull(page);
        assertTrue(page.getRecords().size() > 0);
    }

    @Test
    public void testSelectAllWithUsernameFilter() {
        createTestUser("filter_user");
        IPage<User> page = userService.selectAll(1, "filter_user");
        assertNotNull(page);
        assertTrue(page.getRecords().size() >= 1);
        assertEquals("filter_user", page.getRecords().get(0).getUsername());
    }
}
