package com.vantu.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// promptStuffing là cách đưa 1 đoạn context ngắn cho LLM hc và trả lời
@RestController
@RequestMapping("/api/v1/prompt-stuffing")
public class PromptStuffingController {
    private final ChatClient chatClient;

    public PromptStuffingController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    Resource systemPromptTemplate;

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                // ngoài cách dùng bean đã config thì có thể tự config lại option cho api
                .options(OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.GPT_5_NANO)
                        .temperature(1.0)
                        .build()).system(systemPromptTemplate)
                .user(message)
                .call()
                .content();
    }
}
