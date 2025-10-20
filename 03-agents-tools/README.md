# Module 03: AI Agents with LangChain4j and REST-based Tools

## 🎯 Overview

This module demonstrates **AI Agent implementation** using LangChain4j with Azure OpenAI and REST-based tools. The agent uses a ReAct (Reasoning + Acting) pattern with HTTP tool calling.

**Status**: ✅ Working with LangChain4j + Azure OpenAI

## What You'll Learn

- **LangChain4j Agents**: AI agents with Azure OpenAI integration
- **ReAct Pattern**: Reasoning and Acting with tool execution loops
- **REST-based Tools**: Tools as independent HTTP services
- **OpenAPI Tool Specification**: Define tools via OpenAPI 3.0
- **Conversation Memory**: Session-based conversation management
- **HTTP Tool Calling**: Execute tools via REST API calls
- **Infrastructure as Code**: Deploy with Azure Container Apps and Bicep

## Architecture

This module uses LangChain4j with Azure OpenAI for local agent execution with HTTP-based tools:

```
┌──────────────────────────────────────────────────────────────────┐
│  Azure Container App (Port 8082)                                 │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │  Java Application (LangChain4j)                            │  │
│  │  - AgentController: HTTP endpoints for users              │  │
│  │  - AgentService: ReAct agent with tool calling            │  │
│  │  - ToolsController: Exposes tools via REST                │  │
│  └────────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────┘
                          ↓ (chat messages)
┌──────────────────────────────────────────────────────────────────┐
│  Azure OpenAI Service                                            │
│  - gpt-4o-mini Model                                             │
│  - Chat Completions API                                          │
└──────────────────────────────────────────────────────────────────┘
                          ↑ (tool calls via prompt)
                          ↓ (tool results)
┌──────────────────────────────────────────────────────────────────┐
│  Tools (REST API - Self-calling)                                 │
│  - /api/tools/weather/current                                    │
│  - /api/tools/calculator/add                                     │
│  - [defined in openapi.yaml]                                     │
└──────────────────────────────────────────────────────────────────┘
```

### How It Works

1. **User Request**: User sends message to `/api/agent/chat`
2. **Agent Reasoning**: LangChain4j agent analyzes request with tool descriptions
3. **Tool Decision**: Agent determines if tools are needed via prompt engineering
4. **Tool Execution**: Agent makes HTTP POST to tool endpoints
5. **Result Integration**: Tool results fed back to agent for final answer
6. **Response**: Final answer returned to user


## 📋 Key Components

### 1. **Agent Service (`AgentService.java`)**
- **ReAct Pattern**: Implements Reasoning + Acting loop
- **Session Management**: Manages conversation memory per session
- **Tool Calling**: Parses model responses for TOOL_CALL instructions
- **HTTP Tools**: Executes tools via REST API calls
- **LangChain4j Integration**: Uses `AzureOpenAiChatModel` and `ChatMemory`

**Pattern**: User Message → Agent Reasoning → Tool Call → Tool Result → Final Answer

**Key Code:**
```java
AzureOpenAiChatModel chatModel = AzureOpenAiChatModel.builder()
    .endpoint(endpoint)
    .apiKey(apiKey)
    .deploymentName(deployment)
    .temperature(0.2)
    .build();

// Agent reasoning with tools
String systemPrompt = buildSystemPromptWithTools();
String response = chatModel.chat(systemPrompt + userMessage);

// Parse and execute tools
if (responseContainsToolCall(response)) {
    executeToolByName(toolName, params);
}
```

### 2. **OpenAPI Specification (`openapi.yaml`)**
- Defines all tool endpoints for the Azure AI Agent
- Weather tools: `getCurrentWeather`, `getWeatherForecast`
- Calculator tools: `add`, `subtract`, `multiply`, `divide`, `power`, `sqrt`
- Served to Azure AI Agent for tool discovery

**Example Tool Definition:**
```yaml
/api/tools/calculator/add:
  post:
    operationId: add
    summary: Add two numbers
    requestBody:
      content:
        application/json:
          schema:
            properties:
              a: { type: number }
              b: { type: number }
```

### 3. **Tools REST API (`ToolsController.java`)**
- Exposes tools as HTTP endpoints
- Matches OpenAPI specification exactly
- Called by Azure AI Agent Service when tools are needed
- Returns structured JSON responses

**Example Tool Endpoint:**
```java
@PostMapping("/calculator/add")
public Map<String, Object> add(@RequestBody Map<String, Object> request) {
    double a = ((Number) request.get("a")).doubleValue();
    double b = ((Number) request.get("b")).doubleValue();
    return Map.of("result", calculatorTool.add(a, b));
}
```

### 4. **Azure Infrastructure (Bicep)**
- **Azure OpenAI**: `aoai-{resourceToken}` - GPT-4o-mini model
- **Azure AI Hub**: `hub-{resourceToken}` - AI Foundry Hub (workspace level)
- **Azure AI Project**: `proj-{resourceToken}` - AI Foundry Project with managed identity
- **Container App**: `ca-agents-{resourceToken}` - Hosts the Java application
- **Environment Variables**: OpenAI endpoint, key, deployment, tools URL

**Bicep Module (`aiservices.bicep`):**
```bicep
resource aiHub 'Microsoft.MachineLearningServices/workspaces@2024-04-01' = {
  name: 'hub-${name}'
  kind: 'Hub'
  identity: { type: 'SystemAssigned' }
}

resource aiProject 'Microsoft.MachineLearningServices/workspaces@2024-04-01' = {
  name: 'proj-${name}'
  kind: 'Project'
  properties: { hubResourceId: aiHub.id }
}
```

## 🚀 Quickstart

### Prerequisites

1. **Azure subscription** with access to Azure OpenAI
2. **Azure CLI** and **azd** (Azure Developer CLI) installed
3. **Java 21** installed
4. **Maven 3.9+** installed

### Environment Variables

Required for the Java application:

| Variable | Description | Example |
|----------|-------------|---------|
| `AZURE_OPENAI_ENDPOINT` | Azure OpenAI endpoint | `https://aoai-xyz.openai.azure.com/` |
| `AZURE_OPENAI_API_KEY` | API key for Azure OpenAI | `***` |
| `AZURE_OPENAI_DEPLOYMENT` | Model deployment name | `gpt-4o-mini` |
| `TOOLS_BASE_URL` | Base URL for tools | `https://ca-agents-xyz.azurecontainerapps.io` |

**Note:** These are automatically configured via Bicep deployment.

## Quick Start

### Deploy to Azure (Recommended)

Deploy infrastructure and services:

```bash
# Navigate to getting-started directory
cd 01-getting-started

# Deploy all infrastructure
azd up
```

This provisions:
- **Azure OpenAI**: `aoai-{token}` - gpt-4o-mini deployment
- **Container App (Agents)**: `https://ca-agents-{token}.*.azurecontainerapps.io`
- **Container App (Getting Started)**: `https://ca-{token}.*.azurecontainerapps.io`
- **Container App (RAG)**: `https://ca-rag-{token}.*.azurecontainerapps.io`
- Container Registry, Log Analytics, networking, managed identity

**Cost Optimization:** This module uses direct Azure OpenAI integration via LangChain4j. We intentionally do NOT provision Azure AI Foundry Hub/Project resources to reduce costs and complexity while maintaining full agent functionality.

### Test the Agent

```bash
# Get the agents app URL from deployment output
AGENTS_URL=$(azd env get-values | grep AGENTS_APP_URL | cut -d'=' -f2 | tr -d '"')

# Create a new agent session
curl -X POST "$AGENTS_URL/api/agent/start"

# Chat with the agent (it will use tools as needed)
curl -X POST "$AGENTS_URL/api/agent/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What is the weather in Seattle and what is 25 + 17?",
    "sessionId": "test-session"
  }'
```

### Run Locally (For Development)

**1. Set environment variables:**
```bash
export AZURE_OPENAI_ENDPOINT="https://aoai-yourtoken.openai.azure.com/"
export AZURE_OPENAI_API_KEY="your-key"
export AZURE_OPENAI_DEPLOYMENT="gpt-4o-mini"
export TOOLS_BASE_URL="http://localhost:8082"
```

**2. Build and run:**
```bash
cd 03-agents-tools
mvn clean package -DskipTests
java -jar target/agents-0.1.0.jar
```

**3. Test:**
```bash
curl -X POST http://localhost:8082/api/agent/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Calculate 100 + 50"}'
```


## 📚 API Reference

### Agent Client Endpoints (User-Facing)

These endpoints are exposed by the Java application for users to interact with the agent.

#### 1. Start Session
```http
POST /api/agent/start
```

Creates a new conversation session with memory.

**Response:**
```json
{
  "sessionId": "uuid",
  "message": "Agent session started successfully"
}
```

#### 2. Chat
```http
POST /api/agent/chat
Content-Type: application/json

{
  "message": "What is 100 + 50?",
  "sessionId": "uuid"
}
```

Conversational interface with automatic tool execution.

**Response:**
```json
{
  "answer": "The answer is 150.",
  "sessionId": "uuid",
  "toolsUsed": 0,
  "status": "completed"
}
```

**With Tool Execution:**
```json
{
  "answer": "The current weather in Seattle is 34°C and cloudy.",
  "sessionId": "uuid",
  "toolsUsed": 1,
  "status": "completed"
}
```

#### 4. List Tools
```http
GET /api/agent/tools
```

Returns all tools available to the agent (from OpenAPI spec).

**Response:**
```json
[
  "getCurrentWeather - Get current weather for a location",
  "add - Add two numbers",
  "divide - Divide two numbers"
]
```

#### 5. Clear Session
```http
DELETE /api/agent/session/{sessionId}
```

Clears conversation thread history.

#### 6. Health Check
```http
GET /api/agent/health
```

**Response:** `200 OK` with "Agent service is healthy"

### Tools Endpoints (OpenAPI - Called by Azure AI Agent)

These endpoints are called by the Azure AI Agent Service (not by users directly).

#### Weather Tools
```http
POST /api/tools/weather/current
Content-Type: application/json

{
  "location": "Seattle"
}
```

```http
POST /api/tools/weather/forecast
Content-Type: application/json

{
  "location": "Seattle",
  "days": 7
}
```

#### Calculator Tools
```http
POST /api/tools/calculator/add
POST /api/tools/calculator/subtract
POST /api/tools/calculator/multiply
POST /api/tools/calculator/divide
POST /api/tools/calculator/power
POST /api/tools/calculator/sqrt
```

**Example Request (add):**
```json
{
  "a": 25,
  "b": 17
}
```

**Example Response:**
```json
{
  "result": 42
}
```

**See `openapi.yaml` for full specification.**


## 🏗️ Implementation Details

### LangChain4j Agent Architecture

The agent uses LangChain4j's Azure OpenAI integration with a custom ReAct pattern for tool calling:

1. **System Prompt**: Includes descriptions of all available tools
2. **User Message**: Added to conversation history via ChatMemory
3. **Model Reasoning**: GPT-4o-mini generates response, optionally requesting tool execution
4. **Tool Call Detection**: Parse response for `TOOL_CALL: toolname(params)` pattern
5. **Tool Execution**: HTTP POST to tool endpoints with JSON request body
6. **Result Integration**: Feed tool result back to model for final answer

**Architecture Comparison:**

| Component | This Implementation | Alternative Approaches |
|-----------|---------------------|------------------------|
| Agent Runtime | Local (LangChain4j) | Azure AI Foundry Agent Service |
| Chat Model | `AzureOpenAiChatModel` | `ChatCompletionsClient` |
| Tool Execution | HTTP REST calls | In-process `@Tool` methods |
| Tool Discovery | System prompt | Native function calling API |
| Memory | `MessageWindowChatMemory` | Azure thread-based history |
| Pattern | ReAct (prompt-based) | Native function calling |

### Tool Calling Implementation

**System Prompt Format:**
```
You are a helpful AI assistant with access to the following tools:

1. add(a, b) - Add two numbers together
2. multiply(a, b) - Multiply two numbers
3. getCurrentWeather(location) - Get current weather for a location

To use a tool, respond with:
TOOL_CALL: toolname(param1=value1, param2=value2)
```

**Tool Execution Flow:**
```java
public AgentResponse chat(String sessionId, String userMessage) {
    // Get or create session memory
    ChatMemory memory = sessionMemories.computeIfAbsent(
        sessionId, 
        id -> MessageWindowChatMemory.withMaxMessages(20)
    );
    
    // Build context with system prompt and history
    List<ChatMessage> messages = new ArrayList<>();
    messages.add(new SystemMessage(buildSystemPromptWithTools()));
    messages.addAll(memory.messages());
    messages.add(new UserMessage(userMessage));
    
    // Get response from model
    String response = chatModel.chat(messages);
    
    // Execute tools if needed
    List<ToolExecutionInfo> toolExecutions = new ArrayList<>();
    if (responseContainsToolCall(response)) {
        response = processToolCalls(response, memory, toolExecutions, sessionId);
    }
    
    // Save to memory and return
    memory.add(new UserMessage(userMessage));
    memory.add(new AssistantMessage(response));
    
    return new AgentResponse(response, sessionId, toolExecutions.size(), "completed");
}
```

**Tool Parsing:**
```java
private static final Pattern TOOL_CALL_PATTERN = 
    Pattern.compile("TOOL_CALL:\\s*(\\w+)\\(([^)]+)\\)");

private String processToolCalls(String response, ChatMemory memory, 
                                List<ToolExecutionInfo> toolExecutions, 
                                String sessionId) {
    Matcher matcher = TOOL_CALL_PATTERN.matcher(response);
    
    while (matcher.find()) {
        String toolName = matcher.group(1);
        String params = matcher.group(2);
        
        // Execute tool via HTTP
        String result = executeToolByName(toolName, params);
        toolExecutions.add(new ToolExecutionInfo(toolName, params, result));
        
        // Get final answer from model
        memory.add(new UserMessage("Tool " + toolName + " returned: " + result));
        return chatModel.chat(memory.messages());
    }
    
    return response;
}
```

### Benefits of This Architecture

✅ **Direct OpenAI Integration**: No intermediate agent service required  
✅ **Simple Deployment**: Single Java application with LangChain4j  
✅ **Flexible Tool Execution**: Tools can be local methods or HTTP endpoints  
✅ **Session Management**: In-memory chat history with UUID-based sessions  
✅ **Transparent Reasoning**: Clear visibility into tool calls and results  
✅ **Polyglot Tools**: Tools can be any HTTP service (Python, Node, etc.)  
✅ **Cost Effective**: Pay only for OpenAI API calls, no agent service overhead  

### Files Created

#### Core Application
- **AgentsApplication.java** - Spring Boot entry point
- **AgentController.java** - User-facing REST API (`/api/agent/*`)
- **AgentService.java** - LangChain4j agent with tool calling logic
- **ToolsController.java** - Tool endpoints (`/api/tools/*`)

#### Tool Implementations
- **WeatherTool.java** - Weather data (mock)
- **CalculatorTool.java** - Math operations (add, subtract, multiply, divide)

#### DTOs
- **AgentRequest.java** - User request (message, sessionId)
- **AgentResponse.java** - Agent response (answer, toolsUsed, status)
- **ToolExecutionInfo.java** - Tool execution metadata
- **ErrorResponse.java** - Error format
- **HealthResponse.java** - Health check format

#### Infrastructure
- **aiservices.bicep** - Azure AI Foundry Hub + Project (for future use)
- **main.bicep** - Infrastructure orchestration (OpenAI + Hub + Container Apps)
- **Dockerfile** - Container image (MCR base images)
- **application.yaml** - Spring Boot configuration

### Configuration

**application.yaml:**
```yaml
server:
  port: 8082

azure:
  openai:
      endpoint: ${AZURE_AI_AGENT_ENDPOINT}
      key: ${AZURE_AI_AGENT_KEY}
      id: ${AZURE_AI_AGENT_ID}
      tools:
        base-url: ${TOOLS_BASE_URL}
```

**Note:** This uses `azure.ai.agent` namespace (NOT `langchain4j.azure.openai`).

### Dependencies (pom.xml)

```xml
<dependencies>
  <!-- Spring Boot Web -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  
  <!-- LangChain4j Azure OpenAI Integration -->
  <dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-azure-open-ai</artifactId>
    <version>${langchain4j.version}</version>
  </dependency>
  
  <!-- LangChain4j Core -->
  <dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>${langchain4j.version}</version>
  </dependency>
  
  <!-- Spring Boot Web (for REST endpoints) -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  
  <!-- Jackson for JSON processing -->
  <dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
  </dependency>
</dependencies>
```

**Key Dependencies:** Uses `langchain4j-azure-open-ai` for direct Azure OpenAI integration with chat models and memory management.


## 🧪 Testing

### Using the Test Script

```bash
cd scripts
./test-agents.sh
```

### Manual Testing Examples

**Test Health:**
```bash
curl http://localhost:8082/api/agent/health
```

**Test Tools Directly:**
```bash
# Test calculator
curl -X POST http://localhost:8082/api/tools/calculator/add \
  -H "Content-Type: application/json" \
  -d '{"a": 25, "b": 17}'

# Test weather
curl -X POST http://localhost:8082/api/tools/weather/current \
  -H "Content-Type: application/json" \
  -d '{"location": "Seattle"}'
```

**Test Agent (End-to-End):**
```bash
# Start session
SESSION=$(curl -X POST http://localhost:8082/api/agent/start | jq -r '.sessionId')

# Simple math (model answers directly, no tools needed)
curl -X POST http://localhost:8082/api/agent/chat \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"What is 100 plus 50?\",\"sessionId\":\"$SESSION\"}"

# Weather query (uses getCurrentWeather tool)
curl -X POST http://localhost:8082/api/agent/chat \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"What's the weather in Seattle?\",\"sessionId\":\"$SESSION\"}"

# Complex calculation (may use calculator tools)
curl -X POST http://localhost:8082/api/agent/chat \
  -H "Content-Type: application/json" \
  -d "{\"message\":\"What is 47.5 multiplied by 132.7?\",\"sessionId\":\"$SESSION\"}"
```

**List Available Tools:**
```bash
curl http://localhost:8082/api/agent/tools
```

## 🔍 Troubleshooting

### "endpoint cannot be null or blank"
Ensure `AZURE_AI_AGENT_ENDPOINT` environment variable is set:
```bash
export AZURE_AI_AGENT_ENDPOINT="https://ai-yourtoken.cognitiveservices.azure.com"
```

### "Connection refused" when agent calls tools
Check `TOOLS_BASE_URL`:
- **Local**: Should be `http://localhost:8082`
- **Azure**: Should be the Container App URL (e.g., `https://ca-agents-xyz.azurecontainerapps.io`)

### "Tool not found"
### Agent Not Calling Tools
Check:
1. System prompt includes tool descriptions (`buildSystemPromptWithTools()`)
2. Tool endpoints are accessible (test with direct curl)
3. Response parsing regex is matching tool calls
4. Environment variable `TOOLS_BASE_URL` is set correctly

### Configuration Errors
```bash
# Check environment variables in deployed container
az containerapp exec --name ca-agents-XXXXX --resource-group rg-XXXXX \
  --command "env | grep AZURE"

# Verify application.yaml properties match env vars
# Should use: azure.openai.endpoint, azure.openai.key, azure.openai.deployment
```

### Multi-Step Tool Execution ✅
**Status**: Working! The agent now supports iterative tool execution for complex multi-step tasks.

**Implementation**:
```java
// processToolCalls() with iteration support
int maxIterations = 5;
int iteration = 0;
String currentResponse = response;

while (responseContainsToolCall(currentResponse) && iteration < maxIterations) {
    iteration++;
    // Execute all tools in current response
    // Feed results back to model
    // Get new response (may contain more tool calls)
}
```

**Test Results**:
- ✅ Single-step: Weather lookup, simple calculations
- ✅ Multi-step: Get weather in two cities, then multiply temperatures
- ✅ Complex chains: Weather + calculation + division sequences
- ✅ Max iterations: Prevents infinite loops with 5 iteration limit

**Example**: "Get weather in Paris and London, then multiply their temperatures"
- Tool 1: `getCurrentWeather(Paris)` → 26°C
- Tool 2: `getCurrentWeather(London)` → 23°C  
- Tool 3: `multiply(26, 23)` → 598
- Result: "The result is 598" (3 tools used)


## 🎓 Next Steps

### 1. Add Real Tool Implementations
Replace mock data with actual APIs:
```java
// In WeatherTool.java
public String getCurrentWeather(String location) {
    // Call OpenWeatherMap API
    RestTemplate restTemplate = new RestTemplate();
    String apiKey = System.getenv("OPENWEATHER_API_KEY");
    String url = String.format(
        "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s",
        location, apiKey
    );
    WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
    return formatWeatherResponse(response);
}
```

### 2. Implement Additional Tools
Expand agent capabilities with more tools:
- **Database Query**: Execute SQL queries on Azure SQL
- **Document Search**: Connect to Module 02 RAG service
- **Email**: Send emails via Azure Communication Services  
- **Web Search**: Bing Search API integration
- **Image Analysis**: Azure Computer Vision integration

### 3. Production Enhancements
- **Authentication**: Add Azure AD authentication to endpoints
- **Rate Limiting**: Prevent abuse with throttling
- **Caching**: Cache tool results with Redis
- **Monitoring**: Application Insights for observability
- **Error Recovery**: Retry logic with exponential backoff

### 4. Consider Native Function Calling
Research LangChain4j native function calling as alternative to prompt-based approach:
```java
// Alternative: Use LangChain4j @Tool annotations
interface CalculatorTools {
    @Tool("Add two numbers")
    double add(double a, double b);
}

// Generate with tool specifications
AiServices.builder(CalculatorTools.class)
    .chatLanguageModel(chatModel)
    .tools(new CalculatorToolsImpl())
    .build();
```

## 📖 Resources

- [LangChain4j Documentation](https://docs.langchain4j.dev/)
- [Azure OpenAI Service](https://learn.microsoft.com/azure/ai-services/openai/)
- [Azure AI Foundry](https://learn.microsoft.com/azure/ai-studio/)
- [ReAct Pattern Paper](https://arxiv.org/abs/2210.03629)
- [Function Calling Best Practices](https://learn.microsoft.com/azure/ai-services/openai/how-to/function-calling)
- [Azure Container Apps](https://learn.microsoft.com/azure/container-apps/)

## 🎯 What You Learned

This module demonstrated:
- ✅ **LangChain4j Azure Integration** - Direct Azure OpenAI usage with chat models
- ✅ **ReAct Pattern** - Reasoning + Acting with prompt-based tool calling
- ✅ **Multi-Step Tool Execution** - Iterative tool calling for complex tasks
- ✅ **Session Management** - UUID-based sessions with MessageWindowChatMemory
- ✅ **HTTP Tool Execution** - REST endpoints for polyglot tool implementations
- ✅ **Azure AI Foundry Infrastructure** - Hub + Project for future capabilities
- ✅ **Custom Tool Calling** - Parse and execute tools from model responses
- ✅ **Production Deployment** - Container Apps with managed identity

**Architecture Comparison:**
| Aspect | This Implementation | Alternative Approaches |
|--------|---------------------|------------------------|
| Runtime | Local LangChain4j | Azure AI Foundry Agent Service |
| Tools | HTTP REST endpoints | In-process `@Tool` methods |
| Execution | `AiServices.create()` | `ChatCompletionsClient` |
| Scaling | Application scaling | Agent service scaling |
| Language | Java only | Any (HTTP-based) |

## License

MIT License - See root LICENSE file