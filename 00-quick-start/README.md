# Quick Start with GitHub Models

Get started with LangChain4j using GitHub Models - no Azure subscription required.

## Overview

This quickstart demonstrates LangChain4j with GitHub Models:

- Simple chat completion
- Streaming responses
- Function calling (AI calling your Java methods)
- Text embeddings

## Why GitHub Models?

- Free (no credit card required)
- Easy setup (just need a GitHub account)
- Quick start (perfect for learning)
- OpenAI-compatible API

## Prerequisites

- Java 21+
- Maven 3.9+
- GitHub Personal Access Token

## Setup

### 1. Get Your GitHub Token

1. Go to: https://github.com/settings/tokens
2. Click "Generate new token (classic)"
3. Give it a name (e.g., "LangChain4j Demo")
4. No scopes needed - generate with default settings
5. Copy the token

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
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.SimpleChatExample"
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
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.SimpleChatExample"
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
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.StreamingChatExample"
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
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.FunctionCallingExample"
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
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.EmbeddingExample"
```

## Run All Examples

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.RunAllExamples"
```

## Project Structure

```
00-quick-start/
├── pom.xml
├── README.md
└── src/main/java/com/example/langchain4j/quickstart/
    ├── SimpleChatExample.java
    ├── StreamingChatExample.java
    ├── FunctionCallingExample.java
    ├── EmbeddingExample.java
    └── RunAllExamples.java
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
- Generate new token at https://github.com/settings/tokens
- No special scopes needed

**Build Errors**

```bash
mvn clean install
```

## Next Steps

**Next Module:** [01-introduction - Getting Started with LangChain4j](../01-introduction/README.md)