package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天记录表
 * @TableName t_chat_message
 */
@TableName(value ="t_chat_message")
@Data
public class ChatMessage {
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送者ID
     */
    private Long sendId;

    /**
     * 接收者ID
     */
    private Long receiveId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送时间戳
     */
    private Long timestamp;

    /**
     * 服务端发送时间
     */
    private LocalDateTime sentTime;

    /**
     * 已读时间
     */
    private LocalDateTime readTime;

    /**
     * 撤回时间
     */
    private LocalDateTime revokeTime;

    /**
     * 消息类型(0文本,1系统消息)
     */
    private Integer msgType;

    /**
     * 消息状态(未读0,已读1,撤回2)
     */
    private Integer status;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
}