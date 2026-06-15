package co.yiiu.welink.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * DateUtil 工具类单元测试
 */
public class DateUtilTest {

    @Test
    public void testFormatDateTime() {
        // 使用一个确定的日期：2024-01-15 14:30:00
        Date date = DateUtil.string2Date("2024-01-15 14:30:00", DateUtil.FORMAT_DATETIME);
        String result = DateUtil.formatDateTime(date);
        assertEquals("2024-01-15 14:30:00", result);
    }

    @Test
    public void testFormatDateTimeWithNull() {
        assertNull(DateUtil.formatDateTime(null));
    }

    @Test
    public void testFormatDate() {
        Date date = DateUtil.string2Date("2024-06-15", DateUtil.FORMAT_DATE);
        String result = DateUtil.formatDate(date);
        assertEquals("2024-06-15", result);
    }

    @Test
    public void testFormatDateTimeWithCustomStyle() {
        Date date = DateUtil.string2Date("2024-01-15 14:30:00", DateUtil.FORMAT_DATETIME);
        String result = DateUtil.formatDateTime(date, "yyyy/MM/dd");
        assertEquals("2024/01/15", result);
    }

    @Test
    public void testString2Date() {
        Date date = DateUtil.string2Date("2024-03-20", DateUtil.FORMAT_DATE);
        assertNotNull(date);
        String formatted = DateUtil.formatDate(date);
        assertEquals("2024-03-20", formatted);
    }

    @Test
    public void testString2DateWithNull() {
        assertNull(DateUtil.string2Date(null, DateUtil.FORMAT_DATE));
    }

    @Test
    public void testString2DateWithEmptyString() {
        assertNull(DateUtil.string2Date("", DateUtil.FORMAT_DATE));
    }

    @Test
    public void testIsExpiredWithPastDate() {
        Date pastDate = DateUtil.getDateBefore(new Date(), 1);
        assertTrue(DateUtil.isExpire(pastDate));
    }

    @Test
    public void testIsExpiredWithFutureDate() {
        Date futureDate = DateUtil.getDateAfter(new Date(), 1);
        assertFalse(DateUtil.isExpire(futureDate));
    }

    @Test
    public void testGetHourAfter() {
        Date now = new Date();
        Date later = DateUtil.getHourAfter(now, 2);
        assertNotNull(later);
        // 2小时后应该在当前时间之后
        assertTrue(later.after(now));
    }

    @Test
    public void testGetHourBefore() {
        Date now = new Date();
        Date earlier = DateUtil.getHourBefore(now, 2);
        assertNotNull(earlier);
        assertTrue(earlier.before(now));
    }

    @Test
    public void testGetDateAfter() {
        Date now = new Date();
        Date future = DateUtil.getDateAfter(now, 5);
        assertNotNull(future);
        assertTrue(future.after(now));
        // 约5天后的时间差（毫秒）应在合理范围
        long diffMs = future.getTime() - now.getTime();
        assertTrue(diffMs > 4 * 24 * 60 * 60 * 1000L);
        assertTrue(diffMs < 6 * 24 * 60 * 60 * 1000L);
    }

    @Test
    public void testGetDateBefore() {
        Date now = new Date();
        Date past = DateUtil.getDateBefore(now, 3);
        assertNotNull(past);
        assertTrue(past.before(now));
    }

    @Test
    public void testGetMinuteAfter() {
        Date now = new Date();
        Date later = DateUtil.getMinuteAfter(now, 30);
        assertNotNull(later);
        assertTrue(later.after(now));
        long diffMs = later.getTime() - now.getTime();
        assertEquals(30 * 60 * 1000L, diffMs, 1000);
    }

    @Test
    public void testGetMinuteBefore() {
        Date now = new Date();
        Date earlier = DateUtil.getMinuteBefore(now, 15);
        assertNotNull(earlier);
        assertTrue(earlier.before(now));
    }
}
