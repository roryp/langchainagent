package com.example.langchain4j.rag.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for LangChain4j RAG components.
 * Provides beans for chat model, embedding model, and vector stores.
 */
@Configuration
public class LangChainRagConfig {

    @Value("${azure.openai.endpoint:#{environment.AZURE_OPENAI_ENDPOINT}}")
    private String endpoint;

    @Value("${azure.openai.api-key:#{environment.AZURE_OPENAI_API_KEY}}")
    private String apiKey;

    @Value("${azure.openai.deployment:#{environment.AZURE_OPENAI_DEPLOYMENT}}")
    private String deployment;

    @Value("${azure.openai.embedding-deployment:#{environment.AZURE_OPENAI_EMBEDDING_DEPLOYMENT}}")
    private String embeddingDeployment;

    @Value("${azure.openai.reasoning-effort:medium}")
    private String reasoningEffort;

    @Value("${azure.openai.max-completion-tokens:2000}")
    private Integer maxCompletionTokens;

    /**
     * Creates the Azure OpenAI Chat Model for answer generation.
     * GPT-5 uses reasoning effort levels instead of temperature.
     *
     * @return configured AzureOpenAiChatModel
     */
    @Bean
    public AzureOpenAiChatModel chatModel() {
        return AzureOpenAiChatModel.builder()
            .endpoint(endpoint)
            .apiKey(apiKey)
            .deploymentName(deployment)
            .maxCompletionTokens(maxCompletionTokens)
            .maxRetries(3)
            .logRequestsAndResponses(false)
            .build();
    }

    /**
     * Creates the Azure OpenAI Embedding Model for document vectorization.
     *
     * @return configured AzureOpenAiEmbeddingModel
     */
    @Bean
    public AzureOpenAiEmbeddingModel embeddingModel() {
        return AzureOpenAiEmbeddingModel.builder()
            .endpoint(endpoint)
            .apiKey(apiKey)
            .deploymentName(embeddingDeployment)
            .maxRetries(3)
            .logRequestsAndResponses(false)
            .build();
    }

    /**
     * Creates an in-memory embedding store for development.
     * This store loses all data when the application restarts.
     *
     * @return in-memory embedding store
     */
    @Bean
    @Profile({"default", "dev"})
    public EmbeddingStore<TextSegment> inMemoryEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }
}
