package co.yiiu.welink.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * MD5Util 工具类单元测试
 */
public class MD5UtilTest {

    @Test
    public void testGetMD5String() {
        String md5 = MD5Util.getMD5String("hello");
        assertNotNull(md5);
        // MD5 总是32位十六进制字符串
        assertEquals(32, md5.length());
    }

    @Test
    public void testGetMD5Deterministic() {
        String md5_1 = MD5Util.getMD5String("test123");
        String md5_2 = MD5Util.getMD5String("test123");
        assertEquals(md5_1, md5_2);
    }

    @Test
    public void testGetMD5DifferentInputs() {
        String md5_1 = MD5Util.getMD5String("input1");
        String md5_2 = MD5Util.getMD5String("input2");
        assertNotEquals(md5_1, md5_2);
    }

    @Test
    public void testGetMD5StringWithEmptyString() {
        String md5 = MD5Util.getMD5String("");
        assertNotNull(md5);
        assertEquals(32, md5.length());
    }

    @Test
    public void testGetMD5StringWithSalt() {
        String salted = MD5Util.getMD5StringWithSalt("password123", "randomSalt");
        assertNotNull(salted);
        assertEquals(32, salted.length());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMD5StringWithSaltNullPassword() {
        MD5Util.getMD5StringWithSalt(null, "salt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMD5StringWithSaltEmptySalt() {
        MD5Util.getMD5StringWithSalt("password", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetMD5StringWithSaltInvalidSaltContainingBraces() {
        MD5Util.getMD5StringWithSalt("password", "salt{with}brace");
    }

    @Test
    public void testHexdigest() {
        byte[] input = "data".getBytes();
        String result = MD5Util.hexdigest(input);
        assertNotNull(result);
        assertEquals(32, result.length());
    }

    @Test
    public void testHexdigestWithNullInput() {
        assertNull(MD5Util.hexdigest(null));
    }

    @Test
    public void testKnownMD5Value() {
        // "abc" 的 MD5 是已知值
        String md5 = MD5Util.getMD5String("abc");
        assertEquals("900150983cd24fb0d6963f7d28e17f72", md5);
    }
}
