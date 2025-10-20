# Module 03: AI Agents with Tools

Build autonomous AI agents that can use HTTP-based tools to accomplish complex tasks using LangChain4j and Azure OpenAI.

## Features

- **ReAct Pattern** - Reasoning and Acting with multi-step tool execution
- **HTTP-Based Tools** - Weather lookup and calculator operations
- **Session Management** - Conversation memory per session
- **Tool Chaining** - Execute multiple tools in sequence
- **OpenAPI Spec** - Tools defined with OpenAPI 3.0

## Architecture

```
┌─────────────────────────────────┐
│  Container App (Port 8082)      │
│  • AgentController             │
│  • AgentService (ReAct)        │
│  • ToolsController             │
└─────────────────────────────────┘
          ↓
┌─────────────────────────────────┐
│  Azure OpenAI (gpt-4o-mini)     │
└─────────────────────────────────┘
          ↓
┌─────────────────────────────────┐
│  Tools (HTTP REST)              │
│  • Weather (mock)               │
│  • Calculator                   │
└─────────────────────────────────┘
```

**Flow**: User message → Agent analyzes → Calls tools via HTTP → Returns answer

## Available Tools

**Weather**:
- `getCurrentWeather(location)` - Get current conditions
- `getWeatherForecast(location, days)` - Get forecast

**Calculator**:
- `add`, `subtract`, `multiply`, `divide`, `power`, `sqrt`

## Quick Start

```bash
cd 01-getting-started
azd up  # Deploys all services including agents (port 8082)
```

### Test

```bash
AGENTS_URL=$(azd env get-values | grep AGENTS_APP_URL | cut -d'=' -f2 | tr -d '"')

# Start session
curl -X POST "$AGENTS_URL/api/agent/start"

# Chat (agent will use tools automatically)
curl -X POST "$AGENTS_URL/api/agent/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "What is 25 + 17?", "sessionId": "test-session"}'
```

### Run Locally

```bash
cd 03-agents-tools
export AZURE_OPENAI_ENDPOINT="https://aoai-xyz.openai.azure.com/"
export AZURE_OPENAI_API_KEY="***"
export AZURE_OPENAI_DEPLOYMENT="gpt-4o-mini"
export TOOLS_BASE_URL="http://localhost:8082"
mvn spring-boot:run
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
```http
GET /api/agent/tools
```
{ "answer": "150", "toolsUsed": 1 }
```
| Pattern | ReAct (prompt-based) | Native function calling |

### Tool Calling Implementation

**System Prompt Format:**
```
You are a helpful AI assistant with access to the following tools:

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
    key: ${AZURE_OPENAI_API_KEY}
    deployment: ${AZURE_OPENAI_DEPLOYMENT}
  tools:
    base-url: ${TOOLS_BASE_URL}
```

**Key Dependencies:**
- `langchain4j-azure-open-ai` - Azure OpenAI integration
- `langchain4j` - Core framework with chat memory

## Testing

```bash
# Run test script
cd scripts && ./test-agents.sh

# Or test manually
curl http://localhost:8082/api/agent/health
curl -X POST http://localhost:8082/api/tools/calculator/add \
  -H "Content-Type: application/json" \
  -d '{"a": 25, "b": 17}'
```

## Troubleshooting

**Agent not calling tools:**
- Verify `TOOLS_BASE_URL` is set correctly
- Test tool endpoints directly with curl
- Check system prompt includes tool descriptions

**Connection errors:**
- Local: `TOOLS_BASE_URL=http://localhost:8082`
- Azure: Use Container App URL from `azd env get-values`

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