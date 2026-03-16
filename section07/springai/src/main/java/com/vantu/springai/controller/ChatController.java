package com.vantu.springai.controller;

import com.vantu.springai.advisors.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/normal")
public class ChatController {
    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("message") String message) {
        return this.chatClient.prompt()
                // ko khuyến khích dùng advisor ghi token trong api mà nên cấu hình bean dùng chung
                // .advisors(new TokenUsageAuditAdvisor())
                .user(message)
                .call()
                .content();
    }
}

