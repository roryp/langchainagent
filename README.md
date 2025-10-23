# LangChain4j for Beginners

A comprehensive course for building AI applications with LangChain4j and Azure OpenAI GPT-5, from basic chat to advanced AI agents.

## What You'll Learn

Build production-ready AI applications using:
- **GPT-5** with reasoning effort levels (low/medium/high)
- **LangChain4j** framework for Java developers
- **Azure OpenAI** for enterprise-grade AI services
- **RAG** (Retrieval-Augmented Generation) for knowledge-based systems
- **AI Agents** with tool calling and multi-step reasoning

## Architecture

**Infrastructure**: Azure OpenAI (GPT-5 + text-embedding-3-small) deployed via `azd`  
**Applications**: Spring Boot apps running locally, connecting to Azure OpenAI

## Table of Contents

1. [Quick Start](00-quick-start/) - Get started with LangChain4j
2. [Introduction](01-introduction/) - Learn the fundamentals of LangChain4j
3. [Prompt Engineering](02-prompt-engineering/) - Master effective prompt design
4. [RAG (Retrieval-Augmented Generation)](03-rag/) - Build intelligent knowledge-based systems
5. [Tools](04-tools/) - Integrate external tools and APIs with AI agents
6. [MCP (Model Context Protocol)](05-mcp/) - Work with the Model Context Protocol

## Prerequisites

- Azure subscription with Azure OpenAI GPT-5 access
- Java 21+
- Maven 3.9+
- Azure CLI (`az`)
- Azure Developer CLI (`azd`)

## Quick Start

```bash
# 1. Deploy Azure OpenAI infrastructure
cd 01-introduction
azd up

# 2. Export environment variables
export AZURE_OPENAI_ENDPOINT="$(azd env get-values | grep AZURE_OPENAI_ENDPOINT | cut -d'=' -f2 | tr -d '"')"
export AZURE_OPENAI_API_KEY="$(azd env get-values | grep AZURE_OPENAI_KEY | cut -d'=' -f2 | tr -d '"')"
export AZURE_OPENAI_DEPLOYMENT="gpt-5"

# 3. Run any module
cd ../02-prompt-engineering
mvn spring-boot:run
```

Start with [Quick Start](00-quick-start/) and progress through the modules.

## License

MIT License - See [LICENSE](LICENSE) file for details.