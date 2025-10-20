# GitHub Copilot Instructions for Module 03: Agents & Tools

## Module Overview
This module demonstrates AI agents with tool-calling capabilities using LangChain4j and Azure OpenAI. Agents can autonomously use tools to complete complex tasks requiring multiple steps, external data, or calculations.

## Key Technologies
- **LangChain4j**: 1.7.1 with agent and tool support
- **Azure OpenAI**: Function calling with gpt-4o-mini
- **Spring Boot**: 3.3.4
- **Java**: 21

## Code Patterns

### 1. Tool Definition
Tools are defined as Spring components with `@Tool` annotation:

```java
@Component
public class WeatherTool {
    @Tool("Get the current weather for a given location")
    public String getCurrentWeather(String location) {
        // Tool implementation
        return result;
    }
}
```

**Best Practices:**
- Use clear, descriptive tool names
- Provide detailed `@Tool` descriptions for the AI to understand
- Validate input parameters
- Return informative, structured results
- Log tool executions for debugging

### 2. Agent Service Pattern
```java
@Service
public class AgentService {
    private final AzureOpenAiChatModel chatModel;
    private final ConcurrentHashMap<String, ChatMemory> agentMemories;
    
    // Tool injection via constructor
    public AgentService(AzureOpenAiChatModel chatModel,
                       WeatherTool weatherTool,
                       CalculatorTool calculatorTool) {
        this.chatModel = chatModel;
        this.weatherTool = weatherTool;
        this.calculatorTool = calculatorTool;
    }
}
```

### 3. Session Management
```java
// Create session with memory
String sessionId = UUID.randomUUID().toString();
ChatMemory memory = MessageWindowChatMemory.withMaxMessages(20);
agentMemories.put(sessionId, memory);
```

### 4. Chat with Memory
```java
// Add user message
UserMessage userMessage = UserMessage.from(request.message());
memory.add(userMessage);

// Generate response
String responseText = chatModel.chat(request.message());

// Add AI response to memory
AiMessage aiMessage = AiMessage.from(responseText);
memory.add(aiMessage);
```

## REST API Design

### Request/Response DTOs
```java
// Request
public record AgentRequest(
    String message,
    String sessionId,
    boolean enableTools
) {}

// Response
public record AgentResponse(
    String answer,
    String sessionId,
    List<ToolExecutionInfo> toolExecutions,
    String status
) {}
```

### Controller Endpoints
- `POST /api/agent/start` - Create new session
- `POST /api/agent/execute` - Execute task with tools
- `POST /api/agent/chat` - Conversational interface
- `GET /api/agent/tools` - List available tools
- `DELETE /api/agent/session/{id}` - Clear session
- `GET /api/agent/health` - Health check

## Adding New Tools

### Step 1: Create Tool Class
```java
@Component
public class EmailTool {
    @Tool("Send an email to a recipient")
    public String sendEmail(
        @P("Recipient email address") String to,
        @P("Email subject") String subject,
        @P("Email body content") String body
    ) {
        // Validate inputs
        if (to == null || !to.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }
        
        // Implementation
        // ...
        
        return "Email sent successfully to " + to;
    }
}
```

### Step 2: Inject into AgentService
```java
private final EmailTool emailTool;

public AgentService(..., EmailTool emailTool) {
    this.emailTool = emailTool;
}
```

### Step 3: Register Tool
Add to toolSpecifications list:
```java
this.toolSpecifications.addAll(
    ToolSpecifications.toolSpecificationsFrom(emailTool)
);
```

## Configuration

### application.yaml
```yaml
spring:
  application:
    name: langchain4j-agents

server:
  port: 8082

azure:
  openai:
    endpoint: ${AZURE_OPENAI_ENDPOINT}
    api-key: ${AZURE_OPENAI_API_KEY}
    deployment: ${AZURE_OPENAI_DEPLOYMENT:gpt-4o-mini}
    temperature: 0.2
    max-tokens: 2000
```

### Environment Variables
- `AZURE_OPENAI_ENDPOINT`: Azure OpenAI service endpoint
- `AZURE_OPENAI_API_KEY`: API key
- `AZURE_OPENAI_DEPLOYMENT`: Model deployment name (must support function calling)

## Error Handling

### Tool Execution Errors
```java
@Tool("Divide two numbers")
public double divide(double a, double b) {
    if (b == 0) {
        throw new IllegalArgumentException("Cannot divide by zero");
    }
    return a / b;
}
```

### Service-Level Error Handling
```java
try {
    // Tool execution
    String response = chatModel.chat(request.message());
    return new AgentResponse(response, sessionId, executions, "completed");
} catch (Exception e) {
    log.error("Agent task execution failed", e);
    return new AgentResponse(
        "Error: " + e.getMessage(),
        sessionId,
        new ArrayList<>(),
        "failed"
    );
}
```

## Testing

### Unit Test Example
```java
@Test
void testWeatherTool() {
    WeatherTool tool = new WeatherTool();
    String result = tool.getCurrentWeather("Seattle");
    assertNotNull(result);
    assertTrue(result.contains("Seattle"));
}
```

### Integration Test
```bash
# Start session
SESSION_ID=$(curl -s -X POST http://localhost:8082/api/agent/start | jq -r '.sessionId')

# Execute task
curl -X POST http://localhost:8082/api/agent/execute \
  -H "Content-Type: application/json" \
  -d "{\"message\": \"What is 10 + 5?\", \"sessionId\": \"$SESSION_ID\"}"
```

## Common Issues & Solutions

### 1. Function Calling Not Working
**Problem:** Model doesn't call tools  
**Solution:** 
- Ensure model supports function calling (gpt-4, gpt-4o-mini, gpt-35-turbo-1106+)
- Check tool descriptions are clear
- Verify tools are properly registered

### 2. Session Management
**Problem:** Lost conversation context  
**Solution:**
- Store session IDs on client side
- Implement session expiration cleanup
- Use ConcurrentHashMap for thread safety

### 3. Tool Parameter Extraction
**Problem:** AI can't extract correct parameters  
**Solution:**
- Use descriptive parameter names
- Add `@P` annotations with clear descriptions
- Validate and sanitize inputs

## Security Best Practices

### Input Validation
```java
@Tool("Execute database query")
public String executeQuery(String query) {
    // Sanitize input
    if (query.toLowerCase().contains("drop") || 
        query.toLowerCase().contains("delete")) {
        throw new SecurityException("Dangerous operation not allowed");
    }
    
    // Execute safe query
    return result;
}
```

### Rate Limiting
```java
@Component
public class RateLimiter {
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    
    public boolean allowRequest(String sessionId) {
        int count = requestCounts.computeIfAbsent(
            sessionId, 
            k -> new AtomicInteger(0)
        ).incrementAndGet();
        return count <= MAX_REQUESTS_PER_MINUTE;
    }
}
```

### Sensitive Operations
- Never expose destructive operations without confirmation
- Implement audit logging for all tool executions
- Use authentication/authorization for production tools

## Performance Optimization

### Caching Tool Results
```java
private final Map<String, CachedResult> cache = new ConcurrentHashMap<>();

public String getCachedWeather(String location) {
    CachedResult cached = cache.get(location);
    if (cached != null && !cached.isExpired()) {
        return cached.result;
    }
    
    String result = fetchWeather(location);
    cache.put(location, new CachedResult(result, Duration.ofMinutes(10)));
    return result;
}
```

### Async Tool Execution
```java
@Async
public CompletableFuture<String> executeSlowTool(String param) {
    // Long-running operation
    return CompletableFuture.completedFuture(result);
}
```

## Deployment

### Docker Build
```bash
cd 03-agents-tools
docker build -t agents-tools:latest -f Dockerfile ..
```

### Azure Deployment
```bash
cd 01-getting-started
azd up  # Deploys all three modules together
```

### Environment-Specific Configuration
```yaml
# development
spring:
  profiles: dev
logging:
  level:
    dev.rory.azure.langchain4j: DEBUG

# production
spring:
  profiles: prod
logging:
  level:
    dev.rory.azure.langchain4j: INFO
```

## Monitoring & Observability

### Tool Execution Logging
```java
@Tool("Send email")
public String sendEmail(String to, String subject) {
    long startTime = System.currentTimeMillis();
    try {
        // Execute tool
        String result = doSendEmail(to, subject);
        
        // Log successful execution
        long duration = System.currentTimeMillis() - startTime;
        log.info("Tool executed: sendEmail, duration={}ms, to={}", duration, to);
        
        return result;
    } catch (Exception e) {
        log.error("Tool execution failed: sendEmail, to={}", to, e);
        throw e;
    }
}
```

### Metrics Collection
```java
@Timed(value = "agent.task.execution")
public AgentResponse executeTask(AgentRequest request) {
    // Implementation
}
```

## Resources

- [LangChain4j Agents](https://docs.langchain4j.dev/tutorials/agents)
- [Azure OpenAI Function Calling](https://learn.microsoft.com/azure/ai-services/openai/how-to/function-calling)
- [Tool Design Best Practices](https://docs.langchain4j.dev/tutorials/tools)

## Development Workflow

When adding new features:
1. Define tool interface with clear descriptions
2. Implement tool logic with validation
3. Add unit tests for tool
4. Register tool in AgentService
5. Test with integration tests
6. Update API documentation
7. Deploy and monitor

## Copilot Tips

When working on this module:
- "Add a new tool for [functionality]" - Copilot will create tool class with proper annotations
- "Test the agent with [scenario]" - Copilot will generate test cases
- "Add error handling for [case]" - Copilot will add appropriate try-catch blocks
- "Create endpoint for [operation]" - Copilot will generate REST controller method

Always reference these patterns for consistency across the module.
