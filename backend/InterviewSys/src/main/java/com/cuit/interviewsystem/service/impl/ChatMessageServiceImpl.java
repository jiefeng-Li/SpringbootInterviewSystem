package com.cuit.interviewsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.ChatMessageDto;
import com.cuit.interviewsystem.model.entity.ChatMessage;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.ChatMessageStatusEnum;
import com.cuit.interviewsystem.model.vo.ChatContactVo;
import com.cuit.interviewsystem.model.vo.ChatMessageVo;
import com.cuit.interviewsystem.mapper.UserMapper;
import com.cuit.interviewsystem.service.ChatMessageService;
import com.cuit.interviewsystem.mapper.ChatMessageMapper;
import com.cuit.interviewsystem.utils.JWTUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author jiefe
* @description 针对表【t_chat_message(聊天记录表)】的数据库操作Service实现
* @createDate 2026-03-05 21:01:17
*/
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService{
    @Resource
    private ChatMessageMapper chatMessageMapper;
    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private UserMapper userMapper;

    @Override
    public ChatMessageVo saveChatMessage(@Valid ChatMessageDto msg) {
        ChatMessage message = new ChatMessage();
        BeanUtils.copyProperties(msg, message);
        message.setStatus(ChatMessageStatusEnum.UNREAD.getCode());
        message.setSentTime(LocalDateTime.now());
        message.setMsgType(msg.getMsgType() == null ? 0 : msg.getMsgType());
        message.setTimestamp(msg.getTimestamp() == null ? System.currentTimeMillis() : msg.getTimestamp());
        chatMessageMapper.insert(message);

        ChatMessageVo vo = new ChatMessageVo();
        BeanUtils.copyProperties(message, vo);
        return vo;
    }

    @Override
    public List<ChatContactVo> getChatContacts() {
        User currentUser = jwtUtil.parseLoginUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorEnum.NOT_LOGIN_ERROR);
        }

        List<ChatMessageVo> recentMessages = chatMessageMapper.selectRecentMessagesByUser(currentUser.getUserId(), 500);
        Map<Long, ChatContactVo> contactMap = new HashMap<>();
        Map<Long, Long> unreadMap = new HashMap<>();

        for (ChatMessageVo msg : recentMessages) {
            Long peerId = currentUser.getUserId().equals(msg.getSendId()) ? msg.getReceiveId() : msg.getSendId();
            if (!contactMap.containsKey(peerId)) {
                User peer = userMapper.selectById(peerId);
                if (peer == null || peer.getIsDeleted() == 1) {
                    continue;
                }
                ChatContactVo contactVo = new ChatContactVo();
                contactVo.setUserId(peer.getUserId());
                contactVo.setUsername(peer.getUsername());
                contactVo.setAvatarUrl(peer.getAvatarUrl());
                contactVo.setLastContent(msg.getStatus() != null && msg.getStatus() == ChatMessageStatusEnum.REVOKE.getCode() ? "[消息已撤回]" : msg.getContent());
                contactVo.setLastTime(msg.getSentTime());
                contactVo.setUnreadCount(0L);
                contactMap.put(peerId, contactVo);
            }

            if (currentUser.getUserId().equals(msg.getReceiveId())
                    && msg.getStatus() != null
                    && msg.getStatus().equals(ChatMessageStatusEnum.UNREAD.getCode())) {
                unreadMap.put(peerId, unreadMap.getOrDefault(peerId, 0L) + 1);
            }
        }

        List<ChatContactVo> contacts = new ArrayList<>(contactMap.values());
        for (ChatContactVo c : contacts) {
            c.setUnreadCount(unreadMap.getOrDefault(c.getUserId(), 0L));
        }
        contacts.sort((a, b) -> {
            if (a.getLastTime() == null && b.getLastTime() == null) {
                return 0;
            }
            if (a.getLastTime() == null) {
                return 1;
            }
            if (b.getLastTime() == null) {
                return -1;
            }
            return b.getLastTime().compareTo(a.getLastTime());
        });
        return contacts;
    }

    @Override
    public List<ChatMessageVo> getUnreadMessageByReceiverId(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorEnum.NOT_FOUND_ERROR, "用户id不能为空");
        }
        return chatMessageMapper.selectByReceiveId(userId);
    }

    @Override
    public List<ChatMessageVo> getConversationMessages(Long targetUserId, Integer limit) {
        User currentUser = jwtUtil.parseLoginUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        if (targetUserId == null || targetUserId <= 0) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR, "对话对象不能为空");
        }
        int safeLimit = (limit == null || limit <= 0) ? 50 : Math.min(limit, 200);
        return chatMessageMapper.selectConversationMessages(currentUser.getUserId(), targetUserId, safeLimit);
    }

    @Override
    public void markMessagesAsRead(List<Long> messageIds) {
        User user = jwtUtil.parseLoginUser();
        if (user == null) {
            throw new BusinessException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        if (messageIds == null || messageIds.isEmpty()) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR, "消息ID列表不能为空");
        }
        LambdaUpdateWrapper<ChatMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ChatMessage::getId, messageIds)
                .eq(ChatMessage::getReceiveId, user.getUserId())
                .eq(ChatMessage::getStatus, ChatMessageStatusEnum.UNREAD.getCode())
                .set(ChatMessage::getReadTime, LocalDateTime.now())
                .set(ChatMessage::getStatus, ChatMessageStatusEnum.READ.getCode());
        this.update(updateWrapper);
    }

    @Override
    public void revokeMessage(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR, "消息ID不能为空");
        }
        ChatMessage chatMessage = chatMessageMapper.selectById(id);
        if (chatMessage == null || chatMessage.getIsDeleted() == 1) {
            throw new BusinessException(ErrorEnum.NOT_FOUND_ERROR, "消息不存在");
        }
        // 获取当前登录用户
        User currentUser = jwtUtil.parseLoginUser();
        // 验证是否为消息发送者
        if (!currentUser.getUserId().equals(chatMessage.getSendId())) {
            throw new BusinessException(ErrorEnum.UNAUTHORIZED, "无权撤回该消息");
        }
        // 检查消息是否已撤回
        if (ChatMessageStatusEnum.REVOKE.getCode().equals(chatMessage.getStatus())) {
            throw new BusinessException(ErrorEnum.NOT_FOUND_ERROR, "消息已被撤回");
        }
        // 检查消息发送时间是否超过2分钟(优先使用服务端发送时间)
        long sentMillis = chatMessage.getSentTime() != null
            ? chatMessage.getSentTime().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            : (chatMessage.getTimestamp() == null ? System.currentTimeMillis() : chatMessage.getTimestamp());
        long timeDiff = System.currentTimeMillis() - sentMillis;
        if (timeDiff > 2 * 60 * 1000) {
            throw new BusinessException(ErrorEnum.NOT_FOUND_ERROR, "消息发送超过2分钟，无法撤回");
        }
        // 更新消息状态为撤回
        LambdaUpdateWrapper<ChatMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ChatMessage::getId, id)
            .set(ChatMessage::getRevokeTime, LocalDateTime.now())
                .set(ChatMessage::getStatus, ChatMessageStatusEnum.REVOKE.getCode());
        this.update(updateWrapper);
    }
}
