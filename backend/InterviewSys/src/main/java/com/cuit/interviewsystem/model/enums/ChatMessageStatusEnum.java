package com.cuit.interviewsystem.model.enums;

import lombok.Getter;

/**
 * 消息状态(未读0,已读1,撤回2)
 */
@Getter
public enum ChatMessageStatusEnum {
    UNREAD(0, "未读"),
    READ(1, "已读"),
    REVOKE(2, "撤回");

    private final Integer code;
    private final String text;

    ChatMessageStatusEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }
}
