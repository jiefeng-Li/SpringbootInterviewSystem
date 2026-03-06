package com.cuit.interviewsystem.config;

import com.cuit.interviewsystem.handler.ChatTextWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketServletConfig implements WebSocketConfigurer {

    private final ChatTextWebSocketHandler chatTextWebSocketHandler;

    public WebSocketServletConfig(ChatTextWebSocketHandler chatTextWebSocketHandler) {
        this.chatTextWebSocketHandler = chatTextWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatTextWebSocketHandler, "/chat")
                .setAllowedOriginPatterns("*");
    }
}

