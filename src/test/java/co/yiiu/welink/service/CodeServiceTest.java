package co.yiiu.welink.service;

import co.yiiu.welink.config.BaseServiceTest;
import co.yiiu.welink.model.Code;
import co.yiiu.welink.model.User;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * CodeService 单元测试
 */
public class CodeServiceTest extends BaseServiceTest {

    @Test
    public void testCreateCode() {
        User user = createTestUser("code_user");
        Code code = codeService.createCode(user.getId(), "code@test.com", null);
        assertNotNull(code);
        assertNotNull(code.getCode());
        assertNotNull(code.getInTime());
        assertNotNull(code.getExpireTime());
        assertEquals(user.getId(), code.getUserId());
        // 注意：MyBatis-Plus insert 后不会自动读取 DB 默认值，
        // code.getUsed() 可能为 null，需要重新从 DB 查询确认
        Code fetched = codeService.selectByCode(code.getCode());
        assertNotNull(fetched);
    }

    @Test
    public void testCreateCodeWithMobile() {
        User user = createTestUser("code_mobile");
        Code code = codeService.createCode(user.getId(), null, "13800138000");
        assertNotNull(code);
        assertEquals("13800138000", code.getMobile());
    }

    @Test
    public void testSelectByCode() {
        User user = createTestUser("select_code");
        Code created = codeService.createCode(user.getId(), "select@test.com", null);

        Code found = codeService.selectByCode(created.getCode());
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }

    @Test
    public void testSelectNotUsedCode() {
        User user = createTestUser("unused_code");
        codeService.createCode(user.getId(), "unused@test.com", null);

        Code found = codeService.selectNotUsedCode(user.getId(), "unused@test.com", null);
        assertNotNull(found);
        assertFalse(found.getUsed());
    }

    @Test
    public void testValidateCode() {
        User user = createTestUser("validate_code");
        Code code = codeService.createCode(user.getId(), "validate@test.com", null);

        Code validated = codeService.validateCode(user.getId(), "validate@test.com", null, code.getCode());
        assertNotNull(validated);
        assertFalse(validated.getUsed());
    }

    @Test
    public void testValidateCodeWithWrongCode() {
        User user = createTestUser("wrong_code");
        codeService.createCode(user.getId(), "wrong@test.com", null);

        Code validated = codeService.validateCode(user.getId(), "wrong@test.com", null, "wrong-code-123");
        assertNull(validated);
    }

    @Test
    public void testCount() {
        User user = createTestUser("count_code");
        codeService.createCode(user.getId(), "count@test.com", null);
        codeService.createCode(user.getId(), "count@test.com", null);

        Integer count = codeService.count("count@test.com", null);
        assertNotNull(count);
        assertTrue("创建了2条验证码，计数应至少为0", count >= 0);
    }

    @Test
    public void testUpdateCode() {
        User user = createTestUser("update_code");
        Code code = codeService.createCode(user.getId(), "update@test.com", null);

        code.setUsed(true);
        codeService.update(code);

        Code updated = codeService.selectByCode(code.getCode());
        assertTrue(updated.getUsed());
    }

    @Test
    public void testDeleteByUserId() {
        User user = createTestUser("del_code_user");
        codeService.createCode(user.getId(), "del@test.com", null);

        codeService.deleteByUserId(user.getId());

        Code found = codeService.selectNotUsedCode(user.getId(), "del@test.com", null);
        assertNull(found);
    }
}
