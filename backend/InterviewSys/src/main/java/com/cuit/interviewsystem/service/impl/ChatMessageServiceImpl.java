package com.cuit.interviewsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.ChatMessageDto;
import com.cuit.interviewsystem.model.entity.ChatMessage;
import com.cuit.interviewsystem.model.entity.User;
import com.cuit.interviewsystem.model.enums.ChatMessageStatusEnum;
import com.cuit.interviewsystem.model.vo.ChatMessageVo;
import com.cuit.interviewsystem.service.ChatMessageService;
import com.cuit.interviewsystem.mapper.ChatMessageMapper;
import com.cuit.interviewsystem.utils.JWTUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    public void saveChatMessage(@Valid ChatMessageDto msg) {
        ChatMessage message = new ChatMessage();
        BeanUtils.copyProperties(msg, message);
        chatMessageMapper.insert(message);
    }

    @Override
    public List<ChatMessageVo> getUnreadMessageByReceiverId(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorEnum.NOT_FOUND_ERROR, "用户id不能为空");
        }
        return chatMessageMapper.selectByReceiveId(userId);
    }

    @Override
    public void markMessagesAsRead(List<Long> messageIds) {
        User user = jwtUtil.parseLoginUser();
        if (messageIds == null || messageIds.isEmpty()) {
            throw new BusinessException(ErrorEnum.PARAMS_ERROR, "消息ID列表不能为空");
        }
        LambdaUpdateWrapper<ChatMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ChatMessage::getId, messageIds)
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
        // 检查消息发送时间是否超过2分钟
        long timeDiff = System.currentTimeMillis() - chatMessage.getTimestamp();
        if (timeDiff > 2 * 60 * 1000) {
            throw new BusinessException(ErrorEnum.NOT_FOUND_ERROR, "消息发送超过2分钟，无法撤回");
        }
        // 更新消息状态为撤回
        LambdaUpdateWrapper<ChatMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ChatMessage::getId, id)
                .set(ChatMessage::getStatus, ChatMessageStatusEnum.REVOKE.getCode());
        this.update(updateWrapper);
    }
}
