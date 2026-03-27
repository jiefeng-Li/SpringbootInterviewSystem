package com.cuit.interviewsystem.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@Tag(name = "ai聊天接口")
public class AIController {
    @Resource
    private ChatClient dashScopeChatClient;

    @GetMapping("/test")
    @Operation(summary = "测试接口(非响应)")
    public String chatTest(String prompt) {
        return dashScopeChatClient.prompt(prompt).call().content();
    }

    @GetMapping("/test/stream/chat")
    @Operation(summary = "测试接口(响应式)")
    public Flux<String> streamChat(HttpServletResponse response, String prompt) {
        response.setCharacterEncoding("UTF-8");
        return dashScopeChatClient.prompt(prompt).stream().content();
    }
}
