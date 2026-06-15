package co.yiiu.welink.service;

import co.yiiu.welink.config.BaseServiceTest;
import co.yiiu.welink.model.AdminUser;
import co.yiiu.welink.util.bcrypt.BCryptPasswordEncoder;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * AdminUserService 单元测试
 */
public class AdminUserServiceTest extends BaseServiceTest {

    @Test
    public void testInsert() {
        AdminUser admin = new AdminUser();
        admin.setUsername("newadmin");
        admin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
        admin.setInTime(new Date());
        admin.setRoleId(1);
        adminUserService.insert(admin);

        assertNotNull(admin.getId());
    }

    @Test
    public void testSelectByUsername() {
        // admin 用户已在 schema.sql 种子数据中创建
        AdminUser found = adminUserService.selectByUsername("admin");
        assertNotNull(found);
        assertEquals("admin", found.getUsername());
    }

    @Test
    public void testSelectByUsernameNotFound() {
        AdminUser found = adminUserService.selectByUsername("nonexistent_admin");
        assertNull(found);
    }

    @Test
    public void testSelectById() {
        AdminUser admin = adminUserService.selectByUsername("admin");
        assertNotNull(admin);

        AdminUser found = adminUserService.selectById(admin.getId());
        assertNotNull(found);
        assertEquals(admin.getId(), found.getId());
    }

    @Test
    public void testSelectAll() {
        List<Map<String, Object>> all = adminUserService.selectAll();
        assertNotNull(all);
        assertFalse(all.isEmpty());
    }

    @Test
    public void testUpdate() {
        AdminUser admin = adminUserService.selectByUsername("admin");
        assertNotNull(admin);

        // 不改密码，更新角色
        admin.setRoleId(2);
        adminUserService.update(admin);

        AdminUser updated = adminUserService.selectById(admin.getId());
        assertEquals(Integer.valueOf(2), updated.getRoleId());
    }

    @Test
    public void testSelectByRoleId() {
        // admin 用户角色为 1（超级管理员）
        List<AdminUser> admins = adminUserService.selectByRoleId(1);
        assertNotNull(admins);
        assertFalse(admins.isEmpty());
    }

    @Test
    public void testSelectByRoleIdNotFound() {
        List<AdminUser> admins = adminUserService.selectByRoleId(999);
        assertNotNull(admins);
        assertTrue(admins.isEmpty());
    }

    @Test
    public void testDelete() {
        AdminUser admin = new AdminUser();
        admin.setUsername("tempadmin");
        admin.setPassword(new BCryptPasswordEncoder().encode("temp123"));
        admin.setInTime(new Date());
        admin.setRoleId(1);
        adminUserService.insert(admin);

        Integer id = admin.getId();
        adminUserService.delete(id);

        AdminUser deleted = adminUserService.selectById(id);
        assertNull(deleted);
    }
}
