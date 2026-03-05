package com.vantu.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatMemoryChatClientConfig {
    // phải thêm tên để đảm bảo inject đúng bean vì có bean đã trả về cùng ChatClient rồi
    @Bean(name = "chatMemoryChatClient")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_5_NANO)
                .temperature(1.0)
                .build();

        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return chatClientBuilder
                .defaultOptions(openAiChatOptions)
                .defaultAdvisors(List.of(loggerAdvisor, memoryAdvisor))
                .build();
    }
}
