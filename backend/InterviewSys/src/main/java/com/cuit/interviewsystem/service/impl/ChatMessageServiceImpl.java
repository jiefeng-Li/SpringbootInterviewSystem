package com.cuit.interviewsystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cuit.interviewsystem.exception.BusinessException;
import com.cuit.interviewsystem.exception.ErrorEnum;
import com.cuit.interviewsystem.model.dto.ChatMessageDto;
import com.cuit.interviewsystem.model.entity.ChatMessage;
import com.cuit.interviewsystem.model.vo.ChatMessageVo;
import com.cuit.interviewsystem.service.ChatMessageService;
import com.cuit.interviewsystem.mapper.ChatMessageMapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
}




