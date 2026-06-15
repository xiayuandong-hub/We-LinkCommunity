package co.yiiu.welink.service;

import co.yiiu.welink.config.BaseServiceTest;
import co.yiiu.welink.model.*;
import co.yiiu.welink.util.MyPage;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * CollectService 单元测试
 */
public class CollectServiceTest extends BaseServiceTest {

    @Test
    public void testInsertCollect() {
        User user = createTestUser("collect_user");
        Topic topic = createTestTopic(user, "被收藏的话题");

        Collect collect = collectService.insert(topic.getId(), user);
        assertNotNull(collect);
        assertEquals(topic.getId(), collect.getTopicId());
        assertEquals(user.getId(), collect.getUserId());
    }

    @Test
    public void testSelectByTopicId() {
        User user = createTestUser("collect_stat");
        Topic topic = createTestTopic(user, "收藏统计");

        collectService.insert(topic.getId(), user);

        List<Collect> collects = collectService.selectByTopicId(topic.getId());
        assertNotNull(collects);
        assertEquals(1, collects.size());
    }

    @Test
    public void testSelectByTopicIdAndUserId() {
        User user = createTestUser("collect_check");
        Topic topic = createTestTopic(user, "收藏检查");

        collectService.insert(topic.getId(), user);

        Collect collect = collectService.selectByTopicIdAndUserId(topic.getId(), user.getId());
        assertNotNull(collect);
    }

    @Test
    public void testSelectByTopicIdAndUserIdWhenNotCollected() {
        User user = createTestUser("not_collected");
        Topic topic = createTestTopic(user, "未收藏");

        Collect collect = collectService.selectByTopicIdAndUserId(topic.getId(), user.getId());
        // 话题作者不会自动收藏，所以应该为 null
        assertNull(collect);
    }

    @Test
    public void testDeleteCollect() {
        User user = createTestUser("cancel_collect");
        Topic topic = createTestTopic(user, "取消收藏");

        collectService.insert(topic.getId(), user);
        collectService.delete(topic.getId(), user.getId());

        Collect collect = collectService.selectByTopicIdAndUserId(topic.getId(), user.getId());
        assertNull(collect);
    }

    @Test
    public void testCountByUserId() {
        User user = createTestUser("collect_count");
        Topic topic1 = createTestTopic(user, "收藏1");
        Topic topic2 = createTestTopic(user, "收藏2");

        collectService.insert(topic1.getId(), user);
        collectService.insert(topic2.getId(), user);

        int count = collectService.countByUserId(user.getId());
        assertEquals(2, count);
    }

    @Test
    public void testSelectByUserId() {
        User user = createTestUser("collect_list");
        Topic topic = createTestTopic(user, "收藏列表");
        collectService.insert(topic.getId(), user);

        MyPage<Map<String, Object>> page = collectService.selectByUserId(user.getId(), 1, 10);
        assertNotNull(page);
        assertTrue(page.getRecords().size() > 0);
    }

    @Test
    public void testDeleteByTopicId() {
        User user = createTestUser("del_collect_topic");
        Topic topic = createTestTopic(user, "删除收藏话题");

        collectService.insert(topic.getId(), user);
        collectService.deleteByTopicId(topic.getId());

        List<Collect> collects = collectService.selectByTopicId(topic.getId());
        assertTrue(collects.isEmpty());
    }
}
