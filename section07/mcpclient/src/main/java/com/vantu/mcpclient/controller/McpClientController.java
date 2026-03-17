package com.vantu.mcpclient.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mcp-client")
public class McpClientController {
    private final ChatClient chatClient;

    // ToolCallbackProvider thực chất là một registry chứa danh sách tool, tất cả tool trong provider sẽ được register cho ChatClient.
    public McpClientController(ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider){
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("message") String message){
        return chatClient.prompt().user(message).call().content();
    }
}
