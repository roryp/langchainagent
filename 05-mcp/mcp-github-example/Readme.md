# MCP GitHub Tools Example

This example demonstrates Git repository analysis using LangChain4j with the MCP git server.

## Overview

Uses the Model Context Protocol to connect an LLM to a local Git repository via Docker, enabling repository analysis and commit history queries.

## Features

- Git MCP server integration via Docker
- Stdio transport for local repository access
- Automatic Windows/Unix path handling
- Local repository mounting

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker (installed and running)
- GitHub personal access token

## Setup

### 1. Build the Git MCP Docker Image

```bash
git clone https://github.com/modelcontextprotocol/servers.git
cd servers/src/git
docker build -t mcp/git .
```

### 2. Set Your GitHub Token

```bash
export GITHUB_TOKEN=your_github_token
```

Get a token at: GitHub Settings → Developer settings → Personal access tokens

## Running the Example

```bash
cd mcp-github-example
mvn exec:java -Dexec.mainClass=dev.langchain4j.example.mcp.github.McpGithubToolsExample
```

The example will:
1. Launch the git MCP server in a Docker container
2. Mount your local repository
3. Query the last 3 commits
4. Provide an AI-generated analysis

## How It Works

The example uses GitHub Models (free tier) with your GitHub token:

```java
ChatModel chatModel = OpenAiChatModel.builder()
    .baseUrl("https://models.inference.ai.azure.com")
    .apiKey(System.getenv("GITHUB_TOKEN"))
    .modelName("gpt-4o-mini")
    .build();
```

The MCP client connects to the Docker-based git server:

```java
McpTransport dockerTransport = new StdioMcpTransport.Builder()
    .command(List.of(
        "docker", "run",
        "-e", "GITHUB_PERSONAL_ACCESS_TOKEN=" + System.getenv("GITHUB_TOKEN"),
        "-v", dockerPath + ":/app/LangChain4j-for-Beginners",
        "-i", "mcp/git"
    ))
    .build();
```

## Technical Details

- **Transport**: Stdio via Docker
- **Repository**: Local mount at `/app/LangChain4j-for-Beginners`
- **Model**: gpt-4o-mini via GitHub Models
- **Tools**: Git operations (log, diff, status, etc.)

## Notes

- The example automatically detects Windows/Unix and adjusts paths accordingly
- Repository is mounted read-only for safety
- Working directory should be the project root or the example will adjust paths
