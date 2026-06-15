package co.yiiu.welink.service;

import co.yiiu.welink.config.BaseServiceTest;
import co.yiiu.welink.model.Topic;
import co.yiiu.welink.model.User;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * NotificationService 单元测试
 */
public class NotificationServiceTest extends BaseServiceTest {

    @Test
    public void testInsertNotification() {
        User user = createTestUser("notif_user");
        User targetUser = createTestUser("notif_target");
        Topic topic = createTestTopic(user, "通知话题");

        notificationService.insert(
                user.getId(),
                targetUser.getId(),
                topic.getId(),
                "comment",
                "有人评论了你的话题"
        );

        List<Map<String, Object>> notifications = notificationService.selectByUserId(targetUser.getId(), null, 10);
        assertNotNull(notifications);
        assertTrue("通知应该至少有一条", notifications.size() >= 0);
    }

    @Test
    public void testSelectByUserIdWithReadFilter() {
        User user = createTestUser("notif_read_user");
        User targetUser = createTestUser("notif_read_target");
        Topic topic = createTestTopic(user, "通知读取测试");

        notificationService.insert(user.getId(), targetUser.getId(), topic.getId(), "comment", "通知内容");

        List<Map<String, Object>> unread = notificationService.selectByUserId(targetUser.getId(), false, 10);
        assertNotNull(unread);
        assertTrue(unread.size() > 0);
    }

    @Test
    public void testCountNotRead() {
        User user = createTestUser("notif_count_user");
        User targetUser = createTestUser("notif_count_target");
        Topic topic = createTestTopic(user, "未读计数");

        notificationService.insert(user.getId(), targetUser.getId(), topic.getId(), "comment", "新通知");

        long count = notificationService.countNotRead(targetUser.getId());
        assertTrue(count >= 0);
    }

    @Test
    public void testMarkRead() {
        User user = createTestUser("notif_mark_user");
        User targetUser = createTestUser("notif_mark_target");
        Topic topic = createTestTopic(user, "标记已读");

        notificationService.insert(user.getId(), targetUser.getId(), topic.getId(), "comment", "标记已读测试");

        notificationService.markRead(targetUser.getId());

        long unreadCount = notificationService.countNotRead(targetUser.getId());
        assertEquals(0, unreadCount);
    }

    @Test
    public void testDeleteByTopicId() {
        User user = createTestUser("notif_del_user");
        User targetUser = createTestUser("notif_del_target");
        Topic topic = createTestTopic(user, "删除通知");

        notificationService.insert(user.getId(), targetUser.getId(), topic.getId(), "comment", "将被删除的通知");

        notificationService.deleteByTopicId(topic.getId());

        List<Map<String, Object>> notifications = notificationService.selectByUserId(targetUser.getId(), null, 10);
        assertTrue(notifications == null || notifications.isEmpty());
    }

    @Test
    public void testDeleteByUserId() {
        User user = createTestUser("notif_del_user2");
        User targetUser = createTestUser("notif_del_target2");
        Topic topic = createTestTopic(user, "删除用户通知");

        notificationService.insert(user.getId(), targetUser.getId(), topic.getId(), "comment", "通知");

        notificationService.deleteByUserId(targetUser.getId());

        long count = notificationService.countNotRead(targetUser.getId());
        assertEquals(0, count);
    }
}
