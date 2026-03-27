package com.cuit.interviewsystem.config;

import com.cuit.interviewsystem.aop.WebSocketAuthInterceptor;
import com.cuit.interviewsystem.handler.ChatTextWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketServletConfig implements WebSocketConfigurer {

    private final ChatTextWebSocketHandler chatTextWebSocketHandler;
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;


    public WebSocketServletConfig(ChatTextWebSocketHandler chatTextWebSocketHandler, WebSocketAuthInterceptor webSocketAuthInterceptor) {
        this.chatTextWebSocketHandler = chatTextWebSocketHandler;
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatTextWebSocketHandler, "/chat")
                .addInterceptors(webSocketAuthInterceptor)
                .setAllowedOriginPatterns("*");
    }
}

