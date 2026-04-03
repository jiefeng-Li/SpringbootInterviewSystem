package com.cuit.interviewsystem.model.dto.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SystemMessagePushDto {
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * true: 发送给全量用户(默认)
     * false: 发送给targetUserIds指定用户
     */
    private Boolean sendToAll = true;

    private List<Long> targetUserIds;

    /**
     * 是否包含当前发送管理员自己
     */
    private Boolean includeSelf = false;
}
