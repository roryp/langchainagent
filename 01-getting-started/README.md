# Module 01: Getting Started with LangChain4j

## Overview
This module demonstrates the core features of LangChain4j with Azure OpenAI, including:
- ✅ Basic chat completion (stateless)
- ✅ Conversational chat with memory (stateful)
- ✅ Spring Boot integration
- ✅ Configuration management
- ✅ Error handling

## Prerequisites

### Required Software
- Java 21 or higher
- Maven 3.8+
- Git

### Azure OpenAI Setup
You'll need an Azure OpenAI resource with:
- A deployed chat model (e.g., gpt-4, gpt-3.5-turbo)
- API endpoint
- API key

## Quick Start

### 1. Configure Environment Variables

Create a `.env` file in the project root:

```bash
cp .env.example .env
```

Edit `.env` and fill in your Azure OpenAI credentials:

```bash
AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"
AZURE_OPENAI_API_KEY="your-api-key-here"
AZURE_OPENAI_DEPLOYMENT="your-deployment-name"  # e.g., gpt-4
```

### 2. Build the Project

```bash
cd 01-getting-started
mvn clean compile
```

### 3. Run the Application

From the project root:

```bash
# Source environment variables and run
source .env
cd 01-getting-started
mvn spring-boot:run
```

Or use the provided script:

```bash
./scripts/run-getting-started.sh
```

The application will start on `http://localhost:8080`

## Features & API Endpoints

### 1. Basic Chat (Stateless)

Simple one-off chat interactions without conversation history.

**Endpoint:** `POST /api/chat`

**Request:**
```json
{
  "message": "What is LangChain4j?"
}
```

**Response:**
```json
{
  "prompt": "What is LangChain4j?",
  "answer": "LangChain4j is a Java library that simplifies..."
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H 'Content-Type: application/json' \
  -d '{"message":"Explain Azure OpenAI in one sentence"}'
```

### 2. Conversational Chat (Stateful)

Multi-turn conversations with context retention.

#### Start a New Conversation

**Endpoint:** `POST /api/conversation/start`

**Response:**
```json
{
  "conversationId": "550e8400-e29b-41d4-a716-446655440000",
  "message": "Conversation started successfully"
}
```

**Example:**
```bash
curl -X POST http://localhost:8080/api/conversation/start
```

#### Send a Message

**Endpoint:** `POST /api/conversation/chat`

**Request:**
```json
{
  "conversationId": "550e8400-e29b-41d4-a716-446655440000",
  "message": "My name is John"
}
```

**Response:**
```json
{
  "conversationId": "550e8400-e29b-41d4-a716-446655440000",
  "message": "My name is John",
  "answer": "Nice to meet you, John! How can I help you today?"
}
```

**Example:**
```bash
# First message
curl -X POST http://localhost:8080/api/conversation/chat \
  -H 'Content-Type: application/json' \
  -d '{"conversationId":"YOUR_ID","message":"My name is John"}'

# Follow-up message - the model remembers your name!
curl -X POST http://localhost:8080/api/conversation/chat \
  -H 'Content-Type: application/json' \
  -d '{"conversationId":"YOUR_ID","message":"What is my name?"}'
```

#### Get Conversation History

**Endpoint:** `GET /api/conversation/{conversationId}/history`

**Response:**
```json
{
  "conversationId": "550e8400-e29b-41d4-a716-446655440000",
  "messageCount": 4,
  "messages": [
    { "type": "USER", "text": "My name is John" },
    { "type": "AI", "text": "Nice to meet you, John!" },
    ...
  ]
}
```

#### Clear a Conversation

**Endpoint:** `DELETE /api/conversation/{conversationId}`

**Response:**
```json
{
  "message": "Conversation cleared successfully",
  "conversationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 3. Health Checks

**Endpoints:**
- `GET /api/chat/health`
- `GET /api/conversation/health`

**Response:**
```json
{
  "status": "ok"
}
```

## Architecture

### Project Structure

```
01-getting-started/
├── src/main/java/dev/rory/azure/langchain4j/
│   ├── app/
│   │   ├── Application.java              # Spring Boot main class
│   │   ├── ChatController.java           # Stateless chat endpoint
│   │   └── ConversationController.java   # Stateful conversation endpoint
│   ├── service/
│   │   └── ConversationService.java      # Conversation memory logic
│   ├── config/
│   │   └── LangChainConfig.java          # LangChain4j bean configuration
│   └── model/
│       ├── ChatRequest.java              # Request DTOs
│       └── ChatResponse.java             # Response DTOs
├── src/main/resources/
│   └── application.yaml                  # Application configuration
└── pom.xml                               # Maven dependencies
```

### Key Components

#### 1. LangChainConfig
Configures the Azure OpenAI chat model as a Spring bean:
- Reads configuration from environment variables or application.yaml
- Sets up temperature, max tokens, retry logic
- Provides the model to controllers via dependency injection

#### 2. ChatController
Handles stateless chat requests:
- No conversation memory
- Each request is independent
- Simple request/response pattern

#### 3. ConversationService
Manages conversation state:
- Maintains separate memory for each conversation ID
- Uses `MessageWindowChatMemory` to store recent messages
- Configurable message window size (default: 10 messages)

#### 4. ConversationController
Handles stateful conversations:
- Manages conversation lifecycle (start, chat, history, clear)
- Provides conversation context to the model
- Returns conversation-aware responses

## Configuration

### Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `AZURE_OPENAI_ENDPOINT` | Azure OpenAI endpoint URL | `https://your-resource.openai.azure.com/` |
| `AZURE_OPENAI_API_KEY` | Azure OpenAI API key | `abc123...` |
| `AZURE_OPENAI_DEPLOYMENT` | Model deployment name | `gpt-4` or `gpt-35-turbo` |

### Application Properties

Configure in `application.yaml`:

```yaml
azure:
  openai:
    temperature: 0.2          # Lower = more focused, Higher = more creative
    max-tokens: 1000          # Maximum tokens in response
```

## Common Use Cases

### 1. Simple Q&A Bot
Use the stateless `/api/chat` endpoint for independent questions.

### 2. Customer Support Agent
Use the stateful `/api/conversation/*` endpoints to maintain context across questions.

### 3. Interactive Tutorial
Start a conversation and guide users through multi-step processes while maintaining context.

## Troubleshooting

### "endpoint cannot be null or blank"
**Problem:** Azure OpenAI credentials not configured.

**Solution:** 
1. Create `.env` file with your credentials
2. Source the file: `source .env`
3. Verify variables: `echo $AZURE_OPENAI_ENDPOINT`

### "401 Unauthorized"
**Problem:** Invalid API key or expired credentials.

**Solution:** 
1. Verify your API key in Azure Portal
2. Check key hasn't expired
3. Ensure correct key format (no extra spaces)

### "404 Deployment Not Found"
**Problem:** Deployment name doesn't match actual deployment.

**Solution:** 
1. Check deployment name in Azure Portal
2. Verify deployment is running
3. Update `AZURE_OPENAI_DEPLOYMENT` variable

### Conversation Not Found
**Problem:** Invalid or expired conversation ID.

**Solution:** 
1. Start a new conversation with `/api/conversation/start`
2. Use the returned `conversationId` in subsequent requests
3. Note: Conversations are stored in memory and cleared on app restart

## Best Practices

### 1. Error Handling
Always wrap AI calls in try-catch blocks:
```java
try {
    String answer = model.chat(prompt);
    return ResponseEntity.ok(answer);
} catch (Exception e) {
    return ResponseEntity.status(500).body(error);
}
```

### 2. Configuration Management
- Never commit API keys to git
- Use environment variables for sensitive data
- Provide sensible defaults for non-sensitive config

### 3. Conversation Memory
- Limit conversation window size to control context length
- Clear old conversations to manage memory
- Consider persistence for production use

### 4. Token Usage
- Monitor token consumption for cost control
- Set appropriate `max-tokens` limits
- Use GPT-3.5 for simpler tasks, GPT-4 for complex ones

## Next Steps

Now that you've mastered basic chat, explore:
- **Module 02: RAG** - Add document search capabilities
- **Module 03: Agents & Tools** - Implement function calling
- **Module 04: Production** - Add observability and content safety

## Resources

- [LangChain4j Documentation](https://docs.langchain4j.dev/)
- [Azure OpenAI Service](https://learn.microsoft.com/azure/ai-services/openai/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

## License

See the root [LICENSE](../LICENSE) file for details.
