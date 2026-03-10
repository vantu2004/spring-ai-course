package com.vantu.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/v1/rag")
public class RagController {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagController(@Qualifier("chatMemoryChatClient") ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @Value("classpath:/promptTemplates/systemPromptRandomDataTemplate.st")
    Resource systemPromptRandomDataTemplate;

    @Value("classpath:/promptTemplates/systemPromptDocumentTemplate.st")
    Resource systemPromptDocumentTemplate;

    @GetMapping("/random/chat")
    public ResponseEntity<String> randomChat(@RequestHeader("username") String username, @RequestParam("message") String message) {
//        SearchRequest searchRequest = SearchRequest.builder().query(message).topK(3).similarityThreshold(0.5).build();
//
//        List<Document> documents = vectorStore.similaritySearch(searchRequest);
//        String similarContext = documents.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));

        String response = chatClient.prompt()
//                .system(promptSystemSpec -> promptSystemSpec.text(systemPromptRandomDataTemplate).param("documents", similarContext))
                .advisors(
                        advisorSpec -> advisorSpec.param(CONVERSATION_ID, username)
                )
                .user(message)
                .call()
                .content();

        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(response);
    }

    @GetMapping("/document/chat")
    public ResponseEntity<String> documentChat(@RequestHeader("username") String username, @RequestParam("message") String message) {
//        SearchRequest searchRequest = SearchRequest.builder().query(message).topK(3).similarityThreshold(0.5).build();
//
//        List<Document> documents = vectorStore.similaritySearch(searchRequest);
//        String similarContext = documents.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));

        String response = chatClient.prompt()
//                .system(promptSystemSpec -> promptSystemSpec.text(systemPromptDocumentTemplate).param("documents", similarContext))
                .advisors(
                        advisorSpec -> advisorSpec.param(CONVERSATION_ID, username)
                )
                .user(message)
                .call()
                .content();

        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(response);
    }
}
