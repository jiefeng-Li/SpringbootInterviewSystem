package com.cuit.interviewsystem.controller;


import com.cuit.interviewsystem.common.Result;
import com.cuit.interviewsystem.service.ChatMessageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("chat")
public class ChatController {
    @Resource
    private ChatMessageService chatMessageService;

    @PostMapping("/msg")
    @Operation(summary = "标记消息为已读")
    public Result<Void> markMessagesAsRead(@RequestBody List<Long> messageIds) {
        chatMessageService.markMessagesAsRead(messageIds);
        return Result.success(null);
    }


}
