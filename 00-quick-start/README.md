# Quick Start with GitHub Models

Get started with LangChain4j using GitHub Models - no Azure subscription required.

## Overview

This quickstart demonstrates LangChain4j with GitHub Models:

- Simple chat completion
- Prompt engineering patterns
- Function calling (AI calling your Java methods)
- Simple RAG (Retrieval-Augmented Generation) demo

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
3. Under Personal access tokens, click Fine-grained tokens (or follow this [direct link](https://github.com/settings/personal-access-tokens)).
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

### 2. Prompt Engineering Patterns

Learn fundamental prompting techniques that improve AI responses:

```java
OpenAiChatModel model = OpenAiChatModel.builder()
    .baseUrl("https://models.inference.ai.azure.com")
    .apiKey(System.getenv("GITHUB_TOKEN"))
    .modelName("gpt-4o-mini")
    .temperature(0.7)
    .build();

// Pattern 1: Zero-shot - Direct instruction
String response = model.chat("Classify this sentiment: 'I loved it!'");

// Pattern 2: Few-shot - Learn from examples
String fewShotPrompt = """
    Classify sentiment as positive, negative, or neutral.
    
    Examples:
    Text: "Amazing product!" → Positive
    Text: "It's okay." → Neutral
    Text: "Waste of money." → Negative
    
    Now classify: "Best purchase ever!"
    """;

// Pattern 3: Chain of thought - Show reasoning
String cotPrompt = """
    Problem: A store has 15 apples. They sell 8 and receive 12 more.
    How many do they have now?
    
    Let's solve this step-by-step:
    """;

// Pattern 4: Role-based - Set persona/context
String rolePrompt = """
    You are an experienced software architect reviewing code.
    Provide a brief code review for this function: ...
    """;
```

Run:
```bash
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.PromptEngineeringDemo"
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

### 4. Simple RAG Demo

Ask questions about a document using Retrieval-Augmented Generation:

```java
// Load document content
String doc = Files.readString(Paths.get("document.txt"));

// Build the model
OpenAiChatModel chatModel = OpenAiChatModel.builder()
    .baseUrl("https://models.inference.ai.azure.com")
    .apiKey(System.getenv("GITHUB_TOKEN"))
    .modelName("gpt-4o-mini")
    .build();

// Create prompt with context
String prompt = String.format("""
    You are a helpful assistant. Use only the CONTEXT to answer.
    
    CONTEXT:
    \"\"\"
    %s
    \"\"\"
    
    QUESTION:
    %s
    """, doc, question);

// Get answer based on document context
String response = chatModel.chat(prompt);
```

Run:
```bash
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.SimpleReaderDemo"
```

## Project Structure

```
00-quick-start/
├── pom.xml
├── README.md
├── document.txt
└── src/main/java/com/example/langchain4j/quickstart/
    ├── BasicChatDemo.java
    ├── PromptEngineeringDemo.java
    ├── ToolIntegrationDemo.java
    └── SimpleReaderDemo.java
```

## Key Concepts

**Chat Models**
- Simple, synchronous chat completion
- Ideal for quick Q&A and demonstrations
- Uses gpt-4o-mini for fast responses with good rate limits

**Prompt Engineering**
- Zero-shot: Direct instructions without examples
- Few-shot: Learn from provided examples
- Chain of thought: Request step-by-step reasoning
- Role-based: Set context and persona for specialized responses

**Tools/Function Calling**
- AI can call your Java methods
- AI decides which tool to use
- Automatic parameter extraction

**RAG (Retrieval-Augmented Generation)**
- Provide context to the AI from your documents
- AI answers based on specific information, not just training data
- Prevents hallucination by grounding responses in facts

**Memory**
- MessageWindowChatMemory: Remember last N messages
- Enables multi-turn conversations

## Prompt Engineering Patterns Explained

This quickstart demonstrates 4 fundamental patterns that improve AI responses:

1. **Zero-Shot Prompting** - Give direct instructions without examples
   - Best for: Simple, well-defined tasks
   - Example: "Classify this sentiment: 'I loved it!'"

2. **Few-Shot Prompting** - Provide examples to guide behavior
   - Best for: Tasks requiring specific format or style
   - Example: Show 3 sentiment classifications, then ask for a new one

3. **Chain of Thought** - Ask the model to explain its reasoning
   - Best for: Complex problems requiring logic
   - Example: Math problems, multi-step reasoning

4. **Role-Based Prompting** - Set context and persona
   - Best for: Specialized tasks requiring domain expertise
   - Example: "You are a software architect reviewing code..."

These patterns are simpler than the advanced techniques in module 02, making them ideal for beginners.

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