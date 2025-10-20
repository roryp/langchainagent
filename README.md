# LangChain4j for Azure — From Zero to Production

This repository hosts a multi‑module Java course to teach you
how to build AI‑powered applications using LangChain4j and deploy them
to Azure.  Each numbered module contains a set of labs and sample code
illustrating the core patterns for working with large language models.

## Modules

- **01‑getting‑started** – wire up a minimal Spring Boot chat app
  powered by LangChain4j with Azure OpenAI.  Learn how to configure
  endpoints, API keys and deploy your first container.
- **02‑rag** – add retrieval‑augmented generation (RAG) to your bot
  by indexing your own data and serving relevant knowledge.  Compare
  Qdrant with Azure AI Search for vector storage.
- **03‑agents‑tools** – explore agents
- **04‑production** – wrap up the course by adding observability,
  evaluation harnesses, content safety and real deployment patterns.

## Important

🚫 **Do not commit secrets**.  Configuration values like API keys
belong in a local `.env` file.  An `.env.example` file is provided as a
template; copy it to `.env` and fill in your own settings.
