package com.cuit.interviewsystem.model.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class ChatMessageDto {
    @NotNull(message = "发送者ID不能为空")
    private Long sendId;
    @NotNull(message = "接收者ID不能为空")
    private Long receiveId;
    @Length(max = 1024, message = "消息过长")
    @NotBlank(message = "消息不能为空")
    private String content;
    @NotNull(message = "时间戳不能为空")
    private Long timestamp;
}