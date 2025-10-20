# LangChain4j for Azure — From Zero to Production

This repository hosts a multi‑module Java course to teach you
how to build AI‑powered applications using LangChain4j and deploy them
to Azure. Each numbered module contains a set of labs and sample code
illustrating the core patterns for working with large language models.

## Modules

### 01 - Getting Started
**Simple Spring Boot chat app powered by LangChain4j with Azure OpenAI**

Learn how to configure endpoints, API keys, and deploy your first container to Azure.

- ✅ Azure OpenAI integration
- ✅ Prompt templates
- ✅ Conversation memory
- ✅ Streaming responses
- ✅ Azure Container Apps deployment

### 02 - RAG (Retrieval-Augmented Generation)
**Add your own data to enhance AI responses**

Index your documents and serve relevant knowledge through vector search.

- ✅ Document ingestion (PDF, TXT)
- ✅ Text splitting strategies
- ✅ Embedding generation
- ✅ Vector stores (Qdrant, Azure AI Search)
- ✅ Retrieval-augmented generation
- ✅ Source attribution

### 03 - Agents & Tools
**AI Agents with LangChain4j and REST-based Tools**

Build autonomous agents that can use tools via HTTP to accomplish complex tasks.

- ✅ ReAct pattern (Reasoning + Acting)
- ✅ LangChain4j Azure OpenAI integration
- ✅ HTTP-based tool execution
- ✅ Session-based conversation management
- ✅ OpenAPI tool specification
- ✅ Cost-optimized infrastructure (~60% cheaper than with AI Foundry Hub)

**Architecture:** Direct Azure OpenAI integration (no Azure AI Foundry Hub/Project needed)

## Quick Start

### Prerequisites

- **Azure Account** with active subscription
- **Azure CLI** installed and authenticated (`az login`)
- **Azure Developer CLI** (`azd`) - [Install azd](https://aka.ms/install-azd)
- **Java 21** (OpenJDK or Oracle JDK)
- **Maven 3.9+**
- **Docker** (optional, for local development)

### Deploy to Azure

```bash
# Clone the repository
git clone https://github.com/roryp/langchainagent.git
cd langchainagent/01-getting-started

# Login to Azure
azd auth login

# Provision infrastructure and deploy all services
azd up
```

This provisions:
- **Azure OpenAI** with gpt-4o-mini and text-embedding-3-small models
- **3 Container Apps** (getting-started, RAG, agents)
- **Container Registry** for Docker images
- **Log Analytics** for monitoring
- **Managed Identity** for secure access

**Total deployment time:** ~5-10 minutes

### Test the Services

```bash
# Get the deployed URLs
azd env get-values | grep -E "(APP_URL|AGENTS_APP_URL|RAG_APP_URL)"

# Test Getting Started service
curl "$APP_URL/api/chat" -H "Content-Type: application/json" \
  -d '{"message":"Hello, how are you?"}'

# Test RAG service
curl "$RAG_APP_URL/api/rag/query" -H "Content-Type: application/json" \
  -d '{"query":"Your question here"}'

# Test Agents service
curl "$AGENTS_APP_URL/api/agent/chat" -H "Content-Type: application/json" \
  -d '{"message":"What is the weather in Seattle?","sessionId":"test"}'
```

## Architecture

### Infrastructure Design

This project uses **direct Azure OpenAI integration** via LangChain4j for cost efficiency and simplicity:

```
┌─────────────────────────────────────────────────────────────┐
│  Azure Container Apps                                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   Module 01  │  │   Module 02  │  │   Module 03  │     │
│  │  Chat App    │  │  RAG Service │  │    Agents    │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│  Azure OpenAI Service                                       │
│  - gpt-4o-mini (chat completions)                          │
│  - text-embedding-3-small (embeddings)                     │
└─────────────────────────────────────────────────────────────┘
```

**Cost-Optimized:** We intentionally use direct Azure OpenAI integration instead of Azure AI Foundry Hub/Project to reduce costs by ~60% while maintaining full functionality.

### Key Design Decisions

1. **Direct Azure OpenAI** - Official Microsoft recommendation for Java developers
2. **LangChain4j** - Native Azure integration, well-documented, production-ready
3. **Container Apps** - Serverless deployment, auto-scaling, built-in HTTPS
4. **No AI Foundry Hub** - Not needed for LangChain4j agents, saves $10-50/month

See [03-agents-tools/INFRASTRUCTURE_DECISIONS.md](03-agents-tools/INFRASTRUCTURE_DECISIONS.md) for detailed rationale.

## Technologies

- **LangChain4j** - Java framework for LLM applications
- **Azure OpenAI** - GPT-4o-mini for chat, text-embedding-3-small for embeddings
- **Spring Boot 3.3.4** - Application framework
- **Azure Container Apps** - Serverless container hosting
- **Azure Bicep** - Infrastructure as Code
- **Maven** - Build and dependency management

## Important

### Security Best Practices

🚫 **Do not commit secrets**. Configuration values like API keys belong in environment variables or Azure Key Vault.

- ✅ Use `azd` environment variables (automatically configured)
- ✅ Use Azure Managed Identity in production
- ✅ Never commit `.env` files or API keys to git
- ✅ Use `.env.example` as a template for local development

### Cost Management

Estimated monthly costs for all three modules running 24/7:

| Resource | Monthly Cost |
|----------|--------------|
| Azure OpenAI (moderate usage) | $10-30 |
| Container Apps (3 apps, 0.25 vCPU each) | $3-10 |
| Container Registry | $0.50 |
| Log Analytics | $0.50 |
| **Total** | **$15-40/month** |

**Cost Savings:** By using direct Azure OpenAI instead of AI Foundry Hub/Project, we save ~$10-50/month.

**Tip:** Use `azd down` to delete all resources when not in use.

## Module Details

### Module 01: Getting Started
- **Path:** `01-getting-started/`
- **Port:** 8080
- **Focus:** Basic chat completions, prompt templates, conversation memory

### Module 02: RAG
- **Path:** `02-rag/`
- **Port:** 8081
- **Focus:** Document ingestion, embeddings, vector search, retrieval

### Module 03: Agents & Tools
- **Path:** `03-agents-tools/`
- **Port:** 8082
- **Focus:** ReAct agents, tool calling, HTTP-based tools

Each module is independently deployable and includes:
- ✅ Source code with detailed comments
- ✅ Bicep infrastructure templates
- ✅ README with quickstart guide
- ✅ Test scripts

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes with clear commit messages
4. Test thoroughly
5. Submit a pull request

## Resources

- [LangChain4j Documentation](https://docs.langchain4j.dev/)
- [Azure OpenAI Service](https://learn.microsoft.com/azure/ai-services/openai/)
- [Azure Container Apps](https://learn.microsoft.com/azure/container-apps/)
- [Azure Developer CLI](https://learn.microsoft.com/azure/developer/azure-developer-cli/)
- [Spring Boot](https://spring.io/projects/spring-boot)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or contributions:
- 🐛 [Report bugs](https://github.com/roryp/langchainagent/issues)
- 💡 [Request features](https://github.com/roryp/langchainagent/issues)
- 📖 [View documentation](https://github.com/roryp/langchainagent)
