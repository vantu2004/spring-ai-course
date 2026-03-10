package com.vantu.springai.rag;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

/*
 * Custom DocumentRetriever dùng Tavily Web Search.
 *
 * Nhiệm vụ:
 * Query từ user
 *      ↓
 * gọi Tavily API để search web
 *      ↓
 * lấy kết quả web
 *      ↓
 * convert thành Spring AI Document
 *      ↓
 * trả về cho RAG pipeline
 */
public class WebSearchDocumentRetriever implements DocumentRetriever {
    private static final Logger logger = LoggerFactory.getLogger(WebSearchDocumentRetriever.class);

    private static final String TAVILY_API_KEY = "TAVILY_SEARCH_API_KEY";
    private static final String TAVILY_BASE_URL = "https://api.tavily.com/search";
    private static final int DEFAULT_RESULT_LIMIT = 5;
    private final int resultLimit;
    private final RestClient restClient;



    // clientBuilder : builder để tạo HTTP client
    public WebSearchDocumentRetriever(RestClient.Builder clientBuilder, int resultLimit) {
        Assert.notNull(clientBuilder, "clientBuilder cannot be null");

        String apiKey = System.getenv(TAVILY_API_KEY);
        Assert.hasText(apiKey, "Environment variable " + TAVILY_API_KEY + " must be set");

        this.restClient = clientBuilder
                .baseUrl(TAVILY_BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();

        if (resultLimit <= 0) {
            throw new IllegalArgumentException("resultLimit must be greater than 0");
        }

        this.resultLimit = resultLimit;
    }


    /*
     * Method quan trọng nhất của DocumentRetriever
     *
     * Spring AI sẽ gọi method này khi cần tìm document cho RAG
     *
     * Flow:
     * Query
     *   ↓
     * Tavily Search API
     *   ↓
     * Web Results
     *   ↓
     * convert → Document
     *   ↓
     * return List<Document>
     */
    @Override
    public List<Document> retrieve(Query query) {
        logger.info("Processing query: {}", query.text());

        Assert.notNull(query, "query cannot be null");

        String q = query.text();
        Assert.hasText(q, "query.text() cannot be empty");

        /*
         * Gửi request tới Tavily API
         *
         * body:
         * {
         *   "query": "...",
         *   "search_depth": "advanced",
         *   "max_results": 5
         * }
         *
         * response trả về ánh xạ vào record tạo bên dưới
         */
        TavilyResponsePayload response = restClient.post()
                .body(new TavilyRequestPayload(q, "advanced", resultLimit))
                .retrieve()
                .body(TavilyResponsePayload.class);

        if (response == null || CollectionUtils.isEmpty(response.results())) {
            return List.of();
        }

        List<Document> docs = new ArrayList<>(response.results().size());

        // convert từng kết quả Tavily → Spring AI Document
        for (TavilyResponsePayload.Hit hit : response.results()) {
            Document doc = Document.builder()
                    .text(hit.content())
                    .metadata("title", hit.title())
                    .metadata("url", hit.url())
                    .score(hit.score())
                    .build();

            docs.add(doc);
        }

        return docs;
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record TavilyRequestPayload(String query, String searchDepth, int maxResults) {
    }

    record TavilyResponsePayload(List<Hit> results) {
        // Mỗi kết quả tìm kiếm
        record Hit(String title, String url, String content, Double score) {
        }
    }


    /*
     * Builder pattern để tạo object dễ dàng hơn
     */
    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private RestClient.Builder clientBuilder;
        private int resultLimit = DEFAULT_RESULT_LIMIT;

        private Builder() {
        }

        public Builder restClientBuilder(RestClient.Builder clientBuilder) {
            this.clientBuilder = clientBuilder;

            return this;
        }

        public Builder maxResults(int maxResults) {
            if (maxResults <= 0) {
                throw new IllegalArgumentException("maxResults must be greater than 0");
            }

            this.resultLimit = maxResults;

            return this;
        }

        /*
         * build WebSearchDocumentRetriever
         */
        public WebSearchDocumentRetriever build() {
            return new WebSearchDocumentRetriever(clientBuilder, resultLimit);
        }
    }
}