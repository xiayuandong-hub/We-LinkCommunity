package co.yiiu.welink.service;

import co.yiiu.welink.config.BaseServiceTest;
import co.yiiu.welink.model.Topic;
import co.yiiu.welink.model.User;
import co.yiiu.welink.util.MyPage;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * TopicService 单元测试
 */
public class TopicServiceTest extends BaseServiceTest {

    @Test
    public void testInsertTopic() {
        User user = createTestUser("topic_author");
        Topic topic = createTestTopic(user, "测试话题的标题");

        assertNotNull(topic);
        assertNotNull(topic.getId());
        assertEquals("测试话题的标题", topic.getTitle());
        assertEquals(user.getId(), topic.getUserId());
        assertEquals(Integer.valueOf(1), topic.getView());
        assertEquals(Integer.valueOf(0), topic.getCommentCount());
        assertEquals(Integer.valueOf(0), topic.getCollectCount());
        assertFalse(topic.getTop());
        assertFalse(topic.getGood());
    }

    @Test
    public void testSelectById() {
        User user = createTestUser("topic_finder");
        Topic topic = createTestTopic(user, "查找这个话题");

        Topic found = topicService.selectById(topic.getId());
        assertNotNull(found);
        assertEquals(topic.getId(), found.getId());
        assertEquals("查找这个话题", found.getTitle());
    }

    @Test
    public void testSelectByIdNotFound() {
        Topic found = topicService.selectById(999999);
        assertNull(found);
    }

    @Test
    public void testSelectByTitle() {
        User user = createTestUser("title_finder");
        createTestTopic(user, "惟一的标题");

        Topic found = topicService.selectByTitle("惟一的标题");
        assertNotNull(found);
        assertEquals("惟一的标题", found.getTitle());
    }

    @Test
    public void testSelectAll() {
        User user = createTestUser("list_user");
        createTestTopic(user, "列表话题1");
        createTestTopic(user, "列表话题2");

        MyPage<Map<String, Object>> page = topicService.selectAll(1, "all");
        assertNotNull(page);
        assertTrue(page.getRecords().size() > 0);
    }

    @Test
    public void testSelectByUserId() {
        User user = createTestUser("user_topics");
        createTestTopic(user, "用户的话题1");
        createTestTopic(user, "用户的话题2");
        createTestTopic(user, "用户的话题3");

        MyPage<Map<String, Object>> page = topicService.selectByUserId(user.getId(), 1, 10);
        assertNotNull(page);
        assertEquals(3, page.getRecords().size());
    }

    @Test
    public void testUpdateTopic() {
        User user = createTestUser("update_topic");
        Topic topic = createTestTopic(user, "更新前的标题");

        topic.setTitle("更新后的标题");
        topicService.update(topic, null);

        Topic updated = topicService.selectById(topic.getId());
        assertEquals("更新后的标题", updated.getTitle());
    }

    @Test
    public void testUpdateViewCount() {
        User user = createTestUser("view_count");
        Topic topic = createTestTopic(user, "浏览量测试");
        Integer originalView = topic.getView();

        Topic updated = topicService.updateViewCount(topic, "127.0.0.1");
        assertEquals(Integer.valueOf(originalView + 1), updated.getView());
    }

    @Test
    public void testDeleteTopic() {
        User user = createTestUser("delete_topic");
        Topic topic = createTestTopic(user, "待删除的话题");

        topicService.delete(topic);

        Topic deleted = topicService.selectById(topic.getId());
        assertNull(deleted);
    }

    @Test
    public void testSelectAuthorOtherTopic() {
        User user = createTestUser("author_other");
        Topic topic1 = createTestTopic(user, "作者话题1");
        Topic topic2 = createTestTopic(user, "作者话题2");

        List<Topic> otherTopics = topicService.selectAuthorOtherTopic(user.getId(), topic1.getId(), 5);
        assertNotNull(otherTopics);
        // 排除 topic1 后应包含 topic2
        boolean found = otherTopics.stream().anyMatch(t -> t.getId().equals(topic2.getId()));
        assertTrue(found);
        boolean notFound = otherTopics.stream().noneMatch(t -> t.getId().equals(topic1.getId()));
        assertTrue(notFound);
    }

    @Test
    public void testVote() {
        User user = createTestUser("voter");
        Topic topic = createTestTopic(user, "点赞测试");

        // 点赞
        int count1 = topicService.vote(topic, user);
        // 点赞后 upIds 不应为空，count 应为 1
        assertTrue(count1 > 0);

        Topic votedTopic = topicService.selectById(topic.getId());
        assertNotNull(votedTopic.getUpIds());
        assertTrue(votedTopic.getUpIds().contains(String.valueOf(user.getId())));
    }

    @Test
    public void testVoteCancel() {
        User user = createTestUser("unvoter");
        Topic topic = createTestTopic(user, "取消点赞测试");

        // 先点赞
        topicService.vote(topic, user);
        Topic voted = topicService.selectById(topic.getId());
        assertTrue(voted.getUpIds().contains(String.valueOf(user.getId())));

        // 再取消点赞
        int count = topicService.vote(voted, user);
        Topic unvoted = topicService.selectById(topic.getId());
        assertFalse(unvoted.getUpIds() != null && unvoted.getUpIds().contains(String.valueOf(user.getId())));
    }

    @Test
    public void testCountToday() {
        User user = createTestUser("count_today_t");
        createTestTopic(user, "今日话题1");
        int count = topicService.countToday();
        assertTrue(count >= 0);
    }
}
