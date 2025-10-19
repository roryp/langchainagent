# GitHub Copilot Instructions for LangChain4j Azure Project

## Project Overview
This is a multi-module Java course teaching developers how to build AI-powered applications using LangChain4j with Azure OpenAI. The project follows a progressive learning path from basic chat to production-ready RAG systems.

## Technology Stack
- **Framework**: Spring Boot 3.3.4
- **Java Version**: 21
- **LangChain4j**: 1.7.1
- **Build Tool**: Maven
- **Cloud Platform**: Azure (OpenAI, AI Search, Content Safety)
- **Vector Stores**: In-memory, Qdrant, Azure AI Search

## Code Style Guidelines

### Java Conventions
- Use Java 21 features (records, pattern matching, text blocks)
- Follow Spring Boot best practices
- Use constructor injection over field injection
- Prefer immutability where possible
- Use meaningful variable names

### Project Structure
```
module-name/
├── src/main/java/dev/rory/azure/langchain4j/
│   ├── app/           # REST controllers
│   ├── service/       # Business logic
│   ├── config/        # Configuration classes
│   ├── model/         # DTOs and domain models
│   └── exception/     # Custom exceptions
├── src/main/resources/
│   └── application.yaml
└── pom.xml
```

### Naming Conventions
- Controllers: `*Controller.java` (e.g., `ChatController`, `RagController`)
- Services: `*Service.java` (e.g., `ConversationService`, `EmbeddingService`)
- Configuration: `*Config.java` (e.g., `LangChainConfig`, `VectorStoreConfig`)
- DTOs: `*Request.java`, `*Response.java`

## LangChain4j Patterns

### 1. Chat Model Configuration
```java
// Always use builder pattern for models
AzureOpenAiChatModel model = AzureOpenAiChatModel.builder()
    .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
    .apiKey(System.getenv("AZURE_OPENAI_API_KEY"))
    .deploymentName(System.getenv("AZURE_OPENAI_DEPLOYMENT"))
    .temperature(0.2)
    .maxRetries(3)
    .logRequestsAndResponses(true)
    .build();
```

### 2. Conversation Memory
```java
// Use MessageWindowChatMemory for recent history
ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

// Use ConversationChain for stateful conversations
ConversationChain chain = ConversationChain.builder()
    .chatLanguageModel(model)
    .chatMemory(chatMemory)
    .build();
```

### 3. Prompt Templates
```java
// Use PromptTemplate for dynamic prompts
PromptTemplate template = PromptTemplate.from(
    "You are a {{role}}. Answer the question: {{question}}"
);

Prompt prompt = template.apply(Map.of(
    "role", "helpful assistant",
    "question", userInput
));
```

### 4. RAG Implementation
```java
// Document ingestion
Document document = loadDocument("file.pdf", new ApachePdfBoxDocumentParser());

// Split into chunks
DocumentSplitter splitter = DocumentSplitters.recursive(300, 30);
List<TextSegment> segments = splitter.split(document);

// Create embeddings and store
EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
EmbeddingStoreIngestor.ingest(segments, embeddingStore);

// RAG chain
ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
    .chatLanguageModel(model)
    .retriever(EmbeddingStoreRetriever.from(embeddingStore, embeddingModel))
    .chatMemory(chatMemory)
    .build();
```

### 5. Streaming Responses
```java
// Use StreamingChatLanguageModel for real-time responses
StreamingChatLanguageModel streamingModel = AzureOpenAiStreamingChatModel.builder()
    .endpoint(endpoint)
    .apiKey(apiKey)
    .deploymentName(deployment)
    .build();

// Implement StreamingResponseHandler
streamingModel.generate(prompt, new StreamingResponseHandler<AiMessage>() {
    @Override
    public void onNext(String token) {
        // Send token to client
    }
    
    @Override
    public void onComplete(Response<AiMessage> response) {
        // Finalize response
    }
});
```

### 6. AI Services (Tools/Functions)
```java
// Define tools as interfaces
interface WeatherService {
    @Tool("Get current weather for a location")
    String getCurrentWeather(@P("Location name") String location);
}

// Create AI Service
WeatherService weatherService = AiServices.builder(WeatherService.class)
    .chatLanguageModel(model)
    .tools(new WeatherTools())
    .build();
```

## REST API Design

### Request/Response Pattern
```java
// Request DTOs
public record ChatRequest(String message, String conversationId) {}

// Response DTOs
public record ChatResponse(
    String answer,
    String conversationId,
    List<String> sources,
    TokenUsage tokenUsage
) {}

// Controller methods
@PostMapping("/api/chat")
public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
    try {
        // Business logic
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(e.getMessage()));
    }
}
```

### Error Handling
- Always wrap LangChain4j calls in try-catch blocks
- Return appropriate HTTP status codes
- Include error details in response
- Log errors with context

### Health Checks
- Include `/health` endpoint in each controller
- Test Azure connectivity
- Check model availability

## Configuration Management

### Environment Variables
Required for all modules:
- `AZURE_OPENAI_ENDPOINT`: Azure OpenAI service endpoint
- `AZURE_OPENAI_API_KEY`: API key for authentication
- `AZURE_OPENAI_DEPLOYMENT`: Model deployment name

Additional for specific modules:
- `AZURE_OPENAI_EMBEDDING_DEPLOYMENT`: Embedding model deployment
- `AZURE_SEARCH_ENDPOINT`: Azure AI Search endpoint
- `AZURE_SEARCH_KEY`: Azure AI Search key

### application.yaml
```yaml
spring:
  application:
    name: langchain4j-module

langchain4j:
  azure:
    openai:
      endpoint: ${AZURE_OPENAI_ENDPOINT}
      api-key: ${AZURE_OPENAI_API_KEY}
      deployment: ${AZURE_OPENAI_DEPLOYMENT}
      temperature: 0.2
      max-tokens: 1000
```

## Testing Guidelines

### Unit Tests
- Test business logic in services
- Mock LangChain4j components
- Use `@MockBean` for Spring components

### Integration Tests
- Use `@SpringBootTest` for full context
- Test REST endpoints with `MockMvc`
- Skip tests requiring actual API keys in CI

## Documentation Standards

### README Structure
Each module should include:
1. **Overview**: What this module demonstrates
2. **Prerequisites**: Required setup and credentials
3. **Quick Start**: Step-by-step getting started
4. **Features**: List of implemented features
5. **API Reference**: Endpoint documentation
6. **Examples**: Sample requests and responses
7. **Troubleshooting**: Common issues and solutions

### Code Comments
- Add Javadoc for public classes and methods
- Explain complex LangChain4j patterns
- Document Azure-specific configurations
- Include example usage in comments

### Inline Comments
```java
/**
 * Simple REST controller exposing a chat endpoint.
 * Delegates requests to the AzureOpenAiChatModel provided by LangChain4j.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    // Implementation
}
```

## Module-Specific Guidelines

### Module 01: Getting Started
Focus on:
- Basic chat completion
- Prompt templates
- Conversation memory
- Streaming responses
- Simple error handling

### Module 02: RAG
Focus on:
- Document ingestion (PDF, TXT)
- Text splitting strategies
- Embedding generation
- Vector store integration
- Retrieval-augmented generation
- Source attribution

### Module 03: Agents & Tools
Focus on:
- Function calling
- Tool definition
- AI Services pattern
- Multi-step reasoning
- ReAct agents

### Module 04: Production
Focus on:
- Observability (logging, metrics)
- Content moderation
- Rate limiting
- Retry strategies
- Multi-model support
- Security best practices

## Azure-Specific Best Practices

### Authentication
- Use Managed Identity in production
- Store secrets in Azure Key Vault
- Never commit API keys to git

### Performance
- Implement response caching where appropriate
- Use connection pooling
- Monitor token usage
- Set appropriate timeouts

### Cost Optimization
- Use appropriate model sizes (GPT-3.5 vs GPT-4)
- Implement token limits
- Cache embeddings
- Monitor and alert on usage

## Development Workflow

### Before Committing
1. Run `mvn clean compile` to verify compilation
2. Run `mvn test` if tests exist
3. Check for sensitive data in code
4. Update README if adding new features
5. Follow conventional commit messages

### Commit Message Format
```
<type>(<scope>): <subject>

<body>
```

Types: feat, fix, docs, style, refactor, test, chore

Examples:
- `feat(chat): add conversation memory support`
- `fix(rag): resolve document chunking issue`
- `docs(readme): update API examples`

## Security Considerations

### Never Commit
- API keys or secrets
- `.env` files with real credentials
- Personal or sensitive data
- Azure subscription IDs

### Input Validation
- Validate all user inputs
- Sanitize prompts before sending
- Limit prompt length
- Implement rate limiting

### Output Filtering
- Check for PII in responses
- Implement content filtering
- Log sensitive operations
- Handle errors gracefully

## Copilot Usage Tips

When working with Copilot on this project:
1. **Context**: Always provide context about which module you're working on
2. **Patterns**: Reference these guidelines when asking for code generation
3. **Azure**: Specify Azure OpenAI when working with AI models
4. **Testing**: Request test cases along with implementation
5. **Documentation**: Ask for Javadoc and README updates

## Common Tasks

### Adding a New Endpoint
1. Create request/response DTOs
2. Implement service logic
3. Add controller method
4. Add error handling
5. Update README with API docs
6. Add tests

### Integrating a New LangChain4j Feature
1. Add dependency to pom.xml if needed
2. Create configuration class
3. Implement service wrapper
4. Add REST endpoint
5. Document usage
6. Provide examples

### Adding a New Module
1. Copy structure from existing module
2. Update parent pom.xml
3. Create README from template
4. Implement core features
5. Add Dockerfile
6. Update root README

## Resources

- [LangChain4j Documentation](https://docs.langchain4j.dev/)
- [Azure OpenAI Documentation](https://learn.microsoft.com/azure/ai-services/openai/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Project Repository](https://github.com/roryp/langchainagent)
