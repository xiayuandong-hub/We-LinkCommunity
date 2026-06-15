package co.yiiu.welink.util;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * StringUtil 工具类单元测试
 */
public class StringUtilTest {

    // ========== check() 方法测试 ==========

    @Test
    public void testCheckWithValidUsername() {
        assertTrue(StringUtil.check("testuser", StringUtil.USERNAMEREGEX));
    }

    @Test
    public void testCheckWithShortUsername() {
        assertFalse(StringUtil.check("a", StringUtil.USERNAMEREGEX));
    }

    @Test
    public void testCheckWithInvalidUsernameContainingSpecialChars() {
        assertFalse(StringUtil.check("user@name!", StringUtil.USERNAMEREGEX));
    }

    @Test
    public void testCheckWithValidPassword() {
        assertTrue(StringUtil.check("Abcdefgh12345678", StringUtil.PASSWORDREGEX));
    }

    @Test
    public void testCheckWithPasswordTooShort() {
        assertFalse(StringUtil.check("Abc123", StringUtil.PASSWORDREGEX));
    }

    @Test
    public void testCheckWithPasswordMissingUpperCase() {
        assertFalse(StringUtil.check("abcdefgh12345678", StringUtil.PASSWORDREGEX));
    }

    @Test
    public void testCheckWithPasswordMissingLowerCase() {
        assertFalse(StringUtil.check("ABCDEFGH12345678", StringUtil.PASSWORDREGEX));
    }

    @Test
    public void testCheckWithPasswordMissingDigit() {
        assertFalse(StringUtil.check("Abcdefghijklmnop", StringUtil.PASSWORDREGEX));
    }

    @Test
    public void testCheckWithValidEmail() {
        assertTrue(StringUtil.check("test@example.com", StringUtil.EMAILREGEX));
    }

    @Test
    public void testCheckWithInvalidEmail() {
        assertFalse(StringUtil.check("not-an-email", StringUtil.EMAILREGEX));
    }

    @Test
    public void testCheckWithValidMobile() {
        assertTrue(StringUtil.check("13800138000", StringUtil.MOBILEREGEX));
    }

    @Test
    public void testCheckWithInvalidMobile() {
        assertFalse(StringUtil.check("123", StringUtil.MOBILEREGEX));
    }

    @Test
    public void testCheckWithEmptyString() {
        assertFalse(StringUtil.check("", StringUtil.USERNAMEREGEX));
    }

    @Test
    public void testCheckWithNullString() {
        assertFalse(StringUtil.check(null, StringUtil.USERNAMEREGEX));
    }

    // ========== randomString() 方法测试 ==========

    @Test
    public void testRandomStringLength() {
        String result = StringUtil.randomString(10);
        assertEquals(10, result.length());
    }

    @Test
    public void testRandomStringContainsOnlyValidChars() {
        String result = StringUtil.randomString(100);
        assertTrue(result.matches("[a-z0-9]+"));
    }

    @Test
    public void testRandomStringDifferentOutputs() {
        String s1 = StringUtil.randomString(20);
        String s2 = StringUtil.randomString(20);
        assertNotEquals(s1, s2);
    }

    @Test
    public void testRandomStringZeroLength() {
        String result = StringUtil.randomString(0);
        assertEquals(0, result.length());
    }

    // ========== randomNumber() 方法测试 ==========

    @Test
    public void testRandomNumberLength() {
        String result = StringUtil.randomNumber(6);
        assertEquals(6, result.length());
    }

    @Test
    public void testRandomNumberContainsOnlyDigits() {
        String result = StringUtil.randomNumber(50);
        assertTrue(result.matches("[0-9]+"));
    }

    // ========== uuid() 方法测试 ==========

    @Test
    public void testUuidFormat() {
        String uuid = StringUtil.uuid();
        assertNotNull(uuid);
        // UUID 标准格式: 8-4-4-4-12
        assertTrue(uuid.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}"));
    }

    @Test
    public void testUuidUniqueness() {
        String uuid1 = StringUtil.uuid();
        String uuid2 = StringUtil.uuid();
        assertNotEquals(uuid1, uuid2);
    }

    // ========== isUUID() 方法测试 ==========

    @Test
    public void testIsUUIDWithValidUUID() {
        assertTrue(StringUtil.isUUID("550e8400-e29b-41d4-a716-446655440000"));
    }

    @Test
    public void testIsUUIDWithInvalidString() {
        assertFalse(StringUtil.isUUID("not-a-uuid"));
    }

    @Test
    public void testIsUUIDWithEmptyString() {
        assertFalse(StringUtil.isUUID(""));
    }

    @Test
    public void testIsUUIDWithNull() {
        assertFalse(StringUtil.isUUID(null));
    }

    // ========== formatParams() 方法测试 ==========

    @Test
    public void testFormatParamsNormal() {
        Map<String, Object> params = StringUtil.formatParams("a=1&b=2&c=3");
        assertNotNull(params);
        assertEquals("1", params.get("a"));
        assertEquals("2", params.get("b"));
        assertEquals("3", params.get("c"));
    }

    @Test
    public void testFormatParamsWithEmptyString() {
        assertNull(StringUtil.formatParams(""));
    }

    @Test
    public void testFormatParamsWithNull() {
        assertNull(StringUtil.formatParams(null));
    }

    @Test
    public void testFormatParamsWithSingleParam() {
        Map<String, Object> params = StringUtil.formatParams("key=value");
        assertNotNull(params);
        assertEquals("value", params.get("key"));
    }

    // ========== fetchAtUser() 方法测试 ==========

    @Test
    public void testFetchAtUserWithMention() {
        List<String> users = StringUtil.fetchAtUser("hello @user1, check this @user2");
        assertEquals(2, users.size());
        assertTrue(users.contains("@user1"));
        assertTrue(users.contains("@user2"));
    }

    @Test
    public void testFetchAtUserWithoutMention() {
        List<String> users = StringUtil.fetchAtUser("no mentions here");
        assertTrue(users.isEmpty());
    }

    @Test
    public void testFetchAtUserWithNullContent() {
        List<String> users = StringUtil.fetchAtUser(null);
        assertTrue(users.isEmpty());
    }

    @Test
    public void testFetchAtUserWithEmptyContent() {
        List<String> users = StringUtil.fetchAtUser("");
        assertTrue(users.isEmpty());
    }

    @Test
    public void testFetchAtUserIgnoresCodeBlock() {
        List<String> users = StringUtil.fetchAtUser("```\n@user_in_code\n``` text @valid_user");
        assertEquals(1, users.size());
        assertEquals("@valid_user", users.get(0));
    }

    // ========== removeEmpty() 方法测试 ==========

    @Test
    public void testRemoveEmptyWithMixedArray() {
        Set<String> result = StringUtil.removeEmpty(new String[]{"a", "", "b", null, "c"});
        assertEquals(3, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
    }

    @Test
    public void testRemoveEmptyWithNullArray() {
        Set<String> result = StringUtil.removeEmpty(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testRemoveEmptyWithEmptyArray() {
        Set<String> result = StringUtil.removeEmpty(new String[]{});
        assertTrue(result.isEmpty());
    }

    @Test
    public void testRemoveEmptyRemovesDuplicates() {
        Set<String> result = StringUtil.removeEmpty(new String[]{"a", "a", "b"});
        assertEquals(2, result.size());
    }

    // ========== string2Unicode() 方法测试 ==========

    @Test
    public void testString2UnicodeWithChinese() {
        String unicode = StringUtil.string2Unicode("中文");
        assertNotNull(unicode);
        assertTrue(unicode.contains("\\u"));
    }

    @Test
    public void testString2UnicodeWithAscii() {
        String result = StringUtil.string2Unicode("abc");
        assertEquals("abc", result);
    }

    @Test
    public void testString2UnicodeWithNull() {
        assertNull(StringUtil.string2Unicode(null));
    }

    @Test
    public void testString2UnicodeWithEmpty() {
        assertNull(StringUtil.string2Unicode(""));
    }

    // ========== removeSpecialChar() 方法测试 ==========

    @Test
    public void testRemoveSpecialCharWithSpaces() {
        String result = StringUtil.removeSpecialChar("hello   world");
        assertEquals("helloworld", result);
    }

    @Test
    public void testRemoveSpecialCharWithPunctuation() {
        String result = StringUtil.removeSpecialChar("hello, world!");
        assertEquals("helloworld", result);
    }

    @Test
    public void testRemoveSpecialCharWithNormalText() {
        String result = StringUtil.removeSpecialChar("abc123");
        assertEquals("abc123", result);
    }
}
