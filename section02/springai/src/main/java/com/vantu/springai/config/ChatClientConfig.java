package com.vantu.springai.config;

import com.vantu.springai.advisors.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {
    // vì đã cấu hình bên yml nên OpenAiChatModel tự inject vào ChatClient, sau đó mới inject ChatClient vào hàm
    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        // cx có thể dùng ChatOptions thay vì dùng OpenAiChatOptions
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_5_NANO)
                // model này ko hỗ trợ maxTokens nữa
                // .maxTokens(1000)
                .temperature(1.0)
                .build();

        return chatClientBuilder
                .defaultOptions(openAiChatOptions)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), new TokenUsageAuditAdvisor()))
                .defaultSystem("""
                        You are an internal IT helpdesk assistant. Your role is to assist
                        employees with IT-related issues such as resetting passwords,
                        unlocking accounts, and answering questions related to IT policies.
                        If a user requests help with anything outside of these
                        responsibilities, respond politely and inform them that you are
                        only able to assist with IT support tasks within your defined scope.
                        """)
                .defaultUser("How can you help me?")
                .build();
    }
}
