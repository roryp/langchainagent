package com.example.langchain4j.quickstart;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/**
 * Function Calling Example
 * 
 * Demonstrates how to give the AI access to Java methods (tools).
 * The AI can automatically decide when to call these methods and
 * extract the required parameters from the user's question.
 * 
 * GitHub Models uses an OpenAI-compatible API.
 */
public class FunctionCallingExample {

    /**
     * Calculator tool with methods the AI can use
     */
    static class Calculator {
        
        @Tool("Add two numbers together")
        public double add(double a, double b) {
            System.out.println("[TOOL] add(" + a + ", " + b + ")");
            return a + b;
        }

        @Tool("Subtract the second number from the first")
        public double subtract(double a, double b) {
            System.out.println("[TOOL] subtract(" + a + ", " + b + ")");
            return a - b;
        }

        @Tool("Multiply two numbers")
        public double multiply(double a, double b) {
            System.out.println("[TOOL] multiply(" + a + ", " + b + ")");
            return a * b;
        }

        @Tool("Divide the first number by the second")
        public double divide(double a, double b) {
            System.out.println("[TOOL] divide(" + a + ", " + b + ")");
            if (b == 0) {
                throw new IllegalArgumentException("Cannot divide by zero");
            }
            return a / b;
        }

        @Tool("Calculate the square root of a number")
        public double squareRoot(double number) {
            System.out.println("[TOOL] squareRoot(" + number + ")");
            return Math.sqrt(number);
        }
    }

    /**
     * AI Service interface
     * The AI will implement this interface and use tools as needed
     */
    interface MathAssistant {
        String chat(String message);
    }

    public static void main(String[] args) {
        // Check if GitHub token is set
        String githubToken = System.getenv("GITHUB_TOKEN");
        if (githubToken == null || githubToken.isEmpty()) {
            System.err.println("ERROR: GITHUB_TOKEN environment variable is not set!");
            System.err.println("Please set it with: export GITHUB_TOKEN=\"your_token_here\"");
            System.exit(1);
        }

        System.out.println("=".repeat(60));
        System.out.println("Function Calling Example with GitHub Models");
        System.out.println("=".repeat(60));
        System.out.println();

        // Create the chat model using GitHub Models
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("https://models.inference.ai.azure.com")
                .apiKey(githubToken)
                .modelName("gpt-4o-mini")
                .build();

        // Create an AI service with calculator tools
        MathAssistant assistant = AiServices.builder(MathAssistant.class)
                .chatModel(model)
                .tools(new Calculator())  // Give AI access to calculator
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        // Example 1: Simple calculation
        System.out.println("Example 1: Simple Addition");
        System.out.println("-".repeat(60));
        String question1 = "What is 25 plus 17?";
        System.out.println("Question: " + question1);
        System.out.println();
        String answer1 = assistant.chat(question1);
        System.out.println("Assistant: " + answer1);
        System.out.println();

        // Example 2: Multi-step calculation
        System.out.println("Example 2: Multi-step Calculation");
        System.out.println("-".repeat(60));
        String question2 = "If I have 100 dollars and spend 25% of it, how much do I have left?";
        System.out.println("Question: " + question2);
        System.out.println();
        String answer2 = assistant.chat(question2);
        System.out.println("Assistant: " + answer2);
        System.out.println();

        // Example 3: Complex calculation
        System.out.println("Example 3: Using Square Root");
        System.out.println("-".repeat(60));
        String question3 = "What is the square root of 144?";
        System.out.println("Question: " + question3);
        System.out.println();
        String answer3 = assistant.chat(question3);
        System.out.println("Assistant: " + answer3);
        System.out.println();

        System.out.println("[OK] All examples completed successfully!");
        System.out.println();
        System.out.println("Notice how the AI:");
        System.out.println("   - Automatically chose the right tools");
        System.out.println("   - Extracted parameters from natural language");
        System.out.println("   - Chained multiple operations together");
    }
}
