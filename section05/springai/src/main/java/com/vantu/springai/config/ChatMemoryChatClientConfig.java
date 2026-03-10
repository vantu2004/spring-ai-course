package com.vantu.springai.config;

import com.vantu.springai.advisors.TokenUsageAuditAdvisor;
import com.vantu.springai.rag.PIIMaskingDocumentPostProcessor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatMemoryChatClientConfig {
    @Bean
    ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder().maxMessages(10).chatMemoryRepository(jdbcChatMemoryRepository).build();
    }

    // phải thêm tên để đảm bảo inject đúng bean vì có bean đã trả về cùng ChatClient rồi
    @Bean(name = "chatMemoryChatClient")
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_5_NANO)
                .temperature(1.0)
                .build();

        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();

        return chatClientBuilder
                .defaultOptions(openAiChatOptions)
                .defaultAdvisors(List.of(loggerAdvisor, memoryAdvisor, tokenUsageAdvisor, retrievalAugmentationAdvisor))
                .build();
    }

    // thay vì cấu hình trực tiếp trong api thì cấu hình như này để dùng chung
    @Bean
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        return RetrievalAugmentationAdvisor.builder()
                // advanced RAG with pre-retrieval - tối ưu query
                .queryTransformers(
                        /*
                         *  có 3 loại queryTransformers
                         *  - CompressionQueryTransformer: Rút gọn câu query để loại bỏ phần dư thừa và giữ lại từ khóa quan trọng.
                         *  - RewriteQueryTransformer: Viết lại câu query cho rõ nghĩa hơn để retriever hiểu đúng ý.
                         *  - TranslationQueryTransformer: Dịch query sang ngôn ngữ của document store.
                         * */
                        TranslationQueryTransformer
                                .builder()
                                .chatClientBuilder(chatClientBuilder.clone())
                                .targetLanguage("english")
                                .build())
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build())
                // advanced RAG with post-retrieval - tối ưu document
                // tự triển khai PIIMaskingDocumentPostProcessor (triển khai DocumentPostProcessors) để ẩn thông tin quan trọng
                .documentPostProcessors(PIIMaskingDocumentPostProcessor.builder())
                .build();
    }
}
