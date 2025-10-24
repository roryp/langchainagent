# Module 01: Getting Started with LangChain4j

## What You'll Learn

If you completed the quick start, you saw how to send prompts and get responses. That's the foundation, but real applications need more. This module teaches you how to build conversational AI that remembers context and maintains state - the difference between a one-off demo and a production-ready application.

We'll use Azure OpenAI's GPT-5 throughout this guide because its advanced reasoning capabilities make the behavior of different patterns more apparent. When you add memory, you'll clearly see the difference. This makes it easier to understand what each component brings to your application.

## Understanding the Core Problem

Language models are stateless. Each API call is independent. If you send "My name is John" and then ask "What's my name?", the model has no idea you just introduced yourself. It treats every request as if it's the first conversation you've ever had.

This is fine for simple Q&A but useless for real applications. Customer service bots need to remember what you told them. Personal assistants need context. Any multi-turn conversation requires memory.

## How Memory Works

Chat memory solves the stateless problem by maintaining conversation history. Before sending your request to the model, the framework prepends relevant previous messages. When you ask "What's my name?", the system actually sends the entire conversation history, allowing the model to see you previously said "My name is John."

LangChain4j provides memory implementations that handle this automatically. You choose how many messages to retain and the framework manages the context window.

## What This Module Covers

You'll build two applications that demonstrate the difference:

**Stateless Chat** - Each request is independent. The model has no memory of previous messages. This is the pattern you used in the quick start.

**Stateful Conversation** - Each request includes conversation history. The model maintains context across multiple turns. This is what production applications require.

Both run locally using Azure OpenAI's GPT-5 deployment. You'll see the same Spring Boot application expose both patterns through different API endpoints.

## Technical Stack

- Spring Boot for the REST API
- LangChain4j for model interaction and memory management
- Azure OpenAI GPT-5 for language model capabilities
- MessageWindowChatMemory for conversation state

## Prerequisites

- Azure subscription with Azure OpenAI access
- Java 21, Maven 3.9+, Azure CLI, azd CLI

## Quick Start

### Deploy Azure OpenAI Infrastructure

```bash
cd 01-introduction
azd up  # Select subscription and location (eastus2 recommended)
```

This will:
1. Deploy Azure OpenAI resource with GPT-5 and text-embedding-3-small models
2. Automatically generate `.env` file in project root with credentials
3. Set up all required environment variables

**Verify `.env` was created:**
```bash
cat ../.env  # Should show AZURE_OPENAI_ENDPOINT, API_KEY, etc.
```

**Or manually generate `.env` from azd:**
```bash
cd ..
bash .azd-env.sh
```

### Run Locally

```bash
cd 01-introduction
./start.sh  # Automatically sources .env from parent directory
```

**Or run manually:**
```bash
source ../.env
mvn spring-boot:run
```

## API Endpoints

### 1. Basic Chat (Stateless)

**POST** `/api/chat` - Simple one-off questions

```bash
curl -X POST http://localhost:8080/api/chat \
  -H 'Content-Type: application/json' \
  -d '{"message":"What is LangChain4j?"}'
```

### 2. Conversational Chat (Stateful)

Multi-turn conversations with memory.

**Start:** `POST /api/conversation/start`
```bash
CONV_ID=$(curl -X POST http://localhost:8080/api/conversation/start | jq -r '.conversationId')
```

**Chat:** `POST /api/conversation/chat`
```bash
curl -X POST http://localhost:8080/api/conversation/chat \
  -H 'Content-Type: application/json' \
  -d "{\"conversationId\":\"$CONV_ID\",\"message\":\"My name is John\"}"

# Follow-up - remembers context!
curl -X POST http://localhost:8080/api/conversation/chat \
  -H 'Content-Type: application/json' \
  -d "{\"conversationId\":\"$CONV_ID\",\"message\":\"What is my name?\"}"
```

**History:** `GET /api/conversation/{id}/history`  
**Clear:** `DELETE /api/conversation/{id}`

### 3. Health Checks

- `GET /api/chat/health`
- `GET /api/conversation/health`

## Architecture

### Project Structure

```
01-getting-started/
 src/main/java/...
    app/
       Application.java              # Spring Boot main
       ChatController.java           # Stateless chat
       ConversationController.java   # Stateful chat
    service/
       ConversationService.java      # Memory management
    config/
       LangChainConfig.java          # Azure OpenAI setup
    model/                             # DTOs
 infra/                                 # Bicep templates
 pom.xml
```

**Key Components:**
- `LangChainConfig` - Configures Azure OpenAI chat model
- `ChatController` - Stateless requests (no memory)
- `ConversationService` - Manages conversation memory (MessageWindowChatMemory)
- `ConversationController` - Stateful conversations with lifecycle management

## Configuration

**Environment Variables:**

| Variable | Description |
|----------|-------------|
| `AZURE_OPENAI_ENDPOINT` | Azure OpenAI endpoint URL |
| `AZURE_OPENAI_API_KEY` | API key |
| `AZURE_OPENAI_DEPLOYMENT` | Model deployment name (e.g., gpt-5) |

**Application Properties (application.yaml):**
```yaml
azure:
  openai:
    reasoning-effort: medium  # low, medium, high (GPT-5 reasoning depth)
    max-tokens: 1000          # Max response length
```

## Azure Infrastructure

Deploy Azure OpenAI with `azd up`:
- **Azure OpenAI** - gpt-5 + text-embedding-3-small

```bash
cd 01-introduction
azd up
```

**View deployed resources:**
```bash
azd env get-values
```

All examples run locally using the deployed Azure OpenAI endpoint.

## Troubleshooting

**"endpoint cannot be null or blank"**  
Set environment variables: `export AZURE_OPENAI_ENDPOINT=...`

**"401 Unauthorized"**  
Verify API key in Azure Portal

**Conversation Not Found**  
Start new conversation with `/api/conversation/start`

## Next Steps

**Next Module:** [02-prompt-engineering - Prompt Engineering with GPT-5](../02-prompt-engineering/README.md)
