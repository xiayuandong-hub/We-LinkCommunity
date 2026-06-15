package co.yiiu.welink.service;

import co.yiiu.welink.config.BaseServiceTest;
import co.yiiu.welink.model.SensitiveWord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * SensitiveWordService 单元测试
 */
public class SensitiveWordServiceTest extends BaseServiceTest {

    @Test
    public void testSave() {
        SensitiveWord sw = new SensitiveWord();
        sw.setWord("敏感词测试");
        sensitiveWordService.save(sw);

        assertNotNull(sw.getId());
    }

    @Test
    public void testSelectAll() {
        SensitiveWord sw1 = new SensitiveWord();
        sw1.setWord("词1");
        sensitiveWordService.save(sw1);

        SensitiveWord sw2 = new SensitiveWord();
        sw2.setWord("词2");
        sensitiveWordService.save(sw2);

        List<SensitiveWord> list = sensitiveWordService.selectAll();
        assertNotNull(list);
        assertTrue(list.size() >= 2);
    }

    @Test
    public void testSelectByWord() {
        SensitiveWord sw = new SensitiveWord();
        sw.setWord("查找这个词");
        sensitiveWordService.save(sw);

        SensitiveWord found = sensitiveWordService.selectByWord("查找这个词");
        assertNotNull(found);
        assertEquals("查找这个词", found.getWord());
    }

    @Test
    public void testSelectByWordNotFound() {
        SensitiveWord found = sensitiveWordService.selectByWord("不存在的敏感词");
        assertNull(found);
    }

    @Test
    public void testUpdate() {
        SensitiveWord sw = new SensitiveWord();
        sw.setWord("旧词");
        sensitiveWordService.save(sw);

        sw.setWord("新词");
        sensitiveWordService.update(sw);

        SensitiveWord updated = sensitiveWordService.selectByWord("新词");
        assertNotNull(updated);
        assertNull(sensitiveWordService.selectByWord("旧词"));
    }

    @Test
    public void testUpdateWordById() {
        SensitiveWord sw = new SensitiveWord();
        sw.setWord("原词");
        sensitiveWordService.save(sw);

        sensitiveWordService.updateWordById(sw.getId(), "修改后的词");

        // selectAll 后查找更新后的词
        SensitiveWord updated = sensitiveWordService.selectByWord("修改后的词");
        assertNotNull(updated);
        assertEquals("修改后的词", updated.getWord());
    }

    @Test
    public void testDeleteById() {
        SensitiveWord sw = new SensitiveWord();
        sw.setWord("待删除");
        sensitiveWordService.save(sw);

        sensitiveWordService.deleteById(sw.getId());

        assertNull(sensitiveWordService.selectByWord("待删除"));
    }

    @Test
    public void testPage() {
        SensitiveWord sw = new SensitiveWord();
        sw.setWord("分页测试");
        sensitiveWordService.save(sw);

        IPage<SensitiveWord> page = sensitiveWordService.page(1, null);
        assertNotNull(page);
        assertTrue(page.getRecords().size() > 0);
    }

    @Test
    public void testPageWithWordFilter() {
        SensitiveWord sw = new SensitiveWord();
        sw.setWord("过滤词ABCD");
        sensitiveWordService.save(sw);

        IPage<SensitiveWord> page = sensitiveWordService.page(1, "ABCD");
        assertNotNull(page);
        assertTrue(page.getRecords().size() >= 1);
    }
}
