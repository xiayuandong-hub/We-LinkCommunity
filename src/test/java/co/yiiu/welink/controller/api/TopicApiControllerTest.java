package co.yiiu.welink.controller.api;

import co.yiiu.welink.config.BaseApiControllerTest;
import co.yiiu.welink.model.Topic;
import co.yiiu.welink.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TopicApiController 集成测试
 */
public class TopicApiControllerTest extends BaseApiControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private User topicAuthor;
    private User votingUser;

    @Before
    public void setupTopic() {
        topicAuthor = userService.addUser("topic_api_user", "TestPass12345678", null,
                "topic_api@test.com", null, null, false);
        // 创建另一个用户用于点赞测试（不能给自己的话题/评论点赞）
        votingUser = userService.addUser("voting_user", "TestPass12345678", null,
                "voting@test.com", null, null, false);
    }

    @Test
    public void testCreateTopic() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("title", "API创建的话题标题");
        body.put("content", "API创建的话题内容");
        body.put("tags", "API标签");

        mockMvc.perform(post("/api/topic")
                        .header("token", topicAuthor.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk())
                .andExpect(jsonPath("$.detail.title").value("API创建的话题标题"))
                .andExpect(jsonPath("$.detail.userId").value(topicAuthor.getId()));
    }

    @Test
    public void testCreateTopicWithoutAuth() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("title", "未登录创建");
        body.put("content", "内容");

        mockMvc.perform(post("/api/topic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testCreateTopicWithEmptyTitle() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("title", "");
        body.put("content", "内容");
        body.put("tags", "标签");

        mockMvc.perform(post("/api/topic")
                        .header("token", topicAuthor.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testTopicDetail() throws Exception {
        // 创建话题（参数：title, content）
        Topic topic = createTestTopic("详情话题", "详情话题内容");

        mockMvc.perform(get("/api/topic/{id}", topic.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk())
                .andExpect(jsonPath("$.detail.topic.title").value("详情话题"));
    }

    @Test
    public void testTopicDetailNotFound() throws Exception {
        // 话题不存在时，GlobalExceptionHandler 捕获异常并返回 code=201
        mockMvc.perform(get("/api/topic/{id}", 999999)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testUpdateTopic() throws Exception {
        Topic topic = createTestTopic("edit_topic", "编辑前");

        Map<String, String> body = new HashMap<>();
        body.put("title", "编辑后的标题");
        body.put("content", "编辑后的内容");

        mockMvc.perform(put("/api/topic/{id}", topic.getId())
                        .header("token", topicAuthor.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk());
    }

    @Test
    public void testVoteTopic() throws Exception {
        // 使用不同的用户（votingUser）给 topicAuthor 的话题点赞
        Topic topic = createTestTopic("vote_topic", "点赞话题");

        mockMvc.perform(get("/api/topic/{id}/vote", topic.getId())
                        .header("token", votingUser.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk());
    }

    @Test
    public void testDeleteTopic() throws Exception {
        Topic topic = createTestTopic("delete_topic", "待删除");

        mockMvc.perform(delete("/api/topic/{id}", topic.getId())
                        .header("token", topicAuthor.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk());
    }

    /**
     * 辅助方法：创建一个测试话题
     */
    private Topic createTestTopic(String title, String content) {
        return topicService.insert(title, content, "API标签", topicAuthor);
    }
}
