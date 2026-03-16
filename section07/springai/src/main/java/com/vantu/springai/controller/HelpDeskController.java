package com.vantu.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/v1/tool-calling")
public class HelpDeskController {
    private final ChatClient chatClient;

    public HelpDeskController(@Qualifier("helpDeskChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/help-desk")
    public ResponseEntity<String> helpDesk(@RequestHeader("username") String username, @RequestParam("message") String message) {
        String answer = chatClient.prompt()
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message)
                .toolContext(Map.of("username", username))
                .call()
                .content();

        return ResponseEntity.ok(answer);
    }
}
