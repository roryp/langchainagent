package com.example.langchain4j.agents.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import com.example.langchain4j.agents.model.dto.AgentRequest;
import com.example.langchain4j.agents.model.dto.AgentResponse;
import com.example.langchain4j.agents.model.dto.ToolExecutionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Agent service using LangChain4j with Azure OpenAI and HTTP tool calling.
 * Implements ReAct pattern: Reason → Act → Observe → Repeat
 */
@Service
public class AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentService.class);
    
    private final AzureOpenAiChatModel chatModel;
    private final String toolsBaseUrl;
    private final RestTemplate restTemplate;
    
    // Session management
    private final ConcurrentHashMap<String, ChatMemory> sessionMemories = new ConcurrentHashMap<>();

    public AgentService(
            @Value("${azure.openai.endpoint}") String endpoint,
            @Value("${azure.openai.key}") String apiKey,
            @Value("${azure.openai.deployment}") String deployment,
            @Value("${azure.ai.agent.tools.base-url}") String toolsBaseUrl) {
        
        this.toolsBaseUrl = toolsBaseUrl;
        this.restTemplate = new RestTemplate();
        
        log.info("Initializing Agent Service with Azure OpenAI");
        log.info("Endpoint: {}", endpoint);
        log.info("Deployment: {}", deployment);
        log.info("Tools Base URL: {}", toolsBaseUrl);
        
        // Initialize Azure OpenAI chat model
        // GPT-5 uses reasoning effort instead of temperature
        this.chatModel = AzureOpenAiChatModel.builder()
            .endpoint(endpoint)
            .apiKey(apiKey)
            .deploymentName(deployment)
            .maxRetries(3)
            .logRequestsAndResponses(true)
            .build();
        
        log.info("Agent service initialized successfully");
    }

    /**
     * Create a new agent session.
     */
    public String createAgentSession() {
        String sessionId = UUID.randomUUID().toString();
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(20);
        sessionMemories.put(sessionId, memory);
        log.info("Created new agent session: {}", sessionId);
        return sessionId;
    }

    /**
     * Execute an agent task using ReAct pattern with tool calling.
     */
    public AgentResponse executeTask(AgentRequest request) {
        log.info("Executing agent task: {}", request.message());
        
        String sessionId = request.sessionId();
        if (sessionId == null) {
            sessionId = createAgentSession();
        }
        
        ChatMemory memory = sessionMemories.computeIfAbsent(
            sessionId,
            id -> MessageWindowChatMemory.withMaxMessages(20)
        );
        
        try {
            List<ToolExecutionInfo> toolExecutions = new ArrayList<>();
            
            // Build system prompt with tool descriptions
            String systemPrompt = buildSystemPromptWithTools();
            
            // Add user message to memory
            UserMessage userMessage = UserMessage.from(systemPrompt + "\n\nUser: " + request.message());
            memory.add(userMessage);
            
            // Get response from model
            String fullContext = buildContext(memory);
            String response = chatModel.chat(fullContext);
            
            memory.add(AiMessage.from(response));
            
            // Parse response for tool calls and execute them
            String finalAnswer = response;
            if (responseContainsToolCall(response)) {
                log.info("Response contains tool calls, processing...");
                finalAnswer = processToolCalls(response, memory, toolExecutions, sessionId);
            }
            
            log.info("Agent completed task. Tools used: {}", toolExecutions.size());
            
            return new AgentResponse(
                finalAnswer,
                sessionId,
                toolExecutions,
                "completed"
            );
            
        } catch (Exception e) {
            log.error("Agent task execution failed", e);
            return new AgentResponse(
                "I encountered an error: " + e.getMessage(),
                sessionId,
                new ArrayList<>(),
                "failed"
            );
        }
    }

    /**
     * Build system prompt with available tools.
     */
    private String buildSystemPromptWithTools() {
        return """
            You are a helpful AI assistant with access to the following tools:
            
            1. getCurrentWeather(location: string) - Get current weather for a location
            2. getWeatherForecast(location: string, days: int) - Get weather forecast
            3. add(a: number, b: number) - Add two numbers
            4. subtract(a: number, b: number) - Subtract two numbers
            5. multiply(a: number, b: number) - Multiply two numbers
            6. divide(a: number, b: number) - Divide two numbers
            
            When you need to use a tool, respond with:
            TOOL_CALL: <tool_name>(<param1>=<value1>, <param2>=<value2>)
            
            After getting the tool result, provide your final answer to the user.
            If you don't need a tool, just answer directly.
            """;
    }

    /**
     * Build context from memory.
     */
    private String buildContext(ChatMemory memory) {
        StringBuilder context = new StringBuilder();
        for (ChatMessage msg : memory.messages()) {
            if (msg instanceof UserMessage um) {
                context.append("User: ").append(um.singleText()).append("\n");
            } else if (msg instanceof AiMessage am) {
                context.append("Assistant: ").append(am.text()).append("\n");
            }
        }
        return context.toString();
    }

    /**
     * Check if response contains a tool call.
     */
    private boolean responseContainsToolCall(String response) {
        return response.contains("TOOL_CALL:");
    }

    /**
     * Process tool calls in the response with support for multi-step execution.
     */
    private String processToolCalls(String response, ChatMemory memory, 
                                   List<ToolExecutionInfo> toolExecutions, String sessionId) {
        int maxIterations = 5; // Prevent infinite loops
        int iteration = 0;
        String currentResponse = response;
        
        while (responseContainsToolCall(currentResponse) && iteration < maxIterations) {
            iteration++;
            log.info("Tool execution iteration: {}", iteration);
            
            Pattern pattern = Pattern.compile("TOOL_CALL:\\s*(\\w+)\\(([^)]+)\\)");
            Matcher matcher = pattern.matcher(currentResponse);
            
            StringBuilder results = new StringBuilder();
            boolean toolsExecuted = false;
            
            while (matcher.find()) {
                String toolName = matcher.group(1);
                String params = matcher.group(2);
                
                log.info("Executing tool: {} with params: {}", toolName, params);
                
                try {
                    String result = executeToolByName(toolName, params);
                    results.append("Tool ").append(toolName).append(" result: ").append(result).append("\n");
                    
                    toolExecutions.add(new ToolExecutionInfo(
                        toolName,
                        List.of(params),
                        result
                    ));
                    toolsExecuted = true;
                } catch (Exception e) {
                    log.error("Tool execution failed", e);
                    results.append("Tool error: ").append(e.getMessage()).append("\n");
                }
            }
            
            // If tools were executed, get next response from model
            if (toolsExecuted) {
                String prompt = "Tool results:\n" + results.toString() + 
                              "\n\nUse these results to continue. If you need more tools, call them. Otherwise, provide your final answer.";
                memory.add(UserMessage.from(prompt));
                currentResponse = chatModel.chat(buildContext(memory));
                memory.add(AiMessage.from(currentResponse));
                log.info("Model response after tools (iteration {}): {}", iteration, currentResponse);
            } else {
                break; // No tools found in this iteration
            }
        }
        
        if (iteration >= maxIterations) {
            log.warn("Max iterations reached for tool execution");
        }
        
        return currentResponse;
    }

    /**
     * Execute a tool by name with parameters.
     */
    private String executeToolByName(String toolName, String params) {
        Map<String, String> paramMap = parseParams(params);
        
        // Build request body and URL
        Map<String, Object> requestBody = new HashMap<>();
        String path = switch (toolName) {
            case "getCurrentWeather" -> {
                requestBody.put("location", paramMap.get("location"));
                yield "/api/tools/weather/current";
            }
            case "getWeatherForecast" -> {
                requestBody.put("location", paramMap.get("location"));
                requestBody.put("days", Integer.parseInt(paramMap.get("days")));
                yield "/api/tools/weather/forecast";
            }
            case "add" -> {
                requestBody.put("a", Double.parseDouble(paramMap.get("a")));
                requestBody.put("b", Double.parseDouble(paramMap.get("b")));
                yield "/api/tools/calculator/add";
            }
            case "subtract" -> {
                requestBody.put("a", Double.parseDouble(paramMap.get("a")));
                requestBody.put("b", Double.parseDouble(paramMap.get("b")));
                yield "/api/tools/calculator/subtract";
            }
            case "multiply" -> {
                requestBody.put("a", Double.parseDouble(paramMap.get("a")));
                requestBody.put("b", Double.parseDouble(paramMap.get("b")));
                yield "/api/tools/calculator/multiply";
            }
            case "divide" -> {
                requestBody.put("a", Double.parseDouble(paramMap.get("a")));
                requestBody.put("b", Double.parseDouble(paramMap.get("b")));
                yield "/api/tools/calculator/divide";
            }
            default -> throw new IllegalArgumentException("Unknown tool: " + toolName);
        };
        
        String url = toolsBaseUrl + path;
        log.info("Calling tool endpoint: {} with body: {}", url, requestBody);
        return restTemplate.postForObject(url, requestBody, String.class);
    }

    /**
     * Parse parameter string into map.
     */
    private Map<String, String> parseParams(String params) {
        Map<String, String> paramMap = new HashMap<>();
        String[] pairs = params.split(",");
        for (String pair : pairs) {
            String[] kv = pair.trim().split("=");
            if (kv.length == 2) {
                paramMap.put(kv[0].trim(), kv[1].trim().replace("\"", ""));
            }
        }
        return paramMap;
    }

    /**
     * Simple chat for health checks.
     */
    public String simpleChat(String message) {
        try {
            return chatModel.chat(message);
        } catch (Exception e) {
            log.error("Simple chat failed", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Get available tools.
     */
    public List<String> getAvailableTools() {
        return List.of(
            "getCurrentWeather - Get current weather for a location",
            "getWeatherForecast - Get weather forecast",
            "add - Add two numbers",
            "subtract - Subtract two numbers",
            "multiply - Multiply two numbers",
            "divide - Divide two numbers"
        );
    }

    /**
     * Clear agent session.
     */
    public void clearSession(String sessionId) {
        sessionMemories.remove(sessionId);
        log.info("Cleared session: {}", sessionId);
    }
}
