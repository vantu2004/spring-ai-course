package com.vantu.springai.controller;

import com.vantu.springai.model.CountryCities;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/structure")
public class StructuredOutputController {
    private final ChatClient chatClient;

    // phải tự inject lại ChatCLient mới để tránh config mặc định của bean đã đăng ký
    public StructuredOutputController(ChatClient.Builder chatClientBuilder) {
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_5_NANO)
                .temperature(1.0)
                .build();
        this.chatClient = chatClientBuilder.defaultAdvisors(new SimpleLoggerAdvisor()).defaultOptions(openAiChatOptions).build();
    }

    @GetMapping("/chat")
    public ResponseEntity<CountryCities> chat(@RequestParam("message") String message) {
        CountryCities countryCities = this.chatClient.prompt()
                .user(message)
                .call()
                .entity(CountryCities.class);

        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(countryCities);
    }
}
