# Quick Start with GitHub Models

Get started with LangChain4j using GitHub Models - no Azure subscription required.

## Overview

This quickstart demonstrates LangChain4j with GitHub Models:

- Simple chat completion
- Streaming responses
- Function calling (AI calling your Java methods)
- Text embeddings

## Why GitHub Models?

- Free tier available (no credit card required)
- Straightforward setup (GitHub account only)
- No additional configuration (suitable for learning)
- OpenAI-compatible API

## Prerequisites

- Java 21+
- Maven 3.9+
- GitHub Personal Access Token

## Setup

### 1. Get Your GitHub Token

1. Navigate to GitHub Settings and select Settings from your profile menu.
2. In the left sidebar, click Developer settings (usually at the bottom).
3. Under Personal access tokens, click Fine-grained tokens (or follow this [direct link](https://github.com/settings/tokens?type=beta)).
4. Click Generate new token.
5. Under "Token name", provide a descriptive name (e.g., "LangChain4j Demo").
6. Set an expiration date (recommended: 7 days for security best practices).
7. Under "Resource owner", select your user account.
8. Under "Repository access", select the repositories you want to use with GitHub Models (or "All repositories" if needed).
9. Under "Account permissions", find Models and set it to Read-only.
10. Click Generate token.
11. Copy and save your token now – you won't see it again!

**Security Tip:** Use the minimum required scope and shortest practical expiration time for your access tokens.

### 2. Set Environment Variable

Windows (PowerShell):
```powershell
$env:GITHUB_TOKEN="your_token_here"
```

macOS/Linux:
```bash
export GITHUB_TOKEN="your_token_here"
```

### 3. Run Examples

```bash
cd 00-quick-start
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.ComprehensiveDemoRunner"
```

## Examples

### 1. Simple Chat

Basic question-answer with GPT-4o-mini:

```java
OpenAiChatModel model = OpenAiChatModel.builder()
    .baseUrl("https://models.inference.ai.azure.com")
    .apiKey(System.getenv("GITHUB_TOKEN"))
    .modelName("gpt-4o-mini")
    .build();

String response = model.chat("Explain LangChain4j in 3 bullet points");
System.out.println(response);
```

Run:
```bash
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.BasicChatDemo"
```

### 2. Streaming Chat

Get responses word-by-word:

```java
OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
    .baseUrl("https://models.inference.ai.azure.com")
    .apiKey(System.getenv("GITHUB_TOKEN"))
    .modelName("gpt-4o-mini")
    .build();

model.chat("Write a haiku about Java", new StreamingChatResponseHandler() {
    @Override
    public void onPartialResponse(String token) {
        System.out.print(token);
    }
    
    @Override
    public void onCompleteResponse(ChatResponse response) {
        System.out.println("\nComplete!");
    }
    
    @Override
    public void onError(Throwable error) {
        System.err.println("Error: " + error);
    }
}).join();
```

Run:
```bash
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.StreamingResponseDemo"
```

### 3. Function Calling

Let AI call your Java methods:

```java
// Define tools
class Calculator {
    @Tool("Add two numbers")
    public double add(double a, double b) {
        return a + b;
    }
}

// Create AI service
interface MathAssistant {
    String chat(String message);
}

MathAssistant assistant = AiServices.builder(MathAssistant.class)
    .chatModel(model)
    .tools(new Calculator())
    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
    .build();

// Use it - AI automatically calls tools
String answer = assistant.chat("What is 25 + 17?");
```

Run:
```bash
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.ToolIntegrationDemo"
```

### 4. Embeddings

Convert text to vectors for semantic search:

```java
OpenAiEmbeddingModel model = OpenAiEmbeddingModel.builder()
    .baseUrl("https://models.inference.ai.azure.com")
    .apiKey(System.getenv("GITHUB_TOKEN"))
    .modelName("text-embedding-3-small")
    .build();

Embedding embedding = model.embed("Your text here").content();
float[] vector = embedding.vector(); // 1536 dimensions
```

Run:
```bash
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.VectorEmbeddingDemo"
```

## Run All Examples

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.ComprehensiveDemoRunner"
```

## Project Structure

```
00-quick-start/
├── pom.xml
├── README.md
└── src/main/java/com/example/langchain4j/quickstart/
    ├── BasicChatDemo.java
    ├── StreamingResponseDemo.java
    ├── ToolIntegrationDemo.java
    ├── VectorEmbeddingDemo.java
    └── ComprehensiveDemoRunner.java
```

## Key Concepts

**Chat Models**
- Non-streaming: Get complete response at once
- Streaming: Get response word-by-word

**Tools/Function Calling**
- AI can call your Java methods
- AI decides which tool to use
- Automatic parameter extraction

**Embeddings**
- Convert text to numerical vectors
- Enables semantic search
- Foundation for RAG

**Memory**
- MessageWindowChatMemory: Remember last N messages
- Enables multi-turn conversations

## Troubleshooting

**"GITHUB_TOKEN environment variable is not set"**

Set your token:
```bash
export GITHUB_TOKEN="your_token_here"  # macOS/Linux
$env:GITHUB_TOKEN="your_token_here"    # Windows PowerShell
```

**"401 Unauthorized"**

- Verify token is correct
- Refer to token setup instructions above
- Ensure Models permission is set to Read-only

**Build Errors**

```bash
mvn clean install
```

## Next Steps

**Next Module:** [01-introduction - Getting Started with LangChain4j](../01-introduction/README.md)