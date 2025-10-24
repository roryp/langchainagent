# Quick Start

Get LangChain4j running in minutes using GitHub Models - no Azure subscription required. Try basic examples, then move to the Introduction module for the deep dive with GPT-5.

## Prerequisites

- Java 21+, Maven 3.9+
- GitHub Personal Access Token

## Setup

### 1. Get Your GitHub Token

1. Go to [GitHub Settings → Personal Access Tokens](https://github.com/settings/personal-access-tokens)
2. Click "Generate new token"
3. Set a descriptive name (e.g., "LangChain4j Demo")
4. Set expiration (7 days recommended)
5. Under "Account permissions", find "Models" and set to "Read-only"
6. Click "Generate token"
7. Copy and save your token - you won't see it again

### 2. Set Your Token

```bash
export GITHUB_TOKEN="your_token_here"  # macOS/Linux
$env:GITHUB_TOKEN="your_token_here"    # Windows PowerShell
```

## Run the Examples

### 1. Basic Chat

```bash
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.BasicChatDemo"
```

### 2. Prompt Patterns

```bash
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.PromptEngineeringDemo"
```

Shows zero-shot, few-shot, chain-of-thought, and role-based prompting.

### 3. Function Calling

```bash
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.ToolIntegrationDemo"
```

AI automatically calls your Java methods when needed.

### 4. Document Q&A (RAG)

```bash
mvn exec:java -Dexec.mainClass="com.example.langchain4j.quickstart.SimpleReaderDemo"
```

Ask questions about content in `document.txt`.

## What Each Example Shows

**BasicChatDemo** - Send a prompt, get a response. The foundation of everything.

**PromptEngineeringDemo** - Four patterns that improve AI responses:
- Zero-shot: Direct instructions
- Few-shot: Learn from examples  
- Chain-of-thought: Show reasoning steps
- Role-based: Set context and persona

**ToolIntegrationDemo** - Define Java methods with `@Tool` annotation. AI decides when to call them.

**SimpleReaderDemo** - Provide document context in the prompt. AI answers based on that content instead of general knowledge.

## Troubleshooting

**"GITHUB_TOKEN environment variable is not set"**
```bash
export GITHUB_TOKEN="your_token_here"
```

**"401 Unauthorized"**  
Verify your token and check Models permission is set to Read-only.

**Build errors**  
```bash
mvn clean install
```

## Next Steps

Ready for more? The [Introduction module](../01-introduction/README.md) covers conversation memory, architecture patterns, and production considerations using Azure OpenAI's GPT-5.