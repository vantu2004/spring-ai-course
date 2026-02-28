package com.vantu.multimodel.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/multimodel-chat")
public class MultiModelChatController {
    private final ChatClient openAiChatClient;
    private final ChatClient googleGenAiChatClient;

    // vì 2 bean cùng trả về ChatClient nên phải chỉ định tên để spring inject đúng
    public MultiModelChatController(@Qualifier("openAiChatClient") ChatClient openAiChatClient, @Qualifier("googleGenAiChatClient") ChatClient googleGenAiChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.googleGenAiChatClient = googleGenAiChatClient;
    }

    @GetMapping("/openai")
    public String openAiChat(@RequestParam String message) {
        return this.openAiChatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/google-genai")
    public String googleGenAiChat(@RequestParam String message) {
        return this.googleGenAiChatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
