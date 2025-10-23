package com.example.langchain4j.prompts.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service demonstrating GPT-5 prompting best practices with LangChain4j.
 * 
 * Based on OpenAI's GPT-5 Prompting Guide:
 * https://github.com/openai/openai-cookbook/blob/main/examples/gpt-5/gpt-5_prompting_guide.ipynb
 */
@Service
public class Gpt5PromptService {

    @Autowired
    private AzureOpenAiChatModel chatModel;

    private final Map<String, ChatMemory> sessionMemories = new HashMap<>();

    /**
     * Example 1: Low Eagerness - Quick, focused responses
     * Use when you want fast, direct answers without deep exploration.
     */
    public String solveFocused(String problem) {
        String prompt = """
            <context_gathering>
            - Search depth: very low
            - Bias strongly towards providing a correct answer as quickly as possible
            - Usually, this means an absolute maximum of 2 reasoning steps
            - If you think you need more time, state what you know and what's uncertain
            </context_gathering>
            
            Problem: %s
            
            Provide your answer:
            """.formatted(problem);

        return chatModel.chat(prompt);
    }

    /**
     * Example 2: High Eagerness - Thorough, autonomous problem solving
     * Use for complex tasks where you want the model to explore thoroughly.
     */
    public String solveAutonomous(String problem) {
        String prompt = """
            Solve this problem thoroughly. Document any assumptions you make.
            
            Problem: %s
            
            Provide your complete solution:
            """.formatted(problem);

        return chatModel.chat(prompt);
    }

    /**
     * Example 3: Task Execution with Progress Updates
     * Provides clear preambles and progress updates for better UX.
     */
    public String executeWithPreamble(String task) {
        String prompt = """
            <task_execution>
            1. First, briefly restate the user's goal in a friendly way
            
            2. Create a step-by-step plan:
               - List all steps needed
               - Identify potential challenges
               - Outline success criteria
            
            3. Execute each step:
               - Narrate what you're doing
               - Show progress clearly
               - Handle any issues that arise
            
            4. Summarize:
               - What was completed
               - Any important notes
               - Next steps if applicable
            </task_execution>
            
            <tool_preambles>
            - Always begin by rephrasing the user's goal clearly
            - Outline your plan before executing
            - Narrate each step as you go
            - Finish with a distinct summary
            </tool_preambles>
            
            Task: %s
            
            Begin execution:
            """.formatted(task);

        return chatModel.chat(prompt);
    }

    /**
     * Example 4: Self-Reflecting Code Generation
     * Generates high-quality code using internal quality rubrics.
     */
    public String generateCodeWithReflection(String requirement) {
        String prompt = """
            <self_reflection>
            You are generating production-quality Java code.
            
            Internal rubric (do not show to user):
            1. Correctness: Does it solve the exact requirement?
            2. Quality: Is it clean, readable, and well-structured?
            3. Best Practices: Does it follow Java/Spring Boot conventions?
            4. Maintainability: Is it easy to modify and extend?
            5. Error Handling: Does it handle edge cases properly?
            6. Documentation: Is it properly documented?
            7. Testing: Can it be easily tested?
            
            Iterate internally until the solution scores high on all criteria.
            Only then provide your answer.
            </self_reflection>
            
            <code_standards>
            <guiding_principles>
            - Clarity and Reuse: Code should be modular and reusable
            - Consistency: Follow Spring Boot and Java conventions
            - Simplicity: Favor small, focused methods
            - Quality: Include proper JavaDoc, error handling, and logging
            </guiding_principles>
            
            <java_spring_boot_standards>
            - Use @RestController for REST APIs, @Service for business logic
            - Implement proper exception handling with @ExceptionHandler
            - Use Lombok annotations (@Data, @Builder) to reduce boilerplate
            - Follow constructor-based dependency injection
            - Include JavaDoc for public methods
            - Use meaningful variable and method names
            - Handle null cases and edge conditions
            </java_spring_boot_standards>
            </code_standards>
            
            Requirement: %s
            
            Generate the code:
            """.formatted(requirement);

        return chatModel.chat(prompt);
    }

    /**
     * Example 5: Structured Analysis with Clear Instructions
     * Analyzes code with specific criteria and structured output.
     */
    public String analyzeCode(String code) {
        String prompt = """
            <analysis_framework>
            You are an expert code reviewer. Analyze the code for:
            
            1. Correctness
               - Does it work as intended?
               - Are there logical errors?
            
            2. Best Practices
               - Follows language conventions?
               - Appropriate design patterns?
            
            3. Performance
               - Any inefficiencies?
               - Scalability concerns?
            
            4. Security
               - Potential vulnerabilities?
               - Input validation?
            
            5. Maintainability
               - Code clarity?
               - Documentation?
            
            <output_format>
            Provide your analysis in this structure:
            - Summary: One-sentence overall assessment
            - Strengths: 2-3 positive points
            - Issues: List any problems found with severity (High/Medium/Low)
            - Recommendations: Specific improvements
            </output_format>
            </analysis_framework>
            
            Code to analyze:
            ```
            %s
            ```
            
            Provide your structured analysis:
            """.formatted(code);

        return chatModel.chat(prompt);
    }

    /**
     * Example 6: Multi-Turn Conversation with Context Preservation
     * Maintains conversation context following GPT-5 patterns.
     */
    public String continueConversation(String userMessage, String sessionId) {
        // Get or create chat memory for this session
        ChatMemory chatMemory = sessionMemories.computeIfAbsent(
            sessionId, 
            k -> MessageWindowChatMemory.withMaxMessages(10)
        );

        // Add system message on first interaction
        if (chatMemory.messages().isEmpty()) {
            SystemMessage systemMsg = SystemMessage.from("""
                You are a helpful assistant in an ongoing conversation.
                
                <conversation_guidelines>
                - Remember previous context from this session
                - Build on earlier discussion points naturally
                - Ask clarifying questions when truly needed
                - Maintain consistent personality and tone throughout
                - Reference prior exchanges when relevant
                </conversation_guidelines>
                
                <response_style>
                - Be concise but complete
                - Use examples when they help understanding
                - Format responses clearly with proper structure
                - Acknowledge the user's previous messages when relevant
                - Show that you understand the conversation flow
                </response_style>
                
                <tool_preambles>
                - If you need to perform multiple steps, outline your plan first
                - Provide updates as you work through complex requests
                - Summarize what you've done at the end
                </tool_preambles>
                """);
            chatMemory.add(systemMsg);
        }

        // Add user message
        chatMemory.add(UserMessage.from(userMessage));

        // Generate response using chat() for string list
        String response = chatModel.chat(buildContextString(chatMemory));
        
        // Store assistant's response
        chatMemory.add(AiMessage.from(response));

        return response;
    }
    
    /**
     * Helper method to build context string from chat memory
     */
    private String buildContextString(ChatMemory chatMemory) {
        StringBuilder context = new StringBuilder();
        chatMemory.messages().forEach(msg -> {
            if (msg instanceof SystemMessage) {
                context.append("System: ").append(((SystemMessage)msg).text()).append("\n\n");
            } else if (msg instanceof UserMessage) {
                context.append("User: ").append(((UserMessage)msg).singleText()).append("\n\n");
            } else if (msg instanceof AiMessage) {
                context.append("Assistant: ").append(((AiMessage)msg).text()).append("\n\n");
            }
        });
        return context.toString();
    }

    /**
     * Example 7: Constrained Output Generation
     * Generates output that strictly adheres to constraints.
     */
    public String generateConstrained(String topic, String format, int maxWords) {
        String prompt = """
            <strict_constraints>
            You MUST adhere to these constraints:
            - Topic: %s
            - Format: %s
            - Maximum words: %d
            - Do NOT exceed the word limit
            - Do NOT deviate from the specified format
            </strict_constraints>
            
            <quality_requirements>
            Within the constraints:
            - Be informative and accurate
            - Use clear, professional language
            - Organize content logically
            - Include relevant details
            </quality_requirements>
            
            Generate the content:
            """.formatted(topic, format, maxWords);

        return chatModel.chat(prompt);
    }

    /**
     * Example 8: Step-by-Step Reasoning
     * Encourages explicit reasoning process.
     */
    public String solveWithReasoning(String problem) {
        String prompt = """
            <reasoning_approach>
            Solve this problem by thinking step by step.
            
            Process:
            1. Understand: Restate the problem in your own words
            2. Analyze: Break down the components
            3. Plan: Outline your solution approach
            4. Execute: Work through each step
            5. Verify: Check your answer makes sense
            
            Show your reasoning clearly at each step.
            </reasoning_approach>
            
            Problem: %s
            
            Let's solve this step by step:
            """.formatted(problem);

        return chatModel.chat(prompt);
    }

    /**
     * Clear session memory for a specific session.
     */
    public void clearSession(String sessionId) {
        sessionMemories.remove(sessionId);
    }

    /**
     * Clear all session memories.
     */
    public void clearAllSessions() {
        sessionMemories.clear();
    }
}
