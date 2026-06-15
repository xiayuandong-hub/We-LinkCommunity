package co.yiiu.welink.service;

import co.yiiu.welink.config.BaseServiceTest;
import co.yiiu.welink.model.Tag;
import co.yiiu.welink.model.Topic;
import co.yiiu.welink.model.User;
import co.yiiu.welink.util.MyPage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * TagService 单元测试
 */
public class TagServiceTest extends BaseServiceTest {

    @Test
    public void testInsertTag() {
        List<Tag> tags = tagService.insertTag("测试标签");
        assertNotNull(tags);
        assertEquals(1, tags.size());
        assertEquals("测试标签", tags.get(0).getName());
    }

    @Test
    public void testInsertMultipleTags() {
        List<Tag> tags = tagService.insertTag("标签A,标签B,标签C");
        assertNotNull(tags);
        assertEquals(3, tags.size());
    }

    @Test
    public void testSelectByName() {
        tagService.insertTag("惟一的标签");
        Tag found = tagService.selectByName("惟一的标签");
        assertNotNull(found);
        assertEquals("惟一的标签", found.getName());
    }

    @Test
    public void testSelectByNameNotFound() {
        Tag found = tagService.selectByName("不存在的标签");
        assertNull(found);
    }

    @Test
    public void testSelectById() {
        List<Tag> created = tagService.insertTag("ID标签");
        Integer tagId = created.get(0).getId();

        Tag found = tagService.selectById(tagId);
        assertNotNull(found);
        assertEquals(tagId, found.getId());
    }

    @Test
    public void testSelectByTopicId() {
        User user = createTestUser("tag_topic_user");
        Topic topic = topicService.insert("标签话题", "内容", "主题标签1,主题标签2", user);

        List<Tag> tags = tagService.selectByTopicId(topic.getId());
        assertNotNull(tags);
        assertTrue(tags.size() >= 2);
    }

    @Test
    public void testSelectAll() {
        tagService.insertTag("分页标签1");
        tagService.insertTag("分页标签2");

        IPage<Tag> page = tagService.selectAll(1, 10, null);
        assertNotNull(page);
        assertTrue(page.getRecords().size() > 0);
    }

    @Test
    public void testSelectTopicByTagId() {
        User user = createTestUser("tag_topic");
        Topic topic = topicService.insert("标签话题关联", "内容", "关联标签", user);

        Tag tag = tagService.selectByName("关联标签");
        assertNotNull(tag);
        // 验证话题标签关联
        List<Tag> topicTags = tagService.selectByTopicId(topic.getId());
        boolean found = topicTags.stream().anyMatch(t -> t.getId().equals(tag.getId()));
        assertTrue(found);
    }

    @Test
    public void testReduceTopicCount() {
        User user = createTestUser("reduce_user");
        Topic topic = topicService.insert("减少标签计数", "内容", "计数标签", user);

        Tag tag = tagService.selectByName("计数标签");
        assertNotNull(tag);
        assertTrue(tag.getTopicCount() > 0);

        tagService.reduceTopicCount(topic.getId());
        // 删除话题后计数应减少
        Tag updated = tagService.selectByName("计数标签");
        assertEquals(Integer.valueOf(0), updated.getTopicCount());
    }
}
