package dev.langchain4j.example.mcp;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;

import java.io.File;
import java.util.List;

/**
 * Demonstrates MCP integration via stdio transport for filesystem operations.
 * 
 * Prerequisites:
 * - npm installed
 * - GITHUB_TOKEN environment variable set
 * - Working directory set to mcp-example root
 */
public class McpToolsExampleOverStdio {

    private static final String TARGET_FILE = "src/main/resources/file.txt";
    private static final String GITHUB_MODELS_URL = "https://models.inference.ai.azure.com";
    private static final String MODEL_NAME = "gpt-4o-mini";

    public static void main(String[] args) throws Exception {

        // Configure chat model
        ChatModel chatModel = buildChatModel();

        // Setup stdio transport with filesystem server
        McpTransport stdioTransport = buildStdioTransport();

        // Initialize MCP client
        McpClient client = buildMcpClient(stdioTransport);

        // Create tool provider
        ToolProvider tools = McpToolProvider.builder()
                .mcpClients(List.of(client))
                .build();

        // Build AI service
        Bot assistant = AiServices.builder(Bot.class)
                .chatModel(chatModel)
                .toolProvider(tools)
                .build();

        try {
            File targetFile = new File(TARGET_FILE);
            String query = String.format(
                "Read and summarize the content from: %s", 
                targetFile.getAbsolutePath()
            );
            String result = assistant.chat(query);
            System.out.println("Assistant response: " + result);
        } finally {
            client.close();
        }
    }

    private static ChatModel buildChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(GITHUB_MODELS_URL)
                .apiKey(System.getenv("GITHUB_TOKEN"))
                .modelName(MODEL_NAME)
                .build();
    }

    private static McpTransport buildStdioTransport() {
        // Determine npm command based on OS
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase()
                .contains("win");
        String npmCmd = isWindows ? "npm.cmd" : "npm";

        // Get absolute path to resources directory
        String resourcesDir = new File("src/main/resources")
                .getAbsolutePath();

        return new StdioMcpTransport.Builder()
                .command(List.of(
                    npmCmd, "exec",
                    "@modelcontextprotocol/server-filesystem@0.6.2",
                    resourcesDir
                ))
                .logEvents(true)
                .build();
    }

    private static McpClient buildMcpClient(McpTransport transport) {
        return new DefaultMcpClient.Builder()
                .transport(transport)
                .build();
    }
}
