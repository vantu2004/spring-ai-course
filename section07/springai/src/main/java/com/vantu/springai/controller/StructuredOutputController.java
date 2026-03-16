package com.vantu.springai.controller;

import com.vantu.springai.model.CountryCities;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/structure/countries")
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

    @GetMapping("/cities")
    public ResponseEntity<CountryCities> generateCountryWithCities() {
        CountryCities countryCities = this.chatClient.prompt()
                .system("Provide one real country and 5 major cities.")
                .call()
                .entity(CountryCities.class);

        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(countryCities);
    }

    @GetMapping
    public ResponseEntity<List<String>> generateCountryList() {
        List<String> countries = this.chatClient.prompt()
                .system("Provide 5 real countries.")
                .call()
                // tương tự với Map...
                .entity(new ListOutputConverter());

        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(countries);
    }

    @GetMapping("/country-details")
    public ResponseEntity<List<CountryCities>> generateCountryDetails() {

        List<CountryCities> countries = this.chatClient.prompt()
                .system("Provide 3 real countries with some their cities.")
                .call()
                .entity(new ParameterizedTypeReference<List<CountryCities>>() {});

        return ResponseEntity.ok(countries);
    }
}
