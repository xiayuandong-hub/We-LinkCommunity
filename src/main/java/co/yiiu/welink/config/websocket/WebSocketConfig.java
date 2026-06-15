package co.yiiu.welink.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Created by We-Link.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
@Configuration
@EnableWebSocket
@Profile("!test & !e2e") // 测试和 E2E 环境下无真实的 Servlet 容器，ServerEndpointExporter 会报错
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpoint() {
        return new ServerEndpointExporter();
    }

}
