package com.cuit.interviewsystem.model.vo;

import lombok.Data;

@Data
public class ChatMessageVo {
    private Long sendId;
    private Long receiveId;
    private String content;
    private Long timestamp;
}
