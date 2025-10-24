# Module 05 - Model Context Protocol (MCP)

Integrate external tools and data sources into LangChain4j applications using the Model Context Protocol standard.

## Overview

The Model Context Protocol provides a standardized way for LLM applications to interact with external tools and data sources. This module demonstrates three working examples using different transport mechanisms and practical integrations.

## What You'll Build

Three working examples in a single project demonstrating MCP integration:

**Example 1: Streamable HTTP Transport (StreamableHttpDemo)**
- Streamable HTTP transport connecting to a remote MCP server
- Demonstrates network-based tool integration via HTTP POST requests
- Uses Server-Sent Events (SSE) for streaming responses
- Calls remote calculator and utility tools

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

### Example 1: Streamable HTTP Transport

**Step 1: Clone the MCP servers repository**

```bash
git clone https://github.com/modelcontextprotocol/servers.git
cd servers/src/everything
```

**Step 2: Install dependencies**

```bash
npm install
```

**Step 3: Start the MCP server in streamable HTTP mode**

```bash
node dist/streamableHttp.js
```

You should see:
```
Starting Streamable HTTP server...
MCP Streamable HTTP Server listening on port 3001
```

**Step 4: In a new terminal, set your GitHub token**

```bash
export GITHUB_TOKEN=your_token_here
```

**Step 5: Run the streamable HTTP transport demo**

```bash
cd 05-mcp
mvn exec:java -Dexec.mainClass=dev.langchain4j.example.mcp.StreamableHttpDemo
```

Expected output:
```
Session initialized with ID: [session-id]
Result: The sum of 5 and 12 is 17.
```

### Example 2: Stdio Transport

**Step 1: Set your GitHub token**

```bash
export GITHUB_TOKEN=your_token_here
```

**Step 2: Run the stdio transport demo**

```bash
cd 05-mcp
mvn exec:java -Dexec.mainClass=dev.langchain4j.example.mcp.StdioTransportDemo
```

This example launches the MCP filesystem server automatically as a subprocess. The code detects your OS and uses the correct npm command (Windows uses `npm.cmd`, Unix uses `npm`).

### Example 3: Git Repository Analysis

**Step 1: Clone the MCP servers repository (if not already done)**

```bash
git clone https://github.com/modelcontextprotocol/servers.git
cd servers/src/git
```

**Step 2: Build the Docker image**

```bash
docker build -t mcp/git .
```

**Step 3: Set your GitHub token**

```bash
export GITHUB_TOKEN=your_token_here
```

**Step 4: Run the Git analyzer**

```bash
cd 05-mcp
mvn exec:java -Dexec.mainClass=dev.langchain4j.example.mcp.GitRepositoryAnalyzer
```

This example:
- Launches the MCP Git server in a Docker container
- Mounts your local repository in read-only mode
- Queries recent commits via AI
- Automatically converts Windows paths to Unix format for Docker

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

| Transport | Use Case | Connection | Server Mode | Example |
|-----------|----------|------------|-------------|---------|
| StreamableHTTP | Remote servers | HTTP POST + SSE | `node dist/streamableHttp.js` | Calculator, API services |
| Stdio | Local processes | stdin/stdout | Subprocess | File access, Git commands |

## Implementation Guide

### Step 1: Configure the Chat Model

Using GitHub Models API for LLM access:

```java
ChatModel chatModel = OpenAiChatModel.builder()
    .baseUrl("https://models.github.ai/inference")
    .apiKey(System.getenv("GITHUB_TOKEN"))
    .modelName("gpt-4.1-nano")
    .build();
```

### Step 2: Setup MCP Transport

Choose HTTP or stdio based on your needs:

```java
// Option A: Streamable HTTP transport for remote servers
McpTransport httpTransport = new StreamableHttpMcpTransport.Builder()
    .url("http://localhost:3001/mcp")
    .timeout(Duration.ofSeconds(60))
    .logRequests(true)   // Optional: see traffic in logs
    .logResponses(true)  // Optional: see responses
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
│   ├── StreamableHttpDemo.java         # Streamable HTTP transport
│   ├── StdioTransportDemo.java         # Stdio transport
│   ├── GitRepositoryAnalyzer.java      # Git analysis
│   └── Bot.java                        # Chat interface
└── src/main/resources/
    └── file.txt                        # Sample file for stdio example
```

**What each example does:**

**Streamable HTTP Example:**
- Connects to remote MCP server via streamable HTTP transport
- Uses Server-Sent Events (SSE) for real-time communication
- Discovers 12 available tools (echo, add, longRunningOperation, etc.)
- Calls addition tool to perform calculations (5 + 12 = 17)
- Demonstrates network-based tool integration with session management

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

**MCP server won't start**
- Verify npm is installed: `npm --version`
- Check Node.js version: `node --version` (requires Node.js 16+)
- Navigate to correct directory: `cd servers/src/everything`
- Install dependencies: `npm install`

**Server connection fails**
- HTTP: Confirm server is running: `curl http://localhost:3001/mcp`
- Check server output: Should say "MCP Streamable HTTP Server listening on port 3001"
- Stdio: Verify npm is in system PATH
- Check firewall isn't blocking port 3001

**"Session initialized" but no result**
- Verify GITHUB_TOKEN is set: `echo $GITHUB_TOKEN`
- Check token hasn't expired (create new one if needed)
- Ensure Models permission is set to Read-only
- Review logs for API rate limit errors

**Tool execution errors**
- Review tool parameter requirements in server logs
- Check filesystem permissions (stdio transport)
- Verify repository path is correct (Git example)
- Ensure Docker has permission to mount volumes

**Docker-specific issues**
- Verify Docker daemon is running: `docker ps`
- Check image exists: `docker images | grep mcp/git`
- Rebuild if needed: `cd servers/src/git && docker build -t mcp/git .`
- On Windows, ensure Docker Desktop is running

**GitHub Models API issues**
- Confirm GITHUB_TOKEN environment variable is set
- Check token hasn't expired
- Verify token permissions (Models should be Read-only)
- Ensure you're using the current endpoint: `https://models.github.ai/inference`

**Note:** The previous Azure endpoint (`https://models.inference.ai.azure.com`) was deprecated in July 2025 and sunset in October 2025. All examples now use the current GitHub Models API endpoint.

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

Thank you for completing this course!
