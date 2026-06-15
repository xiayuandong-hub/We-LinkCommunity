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
 * NotificationApiController 集成测试
 */
public class NotificationApiControllerTest extends BaseApiControllerTest {

    private User notifUser;
    private User targetUser;
    private Topic topic;

    @Before
    public void setup() {
        notifUser = userService.addUser("notif_sender", "TestPass123", null,
                "notif_sender@test.com", null, null, false);
        targetUser = userService.addUser("notif_receiver", "TestPass123", null,
                "notif_receiver@test.com", null, null, false);
        topic = topicService.insert("通知话题", "内容", "标签", notifUser);

        // 创建一条通知
        notificationService.insert(
                notifUser.getId(),
                targetUser.getId(),
                topic.getId(),
                "comment",
                "有人评论了你的话题"
        );
    }

    @Test
    public void testNotRead() throws Exception {
        mockMvc.perform(get("/api/notification/notRead")
                        .header("token", targetUser.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk());
    }

    @Test
    public void testNotReadWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/notification/notRead")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }

    @Test
    public void testMarkRead() throws Exception {
        mockMvc.perform(get("/api/notification/markRead")
                        .header("token", targetUser.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isOk());
    }

    @Test
    public void testList() throws Exception {
        mockMvc.perform(get("/api/notification/list")
                        .header("token", targetUser.getToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testListWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/notification/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(isError());
    }
}
