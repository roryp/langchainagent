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

## How This Uses LangChain4j

This module builds on the quick start by adding Spring Boot and conversation memory. Here's what LangChain4j brings:

**Dependencies** - Two core libraries work together:
```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-azure-open-ai</artifactId>
</dependency>
```

**AzureOpenAiChatModel** - Instead of the generic `OpenAiChatModel` from the quick start, we use `AzureOpenAiChatModel` which connects directly to Azure OpenAI. Spring Boot configures it as a bean using your Azure credentials (endpoint, API key, deployment name).

**MessageWindowChatMemory** - The key component for stateful conversations. Create it with `MessageWindowChatMemory.withMaxMessages(10)` to retain the last 10 messages. The service stores one memory instance per conversation ID, allowing multiple users to chat simultaneously without mixing contexts.

**Message Types** - LangChain4j uses typed messages: `UserMessage.from(text)` for user input and `AiMessage.from(text)` for AI responses. Add these to memory with `memory.add(message)` and retrieve the full history with `memory.messages()`. This structure makes it easy to build conversation context before sending to the model.

The stateless chat endpoint skips memory entirely - just `chatModel.chat(prompt)` like the quick start. The stateful endpoint adds messages to memory, retrieves history, and includes that context with each request. Same model, different patterns.

## What This Module Covers

You'll build two applications that demonstrate the difference:

**Stateless Chat** - Each request is independent. The model has no memory of previous messages. This is the pattern you used in the quick start.

**Stateful Conversation** - Each request includes conversation history. The model maintains context across multiple turns. This is what production applications require.

Both run locally using Azure OpenAI's GPT-5 deployment. You'll see the same Spring Boot application expose both patterns through different API endpoints.

## Prerequisites

- Azure subscription with Azure OpenAI access
- Java 21, Maven 3.9+ 
- Azure CLI (https://learn.microsoft.com/en-us/cli/azure/install-azure-cli)
- azd CLI (https://learn.microsoft.com/en-us/azure/developer/azure-developer-cli/install-azd)

## Deploy Azure OpenAI Infrastructure

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

Open http://localhost:8080 in your browser.

## Using the Application

The application provides a web interface with two chat implementations side-by-side.

**Stateless Chat (Left Panel)**

Try this first. Ask "My name is John" and then immediately ask "What's my name?" The model won't remember because each message is independent. This demonstrates the core problem with basic language model integration - no conversation context.

**Stateful Chat (Right Panel)**

Now try the same sequence here. Ask "My name is John" and then "What's my name?" This time it remembers. The difference is MessageWindowChatMemory - it maintains conversation history and includes it with each request. This is how production conversational AI works.

Both panels use the same GPT-5 model. The only difference is memory. This makes it clear what memory brings to your application and why it's essential for real use cases.

## Next Steps

**Next Module:** [02-prompt-engineering - Prompt Engineering with GPT-5](../02-prompt-engineering/README.md)
