package co.yiiu.welink.util;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * SensitiveWordUtil 工具类单元测试
 */
public class SensitiveWordUtilTest {

    @Before
    public void setUp() {
        // 初始化敏感词库
        Set<String> sensitiveWords = new HashSet<>();
        sensitiveWords.add("法轮功");
        sensitiveWords.add("赌博");
        sensitiveWords.add("色情");
        sensitiveWords.add("毒品");
        sensitiveWords.add("政治");
        SensitiveWordUtil.init(sensitiveWords);
    }

    @Test
    public void testContainsWithSensitiveWord() {
        assertTrue(SensitiveWordUtil.contains("这是一段包含法轮功的文字"));
    }

    @Test
    public void testContainsWithoutSensitiveWord() {
        assertFalse(SensitiveWordUtil.contains("这是一段正常的文字"));
    }

    @Test
    public void testContainsWithMinMatchType() {
        // 添加一个子集敏感词 "色情" 在 "色情服务" 中
        // 最小匹配应匹配到 "色情"
        assertTrue(SensitiveWordUtil.contains("提供色情服务", SensitiveWordUtil.MinMatchType));
    }

    @Test
    public void testContainsWithEmptyString() {
        assertFalse(SensitiveWordUtil.contains(""));
    }

    @Test
    public void testGetSensitiveWord() {
        Set<String> found = SensitiveWordUtil.getSensitiveWord("打击赌博行为");
        assertTrue(found.contains("赌博"));
        assertEquals(1, found.size());
    }

    @Test
    public void testGetSensitiveWordMultiple() {
        Set<String> found = SensitiveWordUtil.getSensitiveWord("赌博和色情都是违法的");
        assertEquals(2, found.size());
        assertTrue(found.contains("赌博"));
        assertTrue(found.contains("色情"));
    }

    @Test
    public void testGetSensitiveWordWithNoMatch() {
        Set<String> found = SensitiveWordUtil.getSensitiveWord("今天天气真好");
        assertTrue(found.isEmpty());
    }

    @Test
    public void testReplaceSensitiveWordWithChar() {
        String result = SensitiveWordUtil.replaceSensitiveWord("打击赌博行为", '*', SensitiveWordUtil.MinMatchType);
        assertEquals("打击**行为", result);
    }

    @Test
    public void testReplaceSensitiveWordWithString() {
        String result = SensitiveWordUtil.replaceSensitiveWord("传播色情信息", "[过滤]", SensitiveWordUtil.MinMatchType);
        assertEquals("传播[过滤]信息", result);
    }

    @Test
    public void testReplaceSensitiveWordReturnsNullForEmptyInput() {
        String result = SensitiveWordUtil.replaceSensitiveWord("", "[filter]", SensitiveWordUtil.MinMatchType);
        assertNull(result);
    }

    @Test
    public void testReplaceSensitiveWordDefaultMatchType() {
        String result = SensitiveWordUtil.replaceSensitiveWord("涉及毒品交易", '*');
        assertEquals("涉及**交易", result);
    }

    @Test
    public void testGetSensitiveWordDefaultMatchType() {
        Set<String> result = SensitiveWordUtil.getSensitiveWord("涉及毒品和赌博");
        assertEquals(2, result.size());
    }
}
