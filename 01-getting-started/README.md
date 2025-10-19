# Module 01: Getting Started with LangChain4j# Module 01: Getting Started with LangChain4j



## Overview## Overview

This module demonstrates the core features of LangChain4j with Azure OpenAI, including:This module demonstrates the core features of LangChain4j with Azure OpenAI, including:

- ✅ Basic chat completion (stateless)- ✅ Basic chat completion (stateless)

- ✅ Conversational chat with memory (stateful)- ✅ Conversational chat with memory (stateful)

- ✅ Spring Boot integration- ✅ Spring Boot integration

- ✅ Azure deployment with Container Apps- ✅ Configuration management

- ✅ Infrastructure as Code (Bicep)- ✅ Error handling

- ✅ Configuration management

## Prerequisites

## Quick Start Options

### Required Software

### Option A: Deploy to Azure (Recommended) ⚡- Java 21 or higher

- Maven 3.8+

**Prerequisites**: Azure CLI, Azure Developer CLI (azd), Docker- Git



```bash### Azure OpenAI Setup

# 1. Login to AzureYou'll need an Azure OpenAI resource with:

az login- A deployed chat model (e.g., gpt-4, gpt-3.5-turbo)

azd auth login- API endpoint

- API key

# 2. Deploy everything (takes ~10 minutes)

cd 01-getting-started## Quick Start

azd up

```### 1. Configure Environment Variables



Select your Azure subscription and location (recommend `eastus2` for OpenAI availability).Create a `.env` file in the project root:



**What gets deployed:**```bash

- Azure OpenAI (gpt-4o-mini + text-embedding-3-small)cp .env.example .env

- Azure Container Apps (your application)```

- Azure Container Registry

- Log Analytics WorkspaceEdit `.env` and fill in your Azure OpenAI credentials:

- Managed Identity (passwordless auth)

```bash

**Get your application URL:**AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"

```bashAZURE_OPENAI_API_KEY="your-api-key-here"

azd env get-values | grep APP_URLAZURE_OPENAI_DEPLOYMENT="your-deployment-name"  # e.g., gpt-4

``````



**Test it:**### 2. Build the Project

```bash

curl -X POST https://<your-app-url>/api/chat \```bash

  -H 'Content-Type: application/json' \cd 01-getting-started

  -d '{"message":"Hello Azure!"}'mvn clean compile

``````



**View logs:**### 3. Run the Application

```bash

azd monitor --logsFrom the project root:

```

```bash

**Update after code changes:**# Source environment variables and run

```bashsource .env

azd deploycd 01-getting-started

```mvn spring-boot:run

```

**Clean up:**

```bashOr use the provided script:

azd down

``````bash

./scripts/run-getting-started.sh

### Option B: Run Locally 💻```



**Prerequisites**: Java 21, Maven 3.8+, Azure OpenAI resourceThe application will start on `http://localhost:8080`



1. **Create `.env` file:**## Features & API Endpoints

```bash

cp .env.example .env### 1. Basic Chat (Stateless)

```

Simple one-off chat interactions without conversation history.

2. **Add your Azure OpenAI credentials to `.env`:**

```bash**Endpoint:** `POST /api/chat`

AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"

AZURE_OPENAI_API_KEY="your-api-key-here"**Request:**

AZURE_OPENAI_DEPLOYMENT="gpt-4o-mini"```json

```{

  "message": "What is LangChain4j?"

3. **Build and run:**}

```bash```

# From project root

mvn clean package -DskipTests**Response:**

```json

# Set environment variables and run{

export $(cat .env | xargs)  "prompt": "What is LangChain4j?",

cd 01-getting-started  "answer": "LangChain4j is a Java library that simplifies..."

java -jar target/getting-started-0.1.0.jar}

``````



Or use the provided script:**Example:**

```bash```bash

./scripts/run-getting-started.shcurl -X POST http://localhost:8080/api/chat \

```  -H 'Content-Type: application/json' \

  -d '{"message":"Explain Azure OpenAI in one sentence"}'

The application will start on `http://localhost:8080````



## Features & API Endpoints### 2. Conversational Chat (Stateful)



### 1. Basic Chat (Stateless)Multi-turn conversations with context retention.



Simple one-off chat interactions without conversation history.#### Start a New Conversation



**Endpoint:** `POST /api/chat`**Endpoint:** `POST /api/conversation/start`



**Request:****Response:**

```json```json

{{

  "message": "What is LangChain4j?"  "conversationId": "550e8400-e29b-41d4-a716-446655440000",

}  "message": "Conversation started successfully"

```}

```

**Response:**

```json**Example:**

{```bash

  "prompt": "What is LangChain4j?",curl -X POST http://localhost:8080/api/conversation/start

  "answer": "LangChain4j is a Java library that simplifies..."```

}

```#### Send a Message



**Example:****Endpoint:** `POST /api/conversation/chat`

```bash

curl -X POST http://localhost:8080/api/chat \**Request:**

  -H 'Content-Type: application/json' \```json

  -d '{"message":"Explain Azure OpenAI in one sentence"}'{

```  "conversationId": "550e8400-e29b-41d4-a716-446655440000",

  "message": "My name is John"

### 2. Conversational Chat (Stateful)}

```

Multi-turn conversations with context retention.

**Response:**

#### Start a New Conversation```json

{

**Endpoint:** `POST /api/conversation/start`  "conversationId": "550e8400-e29b-41d4-a716-446655440000",

  "message": "My name is John",

**Response:**  "answer": "Nice to meet you, John! How can I help you today?"

```json}

{```

  "conversationId": "550e8400-e29b-41d4-a716-446655440000",

  "message": "Conversation started successfully"**Example:**

}```bash

```# First message

curl -X POST http://localhost:8080/api/conversation/chat \

#### Send a Message  -H 'Content-Type: application/json' \

  -d '{"conversationId":"YOUR_ID","message":"My name is John"}'

**Endpoint:** `POST /api/conversation/chat`

# Follow-up message - the model remembers your name!

**Request:**curl -X POST http://localhost:8080/api/conversation/chat \

```json  -H 'Content-Type: application/json' \

{  -d '{"conversationId":"YOUR_ID","message":"What is my name?"}'

  "conversationId": "550e8400-e29b-41d4-a716-446655440000",```

  "message": "My name is John"

}#### Get Conversation History

```

**Endpoint:** `GET /api/conversation/{conversationId}/history`

**Response:**

```json**Response:**

{```json

  "conversationId": "550e8400-e29b-41d4-a716-446655440000",{

  "message": "My name is John",  "conversationId": "550e8400-e29b-41d4-a716-446655440000",

  "answer": "Nice to meet you, John! How can I help you today?"  "messageCount": 4,

}  "messages": [

```    { "type": "USER", "text": "My name is John" },

    { "type": "AI", "text": "Nice to meet you, John!" },

**Example conversation:**    ...

```bash  ]

# Start conversation}

CONV_ID=$(curl -X POST http://localhost:8080/api/conversation/start | jq -r '.conversationId')```



# First message#### Clear a Conversation

curl -X POST http://localhost:8080/api/conversation/chat \

  -H 'Content-Type: application/json' \**Endpoint:** `DELETE /api/conversation/{conversationId}`

  -d "{\"conversationId\":\"$CONV_ID\",\"message\":\"My name is John\"}"

**Response:**

# Follow-up - the model remembers!```json

curl -X POST http://localhost:8080/api/conversation/chat \{

  -H 'Content-Type: application/json' \  "message": "Conversation cleared successfully",

  -d "{\"conversationId\":\"$CONV_ID\",\"message\":\"What is my name?\"}"  "conversationId": "550e8400-e29b-41d4-a716-446655440000"

```}

```

#### Get Conversation History

### 3. Health Checks

**Endpoint:** `GET /api/conversation/{conversationId}/history`

**Endpoints:**

#### Clear a Conversation- `GET /api/chat/health`

- `GET /api/conversation/health`

**Endpoint:** `DELETE /api/conversation/{conversationId}`

**Response:**

### 3. Health Checks```json

{

**Endpoints:**  "status": "ok"

- `GET /api/chat/health`}

- `GET /api/conversation/health````



## Architecture## Architecture



### Project Structure### Project Structure



``````

01-getting-started/01-getting-started/

├── infra/                           # Azure infrastructure (Bicep)├── src/main/java/dev/rory/azure/langchain4j/

│   ├── main.bicep                   # Main orchestration│   ├── app/

│   ├── main.bicepparam              # Parameters│   │   ├── Application.java              # Spring Boot main class

│   └── core/                        # Reusable modules│   │   ├── ChatController.java           # Stateless chat endpoint

│       ├── ai/cognitiveservices.bicep│   │   └── ConversationController.java   # Stateful conversation endpoint

│       ├── host/container-app.bicep│   ├── service/

│       └── ...│   │   └── ConversationService.java      # Conversation memory logic

├── src/main/java/dev/rory/azure/langchain4j/│   ├── config/

│   ├── app/│   │   └── LangChainConfig.java          # LangChain4j bean configuration

│   │   ├── Application.java         # Spring Boot main class│   └── model/

│   │   ├── ChatController.java      # Stateless chat endpoint│       ├── ChatRequest.java              # Request DTOs

│   │   └── ConversationController.java  # Stateful conversation│       └── ChatResponse.java             # Response DTOs

│   ├── service/├── src/main/resources/

│   │   └── ConversationService.java # Conversation memory logic│   └── application.yaml                  # Application configuration

│   ├── config/└── pom.xml                               # Maven dependencies

│   │   └── LangChainConfig.java     # LangChain4j configuration```

│   └── model/

│       ├── ChatRequest.java         # Request DTOs### Key Components

│       └── ChatResponse.java        # Response DTOs

├── azure.yaml                       # Azure Developer CLI config#### 1. LangChainConfig

├── Dockerfile                       # Multi-stage Docker buildConfigures the Azure OpenAI chat model as a Spring bean:

└── pom.xml                          # Maven dependencies- Reads configuration from environment variables or application.yaml

```- Sets up temperature, max tokens, retry logic

- Provides the model to controllers via dependency injection

### Key Components

#### 2. ChatController

#### LangChainConfigHandles stateless chat requests:

Configures the Azure OpenAI chat model as a Spring bean:- No conversation memory

- Reads configuration from environment variables or application.yaml- Each request is independent

- Sets up temperature, max tokens, retry logic- Simple request/response pattern

- Uses `@ComponentScan` to ensure configuration is discovered

#### 3. ConversationService

#### ChatControllerManages conversation state:

Handles stateless chat requests - each request is independent.- Maintains separate memory for each conversation ID

- Uses `MessageWindowChatMemory` to store recent messages

#### ConversationService- Configurable message window size (default: 10 messages)

Manages conversation state using `MessageWindowChatMemory`:

- Maintains separate memory for each conversation ID#### 4. ConversationController

- Configurable message window size (default: 10 messages)Handles stateful conversations:

- In-memory storage (cleared on restart)- Manages conversation lifecycle (start, chat, history, clear)

- Provides conversation context to the model

#### ConversationController- Returns conversation-aware responses

Handles stateful conversations with full lifecycle management.

## Configuration

## Azure Infrastructure

### Environment Variables

The `infra/` directory contains production-ready Bicep templates:

| Variable | Description | Example |

### Deployed Resources|----------|-------------|---------|

- **Azure OpenAI** (`aoai-{token}`)| `AZURE_OPENAI_ENDPOINT` | Azure OpenAI endpoint URL | `https://your-resource.openai.azure.com/` |

  - gpt-4o-mini: 20K TPM, $0.150/$0.600 per 1M tokens| `AZURE_OPENAI_API_KEY` | Azure OpenAI API key | `abc123...` |

  - text-embedding-3-small: 20K TPM, $0.020 per 1M tokens| `AZURE_OPENAI_DEPLOYMENT` | Model deployment name | `gpt-4` or `gpt-35-turbo` |

- **Container App** (`ca-{token}`)

  - 2 CPU cores, 4GB memory### Application Properties

  - Auto-scaling (1-10 replicas)

  - HTTPS ingress with CORSConfigure in `application.yaml`:

- **Container Registry** (`acr{token}`)

  - Stores Docker images```yaml

  - Managed identity authazure:

- **Log Analytics** (`log-{token}`)  openai:

  - 30-day retention    temperature: 0.2          # Lower = more focused, Higher = more creative

  - Centralized logging    max-tokens: 1000          # Maximum tokens in response

- **Managed Identity** (`id-{token}`)```

  - Passwordless authentication

  - Cognitive Services OpenAI User role## Common Use Cases



### Security Features### 1. Simple Q&A Bot

✅ API keys stored as Container App secrets  Use the stateless `/api/chat` endpoint for independent questions.

✅ Managed Identity for Azure authentication  

✅ Role-Based Access Control (RBAC)  ### 2. Customer Support Agent

✅ No credentials in code or configuration files  Use the stateful `/api/conversation/*` endpoints to maintain context across questions.



### Cost Estimate### 3. Interactive Tutorial

- **Fixed**: ~$60-80/month (Container App + ACR + Log Analytics)Start a conversation and guide users through multi-step processes while maintaining context.

- **Variable**: Azure OpenAI usage-based pricing

- **Dev Tip**: Set `minReplicas: 0` to scale to zero when idle## Troubleshooting



## Configuration### "endpoint cannot be null or blank"

**Problem:** Azure OpenAI credentials not configured.

### Environment Variables

**Solution:** 

| Variable | Description | Example |1. Create `.env` file with your credentials

|----------|-------------|---------|2. Source the file: `source .env`

| `AZURE_OPENAI_ENDPOINT` | Azure OpenAI endpoint URL | `https://your-resource.openai.azure.com/` |3. Verify variables: `echo $AZURE_OPENAI_ENDPOINT`

| `AZURE_OPENAI_API_KEY` | Azure OpenAI API key | `abc123...` |

| `AZURE_OPENAI_DEPLOYMENT` | Model deployment name | `gpt-4o-mini` |### "401 Unauthorized"

**Problem:** Invalid API key or expired credentials.

### Application Properties

**Solution:** 

Configure in `application.yaml`:1. Verify your API key in Azure Portal

2. Check key hasn't expired

```yaml3. Ensure correct key format (no extra spaces)

azure:

  openai:### "404 Deployment Not Found"

    temperature: 0.2          # Lower = more focused, Higher = more creative**Problem:** Deployment name doesn't match actual deployment.

    max-tokens: 1000          # Maximum tokens in response

```**Solution:** 

1. Check deployment name in Azure Portal

## Troubleshooting2. Verify deployment is running

3. Update `AZURE_OPENAI_DEPLOYMENT` variable

### Azure Deployment Issues

### Conversation Not Found

**"Container App unhealthy"****Problem:** Invalid or expired conversation ID.

```bash

# Check logs**Solution:** 

azd monitor --logs1. Start a new conversation with `/api/conversation/start`

2. Use the returned `conversationId` in subsequent requests

# Check container status3. Note: Conversations are stored in memory and cleared on app restart

az containerapp revision list --name ca-{token} --resource-group rg-{env} -o table

```## Best Practices



**"OpenAI model not found"**### 1. Error Handling

- Wait 2-3 minutes after initial deployment for models to deployAlways wrap AI calls in try-catch blocks:

- Verify deployment name matches in `infra/core/ai/cognitiveservices.bicep````java

try {

**"Cannot modify container app - operation in progress"**    String answer = model.chat(prompt);

- Wait for current deployment to complete (~5 minutes)    return ResponseEntity.ok(answer);

- Check status: `az containerapp show --name ca-{token} --resource-group rg-{env}`} catch (Exception e) {

    return ResponseEntity.status(500).body(error);

### Local Development Issues}

```

**"endpoint cannot be null or blank"**

1. Verify `.env` file exists and contains credentials### 2. Configuration Management

2. Export variables: `export $(cat .env | xargs)`- Never commit API keys to git

3. Confirm: `echo $AZURE_OPENAI_ENDPOINT`- Use environment variables for sensitive data

- Provide sensible defaults for non-sensitive config

**"401 Unauthorized"**

1. Check API key in Azure Portal### 3. Conversation Memory

2. Verify key hasn't expired- Limit conversation window size to control context length

3. Ensure no extra spaces in `.env` file- Clear old conversations to manage memory

- Consider persistence for production use

**"No qualifying bean of type 'AzureOpenAiChatModel'"**

- Verify `Application.java` has `@ComponentScan(basePackages = "dev.rory.azure.langchain4j")`### 4. Token Usage

- Check `LangChainConfig.java` is in the correct package- Monitor token consumption for cost control

- Set appropriate `max-tokens` limits

## Best Practices- Use GPT-3.5 for simpler tasks, GPT-4 for complex ones



### 1. Error Handling## Next Steps

Always wrap AI calls in try-catch blocks:

```javaNow that you've mastered basic chat, explore:

try {- **Module 02: RAG** - Add document search capabilities

    String answer = model.chat(prompt);- **Module 03: Agents & Tools** - Implement function calling

    return ResponseEntity.ok(answer);- **Module 04: Production** - Add observability and content safety

} catch (Exception e) {

    log.error("Chat failed", e);## Resources

    return ResponseEntity.status(500).body(new ErrorResponse(e.getMessage()));

}- [LangChain4j Documentation](https://docs.langchain4j.dev/)

```- [Azure OpenAI Service](https://learn.microsoft.com/azure/ai-services/openai/)

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

### 2. Configuration Management

- Never commit API keys to git## License

- Use environment variables for sensitive data

- Use Managed Identity in Azure (passwordless)See the root [LICENSE](../LICENSE) file for details.

- Provide sensible defaults for non-sensitive config

### 3. Conversation Memory
- Limit conversation window size to control context length
- Clear old conversations to manage memory
- Consider persistence for production use (Azure Cosmos DB, Redis)

### 4. Token Usage
- Monitor token consumption for cost control
- Set appropriate `max-tokens` limits
- Use GPT-3.5 for simpler tasks, GPT-4o-mini for complex ones

## Development Workflow

### After Code Changes

**Azure deployment:**
```bash
azd deploy
```

**Local testing:**
```bash
mvn clean package -DskipTests
java -jar target/getting-started-0.1.0.jar
```

### View Application Logs

**Azure:**
```bash
azd monitor --logs
# or
az containerapp logs show --name ca-{token} --resource-group rg-{env} --follow
```

**Local:**
- Check console output or application logs

## Next Steps

Now that you've mastered basic chat, explore:
- **Module 02: RAG** - Add document search capabilities (embeddings already deployed!)
- **Module 03: Agents & Tools** - Implement function calling
- **Module 04: Production** - Add observability and content safety

## Resources

- [LangChain4j Documentation](https://docs.langchain4j.dev/)
- [Azure OpenAI Service](https://learn.microsoft.com/azure/ai-services/openai/)
- [Azure Container Apps](https://learn.microsoft.com/azure/container-apps/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Azure Developer CLI](https://learn.microsoft.com/azure/developer/azure-developer-cli/)

## License

See the root [LICENSE](../LICENSE) file for details.
