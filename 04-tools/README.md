# Module 04: AI Agents with Tools

Build autonomous AI agents that can use HTTP-based tools to accomplish complex tasks using LangChain4j and Azure OpenAI.

## Features

- **ReAct Pattern** - Reasoning and Acting with multi-step tool execution
- **HTTP-Based Tools** - Weather lookup and temperature conversions
- **Session Management** - Conversation memory per session
- **Tool Chaining** - Execute multiple tools in sequence
- **OpenAPI Spec** - Tools defined with OpenAPI 3.0

## Architecture (Local Development)

```

  Spring Boot App (Port 8084)      
  • AgentController             
  • AgentService (ReAct)        
  • ToolsController             

          ↓

  Azure OpenAI (gpt-5)     

          ↓

  Tools (HTTP REST)              
  • Weather (mock)               
  • Temperature Conversion                   

```

## Quick Start (Local Development)

### Option 1: Using the Web UI (Recommended)

```bash
cd 04-tools
./start.sh
```

Then open your browser to: **http://localhost:8084**

### Option 2: Manual Setup

```bash
cd 04-tools
export AZURE_OPENAI_ENDPOINT="https://aoai-xyz.openai.azure.com/"
export AZURE_OPENAI_API_KEY="***"
export AZURE_OPENAI_DEPLOYMENT="gpt-5"
export TOOLS_BASE_URL="http://localhost:8084"
mvn spring-boot:run
```

**Flow**: User message → Agent analyzes → Calls tools via HTTP → Returns answer

## Web Interface

The app includes a modern web UI at `http://localhost:8084` with:
- **Quick Examples** - Pre-built queries to test different tools
- **Interactive Chat** - Natural language interface to the agent
- **Real-time Tool Execution** - See which tools are being called
- **Session Management** - Persistent conversation history

## Available Tools

**Weather**:
- `getCurrentWeather(location)` - Get current conditions
- `getWeatherForecast(location, days)` - Get forecast

**Temperature Conversion**:
- `celsiusToFahrenheit`, `fahrenheitToCelsius`, `celsiusToKelvin`, `kelvinToCelsius`, `fahrenheitToKelvin`, `kelvinToFahrenheit`

## Testing

### Web UI (Easiest)
Open http://localhost:8084 and try:
- "Convert 100°F to Celsius"
- "What's the weather in Seattle?"
- "What is 25°C in Fahrenheit?"

### API Endpoints

```bash
# Start session
curl -X POST "http://localhost:8084/api/agent/start"

# Chat (agent will use tools automatically)
curl -X POST "http://localhost:8084/api/agent/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Convert 100°F to Celsius", "sessionId": "test-session"}'

# Test temperature conversion directly
curl -X POST "http://localhost:8084/api/tools/temperature/fahrenheit-to-celsius" \
  -H "Content-Type: application/json" \
  -d '{"fahrenheit": 100}'
```


## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/agent/start` | Start new session |
| POST | `/api/agent/chat` | Chat with agent (auto tool execution) |
| GET | `/api/agent/tools` | List available tools |
| POST | `/api/tools/weather/current` | Get current weather (mock) |
| POST | `/api/tools/temperature/*` | Temperature conversions |

**Example Chat Request:**
```json
POST /api/agent/chat
{
  "message": "Convert 100°F to Celsius",
  "sessionId": "uuid"
}
```

**Example Response:**
```json
{ 
  "answer": "37.78°C", 
  "toolsUsed": 1 
}
```

## Implementation

**Key Components:**
- `AgentService.java` - ReAct agent with tool calling via LangChain4j
- `ToolsController.java` - REST endpoints for tools
- `MessageWindowChatMemory` - Session-based conversation history

**ReAct Pattern:**
1. System prompt includes tool descriptions
2. Model responds with `TOOL_CALL: toolname(params)` when needed
3. Agent executes tool via HTTP POST
4. Tool result fed back to model for final answer

**Configuration (application.yaml):**
```yaml
server:
  port: 8084
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

**Next Module:** [05-mcp - Model Context Protocol (MCP)](../05-mcp/README.md)

1. **Real APIs**: Replace mock weather with OpenWeatherMap
2. **More Tools**: Add database, search, email capabilities
3. **Production**: Add auth, rate limiting, monitoring
4. **Native Function Calling**: Explore LangChain4j `@Tool` annotations

## Resources

- [LangChain4j Docs](https://docs.langchain4j.dev/)
- [Azure OpenAI](https://learn.microsoft.com/azure/ai-services/openai/)
- [ReAct Pattern](https://arxiv.org/abs/2210.03629)