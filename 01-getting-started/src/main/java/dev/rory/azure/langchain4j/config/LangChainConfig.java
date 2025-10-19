package dev.rory.azure.langchain4j.config;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for LangChain4j components.
 * Provides beans for Azure OpenAI chat model and other AI services.
 */
@Configuration
public class LangChainConfig {

    @Value("${azure.openai.endpoint:${AZURE_OPENAI_ENDPOINT:}}")
    private String endpoint;

    @Value("${azure.openai.api-key:${AZURE_OPENAI_API_KEY:}}")
    private String apiKey;

    @Value("${azure.openai.deployment:${AZURE_OPENAI_DEPLOYMENT:}}")
    private String deployment;

    @Value("${azure.openai.temperature:0.2}")
    private Double temperature;

    @Value("${azure.openai.max-tokens:1000}")
    private Integer maxTokens;

    /**
     * Creates and configures the Azure OpenAI Chat Model bean.
     * This model is used for standard chat completion requests.
     *
     * @return configured AzureOpenAiChatModel instance
     */
    @Bean
    public AzureOpenAiChatModel chatModel() {
        System.out.println("Creating AzureOpenAiChatModel with endpoint: " + endpoint);
        System.out.println("API Key present: " + (apiKey != null && !apiKey.isEmpty()));
        System.out.println("Deployment: " + deployment);
        
        return AzureOpenAiChatModel.builder()
            .endpoint(endpoint)
            .apiKey(apiKey)
            .deploymentName(deployment)
            .temperature(temperature)
            .maxTokens(maxTokens)
            .maxRetries(3)
            .logRequestsAndResponses(false)
            .build();
    }
}
