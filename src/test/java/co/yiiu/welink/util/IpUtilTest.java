package co.yiiu.welink.util;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;

/**
 * IpUtil 工具类单元测试
 */
public class IpUtilTest {

    @Test
    public void testGetIpAddrFromXForwardedFor() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("x-forwarded-for", "192.168.1.1");
        assertEquals("192.168.1.1", IpUtil.getIpAddr(request));
    }

    @Test
    public void testGetIpAddrFromProxyClientIP() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Proxy-Client-IP", "10.0.0.1");
        assertEquals("10.0.0.1", IpUtil.getIpAddr(request));
    }

    @Test
    public void testGetIpAddrFromWLProxyClientIP() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("WL-Proxy-Client-IP", "172.16.0.1");
        assertEquals("172.16.0.1", IpUtil.getIpAddr(request));
    }

    @Test
    public void testGetIpAddrFallbackToRemoteAddr() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        assertEquals("127.0.0.1", IpUtil.getIpAddr(request));
    }

    @Test
    public void testGetIpAddrPrecedence() {
        // x-forwarded-for 应优先于其他头
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("x-forwarded-for", "1.1.1.1");
        request.addHeader("Proxy-Client-IP", "2.2.2.2");
        request.setRemoteAddr("3.3.3.3");
        assertEquals("1.1.1.1", IpUtil.getIpAddr(request));
    }

    @Test
    public void testGetIpAddrIgnoreUnknown() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("x-forwarded-for", "unknown");
        request.addHeader("Proxy-Client-IP", "8.8.8.8");
        assertEquals("8.8.8.8", IpUtil.getIpAddr(request));
    }

    @Test
    public void testGetIpAddrWhenAllHeadersUnknown() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("x-forwarded-for", "unknown");
        request.addHeader("Proxy-Client-IP", "unknown");
        request.addHeader("WL-Proxy-Client-IP", "unknown");
        request.setRemoteAddr("10.10.10.10");
        assertEquals("10.10.10.10", IpUtil.getIpAddr(request));
    }

    @Test
    public void testGetIpAddrReturnsIPv6() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("x-forwarded-for", "::1");
        request.setRemoteAddr("fe80::1");
        assertEquals("::1", IpUtil.getIpAddr(request));
    }

    @Test
    public void testGetIpAddrHandlesMultipleIps() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("x-forwarded-for", "192.168.1.1, 10.0.0.1");
        assertEquals("192.168.1.1, 10.0.0.1", IpUtil.getIpAddr(request));
    }

    @Test
    public void testGetIpAddrWithNoHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("0:0:0:0:0:0:0:1");
        assertEquals("0:0:0:0:0:0:0:1", IpUtil.getIpAddr(request));
    }
}
