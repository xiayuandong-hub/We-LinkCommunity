package co.yiiu.welink.controller.api;

import co.yiiu.welink.config.BaseApiControllerTest;
import co.yiiu.welink.model.Topic;
import co.yiiu.welink.model.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserApiController 集成测试
 */
public class UserApiControllerTest extends BaseApiControllerTest {

    private User testUser;

    @Before
    public void setupUser() {
        testUser = userService.addUser("profileusr", "TestPass12345678", null,
                "profile@test.com", "个人简介", "https://profile.com", false);
    }

    @Test
    public void testUserProfile() throws Exception {
        mockMvc.perform(get("/api/user/{username}", "profileusr")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUserProfileNotFound() throws Exception {
        mockMvc.perform(get("/api/user/{username}", "nonexistent")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testUserTopics() throws Exception {
        topicService.insert("用户话题1", "内容1", "标签", testUser);
        topicService.insert("用户话题2", "内容2", "标签", testUser);

        mockMvc.perform(get("/api/user/{username}/topics", "profileusr")
                        .param("pageNo", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUserComments() throws Exception {
        Topic topic = topicService.insert("评论话题", "内容", "标签", testUser);
        co.yiiu.welink.model.Comment comment = new co.yiiu.welink.model.Comment();
        comment.setContent("用户评论");
        comment.setTopicId(topic.getId());
        comment.setUserId(testUser.getId());
        comment.setInTime(new java.util.Date());
        commentService.insert(comment, topic, testUser);

        mockMvc.perform(get("/api/user/{username}/comments", "profileusr")
                        .param("pageNo", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUserCollects() throws Exception {
        Topic topic = topicService.insert("收藏话题", "内容", "标签", testUser);

        mockMvc.perform(get("/api/user/{username}/collects", "profileusr")
                        .param("pageNo", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
