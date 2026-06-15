package co.yiiu.welink.controller.api;

import co.yiiu.welink.config.BaseApiControllerTest;
import co.yiiu.welink.model.Topic;
import co.yiiu.welink.model.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CollectApiController 集成测试
 */
public class CollectApiControllerTest extends BaseApiControllerTest {

    private User collector;
    private Topic targetTopic;

    @Before
    public void setup() {
        collector = userService.addUser("collect_api_user", "TestPass123", null,
                "collect_api@test.com", null, null, false);
        targetTopic = topicService.insert("可收藏的话题", "内容", "收藏标签", collector);
    }

    @Test
    public void testCollectTopic() throws Exception {
        mockMvc.perform(post("/api/collect/{topicId}", targetTopic.getId())
                        .header("token", collector.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk());
    }

    @Test
    public void testCollectWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/collect/{topicId}", targetTopic.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testDeleteCollect() throws Exception {
        // 先收藏
        collectService.insert(targetTopic.getId(), collector);

        mockMvc.perform(delete("/api/collect/{topicId}", targetTopic.getId())
                        .header("token", collector.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk());
    }

    @Test
    public void testCollectNonExistentTopic() throws Exception {
        mockMvc.perform(post("/api/collect/{topicId}", 999999)
                        .header("token", collector.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }
}
