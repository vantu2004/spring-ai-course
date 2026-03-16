package com.vantu.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/v1/tool-calling")
public class ToolCallingController {
    private final ChatClient chatClient;

    public ToolCallingController(@Qualifier("toolCallingChatClient") ChatClient chatClient){
        this.chatClient = chatClient;
    }

    @GetMapping("/current-time")
    public ResponseEntity<String> getLocalTime (@RequestHeader("username") String username, @RequestParam("message") String message){
        String answer = chatClient.prompt()
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message)
                .call()
                .content();

        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(answer);
    }
}
