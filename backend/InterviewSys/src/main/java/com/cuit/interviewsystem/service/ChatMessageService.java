package com.cuit.interviewsystem.service;

import com.cuit.interviewsystem.model.dto.ChatMessageDto;
import com.cuit.interviewsystem.model.entity.ChatMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cuit.interviewsystem.model.vo.ChatContactVo;
import com.cuit.interviewsystem.model.vo.ChatMessageVo;

import java.util.List;

/**
* @author jiefe
* @description 针对表【t_chat_message(聊天记录表)】的数据库操作Service
* @createDate 2026-03-05 21:01:17
*/
public interface ChatMessageService extends IService<ChatMessage> {

    ChatMessageVo saveChatMessage(ChatMessageDto message);

    List<ChatContactVo> getChatContacts();

    List<ChatMessageVo> getUnreadMessageByReceiverId(Long userId);

    List<ChatMessageVo> getConversationMessages(Long targetUserId, Integer limit);

    void markMessagesAsRead(List<Long> messageIds);

    void revokeMessage(Long id);
}
