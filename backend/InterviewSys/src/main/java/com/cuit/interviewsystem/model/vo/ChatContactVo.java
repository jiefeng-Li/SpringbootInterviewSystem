package com.cuit.interviewsystem.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatContactVo {
    private Long userId;
    private String username;
    private String avatarUrl;
    private String lastContent;
    private LocalDateTime lastTime;
    private Long unreadCount;
}
