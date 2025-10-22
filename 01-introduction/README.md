# Module 01: Getting Started with LangChain4j

Learn the basics of LangChain4j with Azure OpenAI - chat completion, conversation memory, and Azure deployment.

## Features

- Basic chat completion (stateless)
- Conversational chat with memory (stateful)
- Spring Boot integration
- Azure Container Apps deployment
- Infrastructure as Code (Bicep)

## Prerequisites

- Azure subscription with Azure OpenAI access (deployed chat model)
- Java 21, Maven 3.9+, Azure CLI, azd CLI

## Quick Start

### Deploy to Azure

```bash
cd 01-getting-started
azd up  # Select subscription and location (eastus2 recommended)
```

**Get your URL:**
```bash
azd env get-values | grep APP_URL
```

**Test:**
```bash
curl -X POST https://<your-app-url>/api/chat \
  -H 'Content-Type: application/json' \
  -d '{"message":"Hello!"}'
```

### Run Locally

```bash
cd 01-getting-started
export AZURE_OPENAI_ENDPOINT="https://aoai-xyz.openai.azure.com/"
export AZURE_OPENAI_API_KEY="***"
export AZURE_OPENAI_DEPLOYMENT="gpt-4o-mini"
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
| `AZURE_OPENAI_DEPLOYMENT` | Model deployment name (e.g., gpt-4o-mini) |

**Application Properties (application.yaml):**
```yaml
azure:
  openai:
    temperature: 0.2     # Lower = focused, Higher = creative
    max-tokens: 1000     # Max response length
```

## Azure Infrastructure

Deployed with `azd up`:
- **Azure OpenAI** - gpt-4o-mini + text-embedding-3-small
- **Container App** - Auto-scaling (1-10 replicas), 2 CPU, 4GB memory
- **Container Registry** - Managed identity auth
- **Log Analytics** - 30-day retention

**View logs:**
```bash
azd monitor --logs
```

**Update deployment:**
```bash
azd deploy
```

## Troubleshooting

**"endpoint cannot be null or blank"**  
Set environment variables: `export AZURE_OPENAI_ENDPOINT=...`

**"401 Unauthorized"**  
Verify API key in Azure Portal, check for expiration

**"404 Deployment Not Found"**  
Check deployment name matches Azure Portal

**Conversation Not Found**  
Start new conversation with `/api/conversation/start`. Note: In-memory storage clears on restart.

## Next Steps

- **Module 02: Prompt Engineering** - Master effective prompt design
- **Module 03: RAG** - Document search with embeddings

## Resources

- [LangChain4j Docs](https://docs.langchain4j.dev/)
- [Azure OpenAI](https://learn.microsoft.com/azure/ai-services/openai/)
- [Azure Container Apps](https://learn.microsoft.com/azure/container-apps/)
