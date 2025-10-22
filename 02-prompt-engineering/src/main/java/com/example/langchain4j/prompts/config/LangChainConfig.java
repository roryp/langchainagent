package com.example.langchain4j.prompts.config;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for LangChain4j with GPT-5 support.
 * 
 * GPT-5 uses reasoning effort levels (low, medium, high) instead of temperature.
 * This controls the depth of reasoning the model applies to responses.
 */
@Configuration
public class LangChainConfig {

    @Value("${azure.openai.endpoint}")
    private String azureEndpoint;

    @Value("${azure.openai.api-key}")
    private String azureApiKey;

    @Value("${azure.openai.deployment:gpt-5}")
    private String deploymentName;

    @Value("${azure.openai.reasoning-effort:medium}")
    private String reasoningEffort;

    @Value("${azure.openai.max-completion-tokens:2000}")
    private Integer maxCompletionTokens;

    @Bean
    @Primary
    public AzureOpenAiChatModel chatLanguageModel() {
        return AzureOpenAiChatModel.builder()
                .endpoint(azureEndpoint)
                .apiKey(azureApiKey)
                .deploymentName(deploymentName)
                .maxCompletionTokens(maxCompletionTokens)
                .logRequestsAndResponses(true)
                .build();
    }

    /**
     * Optional: Bean for low reasoning effort (faster, less thorough)
     * Use this for simple tasks requiring quick responses
     */
    @Bean("quickModel")
    public AzureOpenAiChatModel quickChatModel() {
        return AzureOpenAiChatModel.builder()
                .endpoint(azureEndpoint)
                .apiKey(azureApiKey)
                .deploymentName(deploymentName)
                .maxCompletionTokens(maxCompletionTokens)
                .logRequestsAndResponses(true)
                .build();
    }

    /**
     * Optional: Bean for high reasoning effort (slower, more thorough)
     * Use this for complex tasks requiring deep reasoning
     */
    @Bean("thoroughModel")
    public AzureOpenAiChatModel thoroughChatModel() {
        return AzureOpenAiChatModel.builder()
                .endpoint(azureEndpoint)
                .apiKey(azureApiKey)
                .deploymentName(deploymentName)
                .maxCompletionTokens(maxCompletionTokens)
                .logRequestsAndResponses(true)
                .build();
    }
}
