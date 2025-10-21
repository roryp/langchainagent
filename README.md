# LangChain4j for Beginners — Azure OpenAI Course

A comprehensive, hands-on course for building production-ready AI applications with LangChain4j and Azure OpenAI. Learn by building real applications from simple chat to advanced AI agents.

## Course Modules

### 00 - Course Setup
**Start Here!** Complete environment setup, Azure OpenAI configuration, and verification.
- Prerequisites and installation
- Azure account setup
- Model deployments
- Environment variables
- Verification tests

### 01 - Introduction
Simple Spring Boot chat application with conversation memory and streaming responses.
- Stateless chat
- Conversation memory
- Message history
- Basic prompting

### 02 - RAG (Retrieval-Augmented Generation)
Add document search capabilities with embeddings and semantic search.
- Document ingestion
- Vector embeddings
- Semantic search
- Context-aware Q&A
- Source citations

### 03 - Prompt Engineering
Master the art of crafting effective prompts for LLMs.
- Prompt templates
- Few-shot learning
- Structured outputs
- Chain-of-thought
- Output parsers

### 04 - Tools
Enable LLMs to interact with external systems and APIs.
- Tool definition
- HTTP-based tools
- Calculator tools
- Weather API integration
- Custom tool creation

### 05 - MCP (Model Context Protocol)
Connect to MCP servers and use standardized tools.
- MCP transport setup
- MCP client configuration
- Tool providers
- Resource access
- Docker integration

## Quick Start

### Prerequisites

- Azure Account with active subscription
- [Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli) (`az login`)
- [Azure Developer CLI](https://aka.ms/install-azd) (`azd`)
- Java 21 or higher
- Maven 3.9+

**New to this?** Start with [Module 00: Course Setup](00-course-setup/README.md) for detailed instructions.

### Deploy to Azure

```bash
git clone https://github.com/roryp/LangChain4j-for-Beginners.git
cd LangChain4j-for-Beginners/01-introduction

azd auth login
azd up
```

**That's it!** Multi-stage Dockerfiles handle all building automatically.

**Deployment time:** ~5-10 minutes

### What Gets Deployed

- **Azure OpenAI** - gpt-4o-mini + text-embedding-3-small
- **3 Container Apps** - Introduction, RAG, MCP
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

# Test MCP
curl "$MCP_APP_URL/api/mcp/health"
```

## Architecture

**Direct Azure OpenAI Integration** - Cost-optimized, no AI Foundry Hub needed

```
┌──────────────────────────────────────────┐
│  Azure Container Apps                    │
│  ┌────────┐  ┌────────┐  ┌────────┐    │
│  │  Chat  │  │  RAG   │  │  MCP   │    │
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

| Module | Port | Key Features | Difficulty |
|--------|------|-------------|------------|
| **00-course-setup** | - | Environment setup, verification | Beginner |
| **01-introduction** | 8080 | Basic chat, memory, streaming | Beginner |
| **02-rag** | 8081 | Documents, embeddings, Q&A | Intermediate |
| **03-prompt-engineering** | 8083 | Templates, few-shot, parsing | Intermediate |
| **04-tools** | 8082 | Tool definition, HTTP calls | Advanced |
| **05-mcp** | 8082 | MCP servers, tool providers | Advanced |

## Technologies

- **LangChain4j 1.7.1** - Java framework for LLM apps
- **Azure OpenAI** - GPT-4o-mini & text-embedding-3-small
- **Spring Boot 3.3.4** - Application framework
- **Java 21** - Programming language
- **Maven 3.9+** - Build tool
- **Azure Container Apps** - Serverless hosting
- **Azure Bicep** - Infrastructure as Code

## Learning Path

1. **Start with Setup**: Complete [00-course-setup](00-course-setup/README.md)
2. **Learn Basics**: Build your first app in [01-introduction](01-introduction/README.md)
3. **Add Intelligence**: Implement RAG in [02-rag](02-rag/README.md)
4. **Master Prompts**: Learn techniques in [03-prompt-engineering](03-prompt-engineering/README.md)
5. **Extend Capabilities**: Add tools in [04-tools](04-tools/README.md)
6. **Connect to MCP**: Use Model Context Protocol in [05-mcp](05-mcp/README.md)

Each module includes:
- Detailed README with concepts and examples
- Working code you can run immediately
- Hands-on challenges to test your skills
- Troubleshooting tips

## Documentation

- **[GLOSSARY.md](docs/GLOSSARY.md)** - Key terms and concepts
- **[TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)** - Common issues and solutions
- **[TESTING-PLAN.md](TESTING-PLAN.md)** - Comprehensive testing guide
- **[copilot-instructions.md](copilot-instructions.md)** - Project transformation plan

## Development

### Local Development

Each module can run locally. See module-specific READMEs for details.

**Example (Introduction module)**:
```bash
cd 01-introduction

# Set environment variables
export AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"
export AZURE_OPENAI_API_KEY="your-key"
export AZURE_OPENAI_DEPLOYMENT="gpt-4o-mini"

# Run
mvn spring-boot:run
```

### Build All Modules

```bash
# From root directory
mvn clean install
```

### Update Deployment

```bash
cd 01-introduction
azd deploy  # Fast updates for code changes
```

### View Logs

```bash
azd monitor --logs
```

## Resources

- **[LangChain4j Documentation](https://docs.langchain4j.dev/)** - Official docs
- **[Azure OpenAI Service](https://learn.microsoft.com/azure/ai-services/openai/)** - Azure docs
- **[Azure Container Apps](https://learn.microsoft.com/azure/container-apps/)** - Deployment platform
- **[Azure Developer CLI](https://learn.microsoft.com/azure/developer/azure-developer-cli/)** - azd CLI docs
- **[OpenAI Prompt Engineering](https://platform.openai.com/docs/guides/prompt-engineering)** - Best practices

## Contributing

Contributions welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Test thoroughly using [TESTING-PLAN.md](TESTING-PLAN.md)
4. Commit your changes (`git commit -m 'Add amazing feature'`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

## License

MIT License - See [LICENSE](LICENSE) file for details.

## Support

- **Bug Reports**: [GitHub Issues](https://github.com/roryp/LangChain4j-for-Beginners/issues)
- **Feature Requests**: [GitHub Issues](https://github.com/roryp/LangChain4j-for-Beginners/issues)
- **Troubleshooting**: [TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)
- **Discussions**: [GitHub Discussions](https://github.com/roryp/LangChain4j-for-Beginners/discussions)

## Show Your Support

If this course helped you learn LangChain4j, please:
- Star this repository
- Share on social media
- Write a blog post about your experience
- Contribute improvements

---

**Ready to start?** Head to [00-course-setup](00-course-setup/README.md) to begin your journey!
