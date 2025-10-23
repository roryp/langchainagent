# Module 05 - Model Context Protocol (MCP)

Integrate external tools and data sources into LangChain4j applications using the Model Context Protocol standard.

## Overview

The Model Context Protocol provides a standardized way for LLM applications to interact with external tools and data sources. This module demonstrates three working examples using different transport mechanisms and practical integrations.

## What You'll Build

Three working examples in a single project demonstrating MCP integration:

**Example 1: HTTP Transport (HttpTransportDemo)**
- HTTP/SSE transport connecting to a remote MCP server
- Demonstrates network-based tool integration
- Calls remote calculator tools

**Example 2: Stdio Transport (StdioTransportDemo)**
- Stdio transport spawning a local filesystem server
- Cross-platform subprocess management
- File reading operations

**Example 3: Git Repository Analysis (GitRepositoryAnalyzer)**
- Docker-based MCP git server integration
- Local repository mounting and analysis
- Commit history queries via AI assistant

## Requirements

**Software:**
- Java 21 or later
- Maven 3.9+
- npm (for filesystem operations)
- Docker (for Git integration)

**API Access:**
- GitHub personal access token (for GitHub Models API)

**Token Setup:**
Refer to the [Quick Start guide](../00-quick-start/README.md#1-get-your-github-token) for GitHub token creation instructions.

```bash
export GITHUB_TOKEN=your_token_value
```

## Quick Start

### Example 1: HTTP Transport

Start the MCP server first:

```bash
# Get the MCP servers repository
git clone https://github.com/modelcontextprotocol/servers.git
cd servers/src/everything
npm install
node dist/sse.js  # Runs on port 3001
```

In a separate terminal, run the client:

```bash
cd 05-mcp
export GITHUB_TOKEN=your_token
mvn exec:java -Dexec.mainClass=dev.langchain4j.example.mcp.HttpTransportDemo
```

### Example 2: Stdio Transport

This example launches the MCP server automatically:

```bash
cd 05-mcp
export GITHUB_TOKEN=your_token
mvn exec:java -Dexec.mainClass=dev.langchain4j.example.mcp.StdioTransportDemo
```

The code detects your OS and uses the correct npm command automatically.

### Example 3: Git Repository Analysis

Build the Docker image once:

```bash
git clone https://github.com/modelcontextprotocol/servers.git
cd servers/src/git
docker build -t mcp/git .
```

Run the example:

```bash
cd 05-mcp
export GITHUB_TOKEN=your_token
mvn exec:java -Dexec.mainClass=dev.langchain4j.example.mcp.GitRepositoryAnalyzer
```

This mounts your local repository and analyzes recent commits.

## Architecture

MCP enables communication between your application and external tool servers:

```
Application Layer
├── LangChain4j Bot (AI Assistant)
└── MCP Client
    │
    ├── HTTP Transport ──→ Remote MCP Servers
    └── Stdio Transport ──→ Local MCP Servers
                              │
                              ├── Filesystem Tools
                              ├── Git Operations
                              └── Custom Tools
```

**Transport Comparison:**

| Transport | Use Case | Connection | Example |
|-----------|----------|------------|---------|
| HTTP/SSE | Remote servers | Network socket | Calculator, API services |
| Stdio | Local processes | stdin/stdout | File access, Git commands |

## Implementation Guide

### Step 1: Configure the Chat Model

Using GitHub Models (free tier) for LLM access:

```java
ChatModel chatModel = OpenAiChatModel.builder()
    .baseUrl("https://models.inference.ai.azure.com")
    .apiKey(System.getenv("GITHUB_TOKEN"))
    .modelName("gpt-4o-mini")
    .build();
```

### Step 2: Setup MCP Transport

Choose HTTP or stdio based on your needs:

```java
// Option A: HTTP transport for remote servers
McpTransport httpTransport = new HttpMcpTransport.Builder()
    .sseUrl("http://localhost:3001/sse")
    .timeout(Duration.ofSeconds(60))
    .build();

// Option B: Stdio transport for local tools
McpTransport stdioTransport = new StdioMcpTransport.Builder()
    .command(List.of("npm", "exec", "mcp-server-package"))
    .build();
```

### Step 3: Initialize MCP Client

```java
McpClient client = new DefaultMcpClient.Builder()
    .transport(httpTransport)  // or stdioTransport
    .build();
```

### Step 4: Create Tool Provider

```java
ToolProvider tools = McpToolProvider.builder()
    .mcpClients(List.of(client))
    .build();
```

### Step 5: Build AI Assistant

```java
Bot assistant = AiServices.builder(Bot.class)
    .chatModel(chatModel)
    .toolProvider(tools)
    .build();

// Use the assistant
String response = assistant.chat("Your query here");
```

## Project Structure

```
05-mcp/
├── pom.xml
├── src/main/java/dev/langchain4j/example/mcp/
│   ├── HttpTransportDemo.java          # HTTP/SSE transport
│   ├── StdioTransportDemo.java         # Stdio transport
│   ├── GitRepositoryAnalyzer.java      # Git analysis
│   └── Bot.java                        # Chat interface
└── src/main/resources/
    └── file.txt                        # Sample file for stdio example
```

**What each example does:**

**HTTP Example:**
- Connects to remote MCP server via HTTP/SSE
- Calls addition tool to perform calculations
- Demonstrates network-based tool integration

**Stdio Example:**
- Spawns local filesystem MCP server as subprocess
- Reads file contents from local directory
- Handles cross-platform npm command differences

**Git Example:**
- Launches Git MCP server in Docker container
- Mounts local repository (read-only)
- Queries commit history and provides AI analysis
- Automatic path conversion for Windows/Unix

## Maven Dependencies

Add these to your `pom.xml`:

```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-mcp</artifactId>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
</dependency>
```

## Common Issues

**Docker won't start container**
- Verify Docker daemon is running: `docker ps`
- Check image exists: `docker images | grep mcp/git`
- Rebuild if needed: `cd servers/src/git && docker build -t mcp/git .`

**MCP server connection fails**
- HTTP: Confirm server is running on port 3001
- Stdio: Verify npm is in system PATH
- Check firewall isn't blocking connections

**Tool execution errors**
- Review tool parameter requirements
- Check filesystem permissions (stdio transport)
- Verify repository path is correct (Git example)

**GitHub Models API issues**
- Confirm GITHUB_TOKEN environment variable is set
- Check token hasn't expired
- Verify token permissions (Models should be Read-only)

The base URL `https://models.inference.ai.azure.com` routes to Azure's OpenAI deployment, providing the same model behavior with GitHub authentication.

## Congratulations!

You've completed the LangChain4j for Beginners course! You've learned:

- LangChain4j fundamentals and chat models
- Prompt engineering techniques
- RAG (Retrieval-Augmented Generation) implementation
- AI tools and function calling
- Model Context Protocol (MCP) integration

### What's Next?

- Review the [main course overview](../README.md) to revisit any topics
- Explore the [official LangChain4j documentation](https://docs.langchain4j.dev/)
- Build your own AI applications using what you've learned
- Share your projects and contribute to the community

Thank you for completing this course!
