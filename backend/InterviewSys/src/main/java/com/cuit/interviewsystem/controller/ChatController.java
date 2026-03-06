package com.cuit.interviewsystem.controller;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ChatController {
    @Resource
    private ChatClient dashScopeChatClient;

    @GetMapping("/test")
    public String chatTest(String prompt) {
        return dashScopeChatClient.prompt(prompt).call().content();
    }
}
