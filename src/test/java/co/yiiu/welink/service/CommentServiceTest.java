package co.yiiu.welink.service;

import co.yiiu.welink.config.BaseServiceTest;
import co.yiiu.welink.model.Comment;
import co.yiiu.welink.model.Topic;
import co.yiiu.welink.model.User;
import co.yiiu.welink.model.vo.CommentsByTopic;
import co.yiiu.welink.util.MyPage;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * CommentService 单元测试
 */
public class CommentServiceTest extends BaseServiceTest {

    @Test
    public void testInsertComment() {
        User user = createTestUser("comment_user");
        Topic topic = createTestTopic(user, "评论测试话题");

        Comment comment = createTestComment(user, topic, "这是一条测试评论的内容");
        assertNotNull(comment);
        assertNotNull(comment.getId());
        assertEquals(topic.getId(), comment.getTopicId());
        assertEquals(user.getId(), comment.getUserId());
        assertEquals("这是一条测试评论的内容", comment.getContent());
    }

    @Test
    public void testSelectByTopicId() {
        User user = createTestUser("comment_viewer");
        Topic topic = createTestTopic(user, "查看评论的话题");

        createTestComment(user, topic, "评论1");
        createTestComment(user, topic, "评论2");

        List<CommentsByTopic> comments = commentService.selectByTopicId(topic.getId());
        assertNotNull(comments);
        assertEquals(2, comments.size());
    }

    @Test
    public void testSelectByTopicIdWithNoComments() {
        User user = createTestUser("no_comment");
        Topic topic = createTestTopic(user, "没有评论的话题");

        List<CommentsByTopic> comments = commentService.selectByTopicId(topic.getId());
        assertNotNull(comments);
        assertTrue(comments.isEmpty());
    }

    @Test
    public void testSelectById() {
        User user = createTestUser("find_comment");
        Topic topic = createTestTopic(user, "查找评论");

        Comment comment = createTestComment(user, topic, "被查找的评论");
        Comment found = commentService.selectById(comment.getId());
        assertNotNull(found);
        assertEquals(comment.getId(), found.getId());
    }

    @Test
    public void testSelectByUserId() {
        User user = createTestUser("user_comments");
        Topic topic = createTestTopic(user, "评论者的话题");

        createTestComment(user, topic, "用户的评论1");
        createTestComment(user, topic, "用户的评论2");

        MyPage<Map<String, Object>> page = commentService.selectByUserId(user.getId(), 1, 10);
        assertNotNull(page);
        assertTrue(page.getRecords().size() >= 2);
    }

    @Test
    public void testDeleteComment() {
        User user = createTestUser("del_comment");
        Topic topic = createTestTopic(user, "删除评论话题");

        Comment comment = createTestComment(user, topic, "待删除的评论");
        commentService.delete(comment);

        Comment deleted = commentService.selectById(comment.getId());
        assertNull(deleted);
    }

    @Test
    public void testVoteComment() {
        User user = createTestUser("vote_comment_user");
        Topic topic = createTestTopic(user, "评论点赞话题");

        Comment comment = createTestComment(user, topic, "被点赞的评论");

        // 点赞
        int count = commentService.vote(comment, user);
        assertEquals(1, count);

        Comment voted = commentService.selectById(comment.getId());
        assertNotNull(voted.getUpIds());
        assertTrue(voted.getUpIds().contains(String.valueOf(user.getId())));
    }

    @Test
    public void testDeleteByTopicId() {
        User user = createTestUser("cleanup_user");
        Topic topic = createTestTopic(user, "清理评论的话题");

        createTestComment(user, topic, "评论1");
        createTestComment(user, topic, "评论2");

        commentService.deleteByTopicId(topic.getId());

        List<CommentsByTopic> comments = commentService.selectByTopicId(topic.getId());
        assertTrue(comments == null || comments.isEmpty());
    }

    @Test
    public void testCountToday() {
        User user = createTestUser("count_comment");
        Topic topic = createTestTopic(user, "评论统计");
        createTestComment(user, topic, "今日评论");

        int count = commentService.countToday();
        assertTrue(count >= 0);
    }
}
