# LangChain4j for Azure

This repository provides three progressive modules teaching you to build chat applications, add document search (RAG), and create autonomous AI agents.

## Modules

### 01 - Getting Started
Simple Spring Boot chat app with conversation memory and streaming responses.

### 02 - RAG (Retrieval-Augmented Generation)
Add document search capabilities with embeddings and vector stores.

### 03 - Agents & Tools
Build autonomous agents that use HTTP-based tools to accomplish complex tasks.

## Quick Start

### Prerequisites

- Azure Account with active subscription
- [Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli) (`az login`)
- [Azure Developer CLI](https://aka.ms/install-azd) (`azd`)
- Java 21 or higher
- Maven 3.9+

### Deploy to Azure

```bash
git clone https://github.com/roryp/langchainagent.git
cd langchainagent/01-getting-started

azd auth login
azd up
```

**That's it!** Multi-stage Dockerfiles handle all building automatically.

**Deployment time:** ~5-10 minutes

### What Gets Deployed

- **Azure OpenAI** - gpt-4o-mini + text-embedding-3-small
- **3 Container Apps** - Getting Started, RAG, Agents
- **Container Registry** - For Docker images
- **Log Analytics** - For monitoring
- **Managed Identity** - Secure authentication

### Test Your Deployment

```bash
# Get URLs
azd env get-values | grep APP_URL

# Test chat
curl "$APP_URL/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message":"Hello!"}'

# Test RAG
curl "$RAG_APP_URL/api/rag/health"

# Test agents
curl "$AGENTS_APP_URL/api/agent/health"
```

## Architecture

**Direct Azure OpenAI Integration** - Cost-optimized, no AI Foundry Hub needed

```
┌──────────────────────────────────────────┐
│  Azure Container Apps                    │
│  ┌────────┐  ┌────────┐  ┌────────┐    │
│  │  Chat  │  │  RAG   │  │ Agents │    │
│  │  :8080 │  │  :8081 │  │  :8082 │    │
│  └────────┘  └────────┘  └────────┘    │
└──────────────────────────────────────────┘
                 ↓
┌──────────────────────────────────────────┐
│  Azure OpenAI Service                    │
│  • gpt-4o-mini (chat)                   │
│  • text-embedding-3-small (embeddings)  │
└──────────────────────────────────────────┘
```

## Features by Module

| Module | Port | Key Features |
|--------|------|-------------|
| **01-getting-started** | 8080 | Basic chat, conversation memory, streaming |
| **02-rag** | 8081 | Document upload, embeddings, semantic search, Q&A |
| **03-agents-tools** | 8082 | Function calling, tool execution, multi-step reasoning |

## Technologies

- **LangChain4j** - Java framework for LLM apps
- **Azure OpenAI** - GPT-4o-mini & embeddings
- **Spring Boot 3.3.4** - Application framework
- **Azure Container Apps** - Serverless hosting
- **Azure Bicep** - Infrastructure as Code

## Development

### Local Development

Each module can run locally. See module-specific READMEs for details.

### Update Deployment

```bash
cd 01-getting-started
azd deploy  # Fast updates for code changes
```

### View Logs

```bash
azd monitor --logs
```
## Resources

- [LangChain4j Documentation](https://docs.langchain4j.dev/)
- [Azure OpenAI Service](https://learn.microsoft.com/azure/ai-services/openai/)
- [Azure Container Apps](https://learn.microsoft.com/azure/container-apps/)
- [Azure Developer CLI](https://learn.microsoft.com/azure/developer/azure-developer-cli/)

## Contributing

Contributions welcome! Fork the repo, create a feature branch, test thoroughly, and submit a PR.

## License

MIT License - See [LICENSE](LICENSE) file for details.