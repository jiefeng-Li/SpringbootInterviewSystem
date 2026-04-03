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

@Component
@Slf4j
public class ChatTextWebSocketHandler extends TextWebSocketHandler {
    @Resource
    private ChatMessageService chatMessageService;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ChatSessionManager chatSessionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId;
        try {
            userId = Long.parseLong((String) session.getAttributes().get("userId"));
        } catch (Exception e) {
            session.close(CloseStatus.POLICY_VIOLATION);
            throw new BusinessException(ErrorEnum.NOT_FOUND_ERROR);
        }
        List<ChatMessageVo> msg = chatMessageService.getUnreadMessageByReceiverId(userId);
        chatSessionManager.putSession(userId, session);
        //获取请求头
        Map<String, String> headers = session.getHandshakeHeaders().toSingleValueMap();
        String token = headers.get("token");
        log.info("用户 {} 连接建立，token: {}", userId, token);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long loginUserId;
        try {
            loginUserId = Long.parseLong((String) session.getAttributes().get("userId"));
        } catch (Exception e) {
            session.close(CloseStatus.POLICY_VIOLATION);
            throw new BusinessException(ErrorEnum.NOT_LOGIN_ERROR);
        }

        ChatMessageDto chatMessage;
        try {
            chatMessage = objectMapper.readValue(message.getPayload(), ChatMessageDto.class);
        } catch (Exception e) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR, "消息格式错误");
        }

        log.info("收到消息: {}", chatMessage);
        if (!loginUserId.equals(chatMessage.getSendId())) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR, "发送用户与登录用户不一致");
        }
        forwardMessage(chatMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        chatSessionManager.removeSession(session);
    }

    private void forwardMessage(ChatMessageDto message) throws Exception {
        ChatMessageVo savedMessage = chatMessageService.saveChatMessage(message);
        chatSessionManager.sendToUser(message.getSendId(), savedMessage);
        chatSessionManager.sendToUser(message.getReceiveId(), savedMessage);
    }
}
