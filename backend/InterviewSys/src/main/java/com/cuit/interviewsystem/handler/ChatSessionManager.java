package com.cuit.interviewsystem.handler;

import com.cuit.interviewsystem.model.vo.ChatMessageVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatSessionManager {
    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Resource
    private ObjectMapper objectMapper;

    public void putSession(Long userId, WebSocketSession session) {
        if (userId == null || session == null) {
            return;
        }
        sessions.put(userId, session);
    }

    public void removeSession(WebSocketSession session) {
        if (session == null) {
            return;
        }
        sessions.entrySet().removeIf(e -> e.getValue() == session);
    }

    public void sendToUser(Long userId, ChatMessageVo message) {
        if (userId == null || message == null) {
            return;
        }
        WebSocketSession socketSession = sessions.get(userId);
        if (socketSession == null || !socketSession.isOpen()) {
            return;
        }
        try {
            socketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (Exception ignored) {
            // push失败不影响主流程
        }
    }
}
