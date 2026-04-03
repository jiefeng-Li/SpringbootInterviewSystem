package com.cuit.interviewsystem.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageVo {
    private Long id;
    private Long sendId;
    private Long receiveId;
    private String content;
    private Long timestamp;
    private Integer status;
    private Integer msgType;
    private LocalDateTime sentTime;
    private LocalDateTime readTime;
    private LocalDateTime revokeTime;
}
