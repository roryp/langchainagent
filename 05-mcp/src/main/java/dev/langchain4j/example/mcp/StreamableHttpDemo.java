package dev.langchain4j.example.mcp;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;

import java.time.Duration;
import java.util.List;

/**
 * Demonstrates MCP integration via streamable HTTP transport.
 * 
 * Prerequisites:
 * - MCP 'everything' server running on localhost:3001
 * - GITHUB_TOKEN environment variable set
 * 
 * Setup:
 * 1. Clone https://github.com/modelcontextprotocol/servers
 * 2. cd servers/src/everything
 * 3. npm install
 * 4. node dist/streamableHttp.js
 */
public class StreamableHttpDemo {

    private static final String MCP_ENDPOINT = "http://localhost:3001/mcp";
    private static final String GITHUB_MODELS_URL = "https://models.inference.ai.azure.com";
    private static final String MODEL_NAME = "gpt-4o-mini";

    public static void main(String[] args) throws Exception {
        
        // Configure GitHub Models chat
        ChatModel chatModel = buildChatModel();
        
        // Setup HTTP transport for MCP server
        McpTransport httpTransport = buildHttpTransport();
        
        // Initialize MCP client
        McpClient client = buildMcpClient(httpTransport);
        
        // Create tool provider from MCP
        ToolProvider tools = McpToolProvider.builder()
                .mcpClients(List.of(client))
                .build();

        // Build AI service with MCP tools
        Bot assistant = AiServices.builder(Bot.class)
                .chatModel(chatModel)
                .toolProvider(tools)
                .build();
        
        try {
            // Test math operation via MCP tool
            String result = assistant.chat(
                "Calculate 5 plus 12 using the available addition tool"
            );
            System.out.println("Result: " + result);
        } finally {
            client.close();
        }
    }

    private static ChatModel buildChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(GITHUB_MODELS_URL)
                .apiKey(System.getenv("GITHUB_TOKEN"))
                .modelName(MODEL_NAME)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    private static McpTransport buildHttpTransport() {
        return new StreamableHttpMcpTransport.Builder()
                .url(MCP_ENDPOINT)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    private static McpClient buildMcpClient(McpTransport transport) {
        return new DefaultMcpClient.Builder()
                .transport(transport)
                .build();
    }
}
