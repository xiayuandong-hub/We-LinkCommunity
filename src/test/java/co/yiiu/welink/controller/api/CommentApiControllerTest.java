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
 * CommentApiController 集成测试
 */
public class CommentApiControllerTest extends BaseApiControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private User commentAuthor;
    private User votingUser;
    private Topic testTopic;

    @Before
    public void setup() {
        commentAuthor = userService.addUser("comment_api_user", "TestPass12345678", null,
                "comment_api@test.com", null, null, false);
        votingUser = userService.addUser("comment_voter", "TestPass12345678", null,
                "comment_voter@test.com", null, null, false);
        testTopic = topicService.insert("评论接口话题", "话题内容", "评论标签", commentAuthor);
    }

    @Test
    public void testCreateComment() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("content", "API评论的内容");
        body.put("topicId", String.valueOf(testTopic.getId()));

        mockMvc.perform(post("/api/comment")
                        .header("token", commentAuthor.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk())
                .andExpect(jsonPath("$.detail.content").value("API评论的内容"));
    }

    @Test
    public void testCreateCommentWithoutAuth() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("content", "未登录评论");
        body.put("topicId", String.valueOf(testTopic.getId()));

        mockMvc.perform(post("/api/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testCreateCommentWithEmptyContent() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("content", "");
        body.put("topicId", String.valueOf(testTopic.getId()));

        mockMvc.perform(post("/api/comment")
                        .header("token", commentAuthor.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testCreateCommentWithoutTopicId() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("content", "没有话题ID");

        mockMvc.perform(post("/api/comment")
                        .header("token", commentAuthor.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testDeleteComment() throws Exception {
        // 先创建一条评论
        co.yiiu.welink.model.Comment comment = new co.yiiu.welink.model.Comment();
        comment.setContent("待删除评论");
        comment.setTopicId(testTopic.getId());
        comment.setUserId(commentAuthor.getId());
        comment.setInTime(new java.util.Date());
        comment = commentService.insert(comment, testTopic, commentAuthor);

        mockMvc.perform(delete("/api/comment/{id}", comment.getId())
                        .header("token", commentAuthor.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk());
    }

    @Test
    public void testVoteComment() throws Exception {
        // 先创建一条评论
        co.yiiu.welink.model.Comment comment = new co.yiiu.welink.model.Comment();
        comment.setContent("点赞评论");
        comment.setTopicId(testTopic.getId());
        comment.setUserId(commentAuthor.getId());
        comment.setInTime(new java.util.Date());
        comment = commentService.insert(comment, testTopic, commentAuthor);

        // 使用不同用户（votingUser）点赞（不能给自己的评论点赞）
        mockMvc.perform(get("/api/comment/{id}/vote", comment.getId())
                        .header("token", votingUser.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk());
    }

    @Test
    public void testUpdateComment() throws Exception {
        co.yiiu.welink.model.Comment comment = new co.yiiu.welink.model.Comment();
        comment.setContent("原内容");
        comment.setTopicId(testTopic.getId());
        comment.setUserId(commentAuthor.getId());
        comment.setInTime(new java.util.Date());
        comment = commentService.insert(comment, testTopic, commentAuthor);

        Map<String, String> body = new HashMap<>();
        body.put("content", "更新后的内容");

        mockMvc.perform(put("/api/comment/{id}", comment.getId())
                        .header("token", commentAuthor.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk());
    }
}
