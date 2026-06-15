package co.yiiu.welink.config;

import co.yiiu.welink.model.Comment;
import co.yiiu.welink.model.Topic;
import co.yiiu.welink.model.User;
import co.yiiu.welink.service.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Service 层测试基类
 * <p>
 * 提供：
 * - H2 内存数据库环境
 * - 每个测试自动回滚（@Transactional）
 * - 辅助方法：快速创建测试用户、话题、标签
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseServiceTest {

    @Resource
    protected IUserService userService;

    @Resource
    protected ITopicService topicService;

    @Resource
    protected ICommentService commentService;

    @Resource
    protected ITagService tagService;

    @Resource
    protected ICollectService collectService;

    @Resource
    protected INotificationService notificationService;

    @Resource
    protected ICodeService codeService;

    @Resource
    protected ISystemConfigService systemConfigService;

    @Resource
    protected ISensitiveWordService sensitiveWordService;

    @Resource
    protected IAdminUserService adminUserService;

    // ========== 辅助方法：快速创建测试实体 ==========

    /**
     * 创建一个测试用户
     */
    protected User createTestUser(String username) {
        return userService.addUser(
                username,
                "TestPass12345678",
                null,
                username + "@test.com",
                "测试用户简介",
                "https://" + username + ".com",
                false
        );
    }

    /**
     * 创建一个测试话题
     */
    protected Topic createTestTopic(User user, String title) {
        return topicService.insert(
                title,
                "这是话题内容：" + title,
                "测试标签",
                user
        );
    }

    /**
     * 创建一个测试评论
     */
    protected Comment createTestComment(User user, Topic topic, String content) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setInTime(new Date());
        comment.setTopicId(topic.getId());
        comment.setUserId(user.getId());
        return commentService.insert(comment, topic, user);
    }
}
