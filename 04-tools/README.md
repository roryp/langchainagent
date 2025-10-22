# Module 04: AI Agents with Tools

Build autonomous AI agents that can use HTTP-based tools to accomplish complex tasks using LangChain4j and Azure OpenAI.

## Features

- **ReAct Pattern** - Reasoning and Acting with multi-step tool execution
- **HTTP-Based Tools** - Weather lookup and calculator operations
- **Session Management** - Conversation memory per session
- **Tool Chaining** - Execute multiple tools in sequence
- **OpenAPI Spec** - Tools defined with OpenAPI 3.0

## Architecture (Local Development)

```

  Spring Boot App (Port 8082)      
  • AgentController             
  • AgentService (ReAct)        
  • ToolsController             

          ↓

  Azure OpenAI (gpt-5)     

          ↓

  Tools (HTTP REST)              
  • Weather (mock)               
  • Calculator                   

```

## Quick Start (Local Development)

```bash
cd 04-tools
export AZURE_OPENAI_ENDPOINT="https://aoai-xyz.openai.azure.com/"
export AZURE_OPENAI_API_KEY="***"
export AZURE_OPENAI_DEPLOYMENT="gpt-5"
export TOOLS_BASE_URL="http://localhost:8082"
mvn spring-boot:run
```

**Flow**: User message → Agent analyzes → Calls tools via HTTP → Returns answer

## Available Tools

**Weather**:
- `getCurrentWeather(location)` - Get current conditions
- `getWeatherForecast(location, days)` - Get forecast

**Calculator**:
- `add`, `subtract`, `multiply`, `divide`, `power`, `sqrt`

## Testing

```bash
# Start session
curl -X POST "http://localhost:8082/api/agent/start"

# Chat (agent will use tools automatically)
curl -X POST "http://localhost:8082/api/agent/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "What is 25 + 17?", "sessionId": "test-session"}'

# Test calculator directly
curl -X POST "http://localhost:8082/api/tools/calculator/add" \
  -H "Content-Type: application/json" \
  -d '{"a": 25, "b": 17}'
```


## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/agent/start` | Start new session |
| POST | `/api/agent/chat` | Chat with agent (auto tool execution) |
| GET | `/api/agent/tools` | List available tools |
| POST | `/api/tools/weather/current` | Get current weather (mock) |
| POST | `/api/tools/calculator/*` | Calculator operations |

**Example Chat Request:**
```json
POST /api/agent/chat
{
  "message": "What is 100 + 50?",
  "sessionId": "uuid"
}
```

**Example Response:**
```json
{ 
  "answer": "150", 
  "toolsUsed": 1 
}
```

## Implementation

**Key Components:**
- `AgentService.java` - ReAct agent with tool calling via LangChain4j
- `ToolsController.java` - REST endpoints for tools
- `openapi.yaml` - Tool definitions
- `MessageWindowChatMemory` - Session-based conversation history

**ReAct Pattern:**
1. System prompt includes tool descriptions
2. Model responds with `TOOL_CALL: toolname(params)` when needed
3. Agent executes tool via HTTP POST
4. Tool result fed back to model for final answer

**Configuration (application.yaml):**
```yaml
server:
  port: 8082
azure:
  openai:
    endpoint: ${AZURE_OPENAI_ENDPOINT}
    api-key: ${AZURE_OPENAI_API_KEY}
    deployment: ${AZURE_OPENAI_DEPLOYMENT}
  ai:
    agent:
      tools:
        base-url: ${TOOLS_BASE_URL}
```

**Key Dependencies:**
- `langchain4j-azure-open-ai` - Azure OpenAI integration
- `langchain4j` - Core framework with chat memory



**Multi-step execution:**
- Supports up to 5 iterations per request
- Agent automatically chains multiple tool calls

## Next Steps

1. **Real APIs**: Replace mock weather with OpenWeatherMap
2. **More Tools**: Add database, search, email capabilities
3. **Production**: Add auth, rate limiting, monitoring
4. **Native Function Calling**: Explore LangChain4j `@Tool` annotations

## Resources

- [LangChain4j Docs](https://docs.langchain4j.dev/)
- [Azure OpenAI](https://learn.microsoft.com/azure/ai-services/openai/)
- [ReAct Pattern](https://arxiv.org/abs/2210.03629)