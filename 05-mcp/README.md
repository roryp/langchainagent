# Module 05 - Model Context Protocol (MCP)

Connect LangChain4j applications to MCP servers for standardized tool integration.

## What is MCP?

Model Context Protocol (MCP) is an open standard that lets LLM applications connect to external data sources and tools through a standardized interface.

## Projects

### 1. mcp-example
Basic examples showing HTTP and stdio transport mechanisms.

**Run it:**
```bash
cd mcp-example
export GITHUB_TOKEN=your_github_token
mvn exec:java -Dexec.mainClass=dev.langchain4j.example.mcp.McpToolsExampleOverHttp
```

### 2. mcp-github-example
Real-world example using GitHub MCP server to summarize repository commits.

**Run it:**
```bash
cd mcp-github-example

# First, build the GitHub MCP Docker image
git clone https://github.com/modelcontextprotocol/servers.git
cd servers/src/github
docker build -t mcp/git .

# Then run the example
cd ../../../mcp-github-example
export GITHUB_TOKEN=your_github_token
mvn exec:java -Dexec.mainClass=dev.langchain4j.example.mcp.github.McpGithubToolsExample
```

See [mcp-github-example/Readme.md](mcp-github-example/Readme.md) for more details.

## Prerequisites

- Java 21+
- Maven 3.9+
- npm (for stdio example)
- Docker
- GitHub personal access token

### Getting a GitHub Token

1. Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Click "Generate new token (classic)"
3. Give it a descriptive name (e.g., "LangChain4j MCP Examples")
4. No specific scopes are required for GitHub Models API access
5. Click "Generate token" and copy the token
6. Set it as an environment variable:
   ```bash
   export GITHUB_TOKEN=your_token_here
   ```

## How It Works

MCP connects your application to external tools:

```
Your App → MCP Client → MCP Server → Tools (GitHub, Files, etc.)
```

**Transport Options:**
- **HTTP** - Network-based, good for remote servers
- **Stdio** - Process-based, good for local tools

## Key Code Example

```java
// Setup chat model with GitHub Models
ChatModel model = OpenAiChatModel.builder()
    .baseUrl("https://models.inference.ai.azure.com")
    .apiKey(System.getenv("GITHUB_TOKEN"))
    .modelName("gpt-4o-mini")
    .build();

// Setup MCP client
McpClient client = McpClient.builder()
    .transport(httpTransport)
    .build();

// Create tool provider
McpToolProvider toolProvider = McpToolProvider.builder()
    .mcpClient(client)
    .build();

// Use with AI assistant
AiServices.builder(Bot.class)
    .chatLanguageModel(model)
    .tools(toolProvider)
    .build();
```

## Getting Started

### 1. MCP Example (HTTP/Stdio)

```bash
cd mcp-example

# Set your GitHub token
export GITHUB_TOKEN=your_github_token

# Run HTTP transport example (requires MCP server running on localhost:3001)
mvn exec:java -Dexec.mainClass=dev.langchain4j.example.mcp.McpToolsExampleOverHttp

# Run stdio transport example (requires npm installed)
mvn exec:java -Dexec.mainClass=dev.langchain4j.example.mcp.McpToolsExampleOverStdio
```

**Note:** The stdio example automatically detects your OS and uses the appropriate npm command (npm.cmd on Windows, npm on Linux/Mac).

### 2. GitHub MCP Example

```bash
cd mcp-github-example

# Build GitHub MCP Docker image first
git clone https://github.com/modelcontextprotocol/servers.git
cd servers/src/github
docker build -t mcp/git .

# Return to project and run
cd ../../../mcp-github-example
export GITHUB_TOKEN=your_github_token
mvn exec:java -Dexec.mainClass=dev.langchain4j.example.mcp.github.McpGithubToolsExample
```

## MCP Architecture

```
┌─────────────────────────────────────┐
│  LangChain4j Application            │
│  ┌───────────────────────────────┐  │
│  │  AI Assistant (Bot)           │  │
│  └───────────────────────────────┘  │
│              ↓                      │
│  ┌───────────────────────────────┐  │
│  │  MCP Client                   │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
              ↓ (HTTP/stdio)
┌─────────────────────────────────────┐
│  MCP Server                         │
│  ┌───────────────────────────────┐  │
│  │  Tool Providers               │  │
│  │  - File operations            │  │
│  │  - GitHub API                 │  │
│  │  - Custom tools               │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
```

## Configuration

### GitHub Models Setup

All examples now use GitHub Models instead of OpenAI directly:

```java
ChatModel model = OpenAiChatModel.builder()
    .baseUrl("https://models.inference.ai.azure.com")
    .apiKey(System.getenv("GITHUB_TOKEN"))
    .modelName("gpt-4o-mini")
    .build();
```

**Benefits:**
- Free tier available for developers
- Uses your GitHub personal access token
- Same OpenAI models, different endpoint
- No separate OpenAI API key needed

### MCP Server Configuration

Configure MCP servers in your application:

```java
McpClient client = McpClient.builder()
    .transport(httpTransport)  // or stdioTransport
    .build();

McpToolProvider toolProvider = McpToolProvider.builder()
    .mcpClient(client)
    .build();
```

### Tool Registration

Register MCP tools with your AI assistant:

```java
AiServices.builder(Bot.class)
    .chatLanguageModel(model)
    .tools(toolProvider)
    .build();
```

## Dependencies

Key dependencies used in this module:

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

## Troubleshooting

### Docker Issues

**Error:** "Cannot connect to Docker daemon"
- Ensure Docker is running
- Check Docker socket permissions
- Verify Docker path in code

**Error:** "Image mcp/git not found"
- Build the GitHub MCP Docker image
- Follow setup instructions in mcp-github-example

### Connection Issues

**Error:** "MCP server not responding"
- Check server is running
- Verify transport configuration
- Review server logs

**Error:** "Tool execution failed"
- Check tool parameters
- Verify permissions
- Review error messages
