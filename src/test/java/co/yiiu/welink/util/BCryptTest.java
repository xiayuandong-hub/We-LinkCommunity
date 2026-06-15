package co.yiiu.welink.util;

import co.yiiu.welink.util.bcrypt.BCrypt;
import co.yiiu.welink.util.bcrypt.BCryptPasswordEncoder;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * BCrypt 密码加密测试
 */
public class BCryptTest {

    @Test
    public void testBCryptPasswordEncoderEncode() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("password123");
        assertNotNull(encoded);
        // BCrypt 输出以 $2a$ 或 $2b$ 开头
        assertTrue(encoded.startsWith("$2a$") || encoded.startsWith("$2b$"));
    }

    @Test
    public void testBCryptPasswordEncoderMatches() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("password123");
        assertTrue(encoder.matches("password123", encoded));
    }

    @Test
    public void testBCryptPasswordEncoderWrongPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("correctPassword");
        assertFalse(encoder.matches("wrongPassword", encoded));
    }

    @Test
    public void testBCryptSamePasswordDifferentHashEachTime() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash1 = encoder.encode("samePassword");
        String hash2 = encoder.encode("samePassword");
        // 每次的盐不同，hash 也不同
        assertNotEquals(hash1, hash2);
    }

    @Test
    public void testBCryptCheckpw() {
        String password = "myPassword";
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        assertTrue(BCrypt.checkpw(password, hashed));
    }

    @Test
    public void testBCryptGensaltWithLogRounds() {
        String salt = BCrypt.gensalt(10);
        assertNotNull(salt);
        assertTrue(salt.startsWith("$2a$10$") || salt.startsWith("$2b$10$"));
    }

    @Test
    public void testBCryptHashpwWithNullPassword() {
        // Java 中 null + "" 结果为 "null"，所以 BCrypt.hashpw(null, salt) 不会抛异常
        // 而是产生一个基于字符串"null"的 hash
        String hashed = BCrypt.hashpw(null, BCrypt.gensalt());
        assertNotNull(hashed);
        assertTrue(hashed.startsWith("$2a$") || hashed.startsWith("$2b$"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBCryptHashpwWithNullSalt() {
        BCrypt.hashpw("password", null);
    }
}
