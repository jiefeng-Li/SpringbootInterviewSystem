package com.cuit.interviewsystem.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

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
     * 消息状态(未读0,已读1,撤回2)
     */
    private Integer status;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;
}