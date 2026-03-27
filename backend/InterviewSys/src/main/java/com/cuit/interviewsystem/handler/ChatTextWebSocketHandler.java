package com.cuit.interviewsystem.handler;

import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.ChatMessageDto;
import com.cuit.interviewsystem.model.vo.ChatMessageVo;
import com.cuit.interviewsystem.service.ChatMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ChatTextWebSocketHandler extends TextWebSocketHandler {
    @Resource
    private ChatMessageService chatMessageService;

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        Long userId;
        try {
            userId = Long.parseLong((String) session.getAttributes().get("userId"));
        } catch (Exception e) {
            session.close(CloseStatus.POLICY_VIOLATION);
            throw new BusinessException(ErrorEnum.NOT_FOUND_ERROR);
        }
        List<ChatMessageVo> msg = chatMessageService.getUnreadMessageByReceiverId(userId);
        sessions.put(userId, session);
        //获取请求头
        Map<String, String> headers = session.getHandshakeHeaders().toSingleValueMap();
        String token = headers.get("token");
        log.info("用户 {} 连接建立，token: {}", userId, token);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long sendId = null;
        try {
            sendId = Long.parseLong((String) session.getAttributes().get("userId"));
            String payload = message.getPayload();
            ChatMessageDto chatMessage = objectMapper.readValue(payload, ChatMessageDto.class);
            log.info("收到消息: {}", chatMessage);
            forwardMessage(chatMessage);
            if (sendId != chatMessage.getSendId()) {
                throw new BusinessException(ErrorEnum.PARAMS_ERROR, "发送用户与登录用户不一致");
            }
        } catch (Exception e) {
            session.close(CloseStatus.POLICY_VIOLATION);
            throw new BusinessException(ErrorEnum.NOT_LOGIN_ERROR);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.entrySet().removeIf(e -> e.getValue() == session);
    }

    private void forwardMessage(ChatMessageDto message) throws Exception {
        WebSocketSession receiverSession = sessions.get(message.getReceiveId());
        if (receiverSession != null && receiverSession.isOpen()) {
            String json = objectMapper.writeValueAsString(message);
            receiverSession.sendMessage(new TextMessage(json));
        }
        chatMessageService.saveChatMessage(message);
    }

    private Long extractUserId(String query) {
        if (query != null && query.startsWith("userId=")) {
            return Long.parseLong(query.substring(7));
        }
        throw new BusinessException(ErrorEnum.PARAMS_ERROR, "缺少用户id");
    }
}
