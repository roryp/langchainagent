package com.example.langchain4j.quickstart;

/**
 * ComprehensiveDemo - Execute All Quick Start Examples
 * 
 * This runner demonstrates the complete capabilities of LangChain4j by executing
 * all quick start examples in sequence. Each example showcases a different aspect
 * of building AI applications with Java.
 * 
 * Covers:
 * - Basic chat interactions
 * - Streaming responses
 * - AI function calling (tools)
 * - Text embeddings and semantic similarity
 */
public class ComprehensiveDemoRunner {

    public static void main(String[] args) {
        displayMainHeader();

        // Verify authentication prerequisites
        String githubToken = System.getenv("GITHUB_TOKEN");
        if (githubToken == null || githubToken.isEmpty()) {
            System.err.println("ERROR: GITHUB_TOKEN environment variable is missing!");
            System.err.println();
            System.err.println("Setup Instructions:");
            System.err.println("  1. Generate token at: https://github.com/settings/tokens");
            System.err.println("  2. Configure environment:");
            System.err.println("     - macOS/Linux: export GITHUB_TOKEN=\"your_token_here\"");
            System.err.println("     - Windows PowerShell: $env:GITHUB_TOKEN=\"your_token_here\"");
            System.err.println();
            System.exit(1);
        }

        System.out.println("[OK] Authentication token verified");
        System.out.println();
        
        try {
            // Demo 1: Basic Chat
            System.out.println();
            System.out.println(">> Executing Demo 1 of 4...");
            System.out.println();
            BasicChatDemo.main(args);
            
            pauseForUser();

            // Demo 2: Streaming Responses
            System.out.println();
            System.out.println(">> Executing Demo 2 of 4...");
            System.out.println();
            StreamingResponseDemo.main(args);
            
            pauseForUser();

            // Demo 3: AI Tool Integration
            System.out.println();
            System.out.println(">> Executing Demo 3 of 4...");
            System.out.println();
            ToolIntegrationDemo.main(args);
            
            pauseForUser();

            // Demo 4: Vector Embeddings
            System.out.println();
            System.out.println(">> Executing Demo 4 of 4...");
            System.out.println();
            VectorEmbeddingDemo.main(args);

            // Completion summary
            displayCompletionSummary();

        } catch (Exception e) {
            System.err.println();
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Pause execution and wait for user input
     */
    private static void pauseForUser() {
        System.out.println();
        System.out.println("Press Enter to proceed to next demo...");
        try {
            System.in.read();
        } catch (Exception e) {
            // Silently ignore read errors
        }
    }

    private static void displayMainHeader() {
        System.out.println("=".repeat(70));
        System.out.println("  LangChain4j Quick Start - Comprehensive Feature Demonstration");
        System.out.println("=".repeat(70));
        System.out.println();
    }

    private static void displayCompletionSummary() {
        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println("  All Demonstrations Completed Successfully!");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("What You've Learned:");
        System.out.println("  * Synchronous chat completion with GPT-4o-mini");
        System.out.println("  * Streaming responses for improved UX");
        System.out.println("  * AI function calling (autonomous tool usage)");
        System.out.println("  * Text embeddings for semantic search");
        System.out.println();
        System.out.println("Continue Your Journey:");
        System.out.println("  - Module 01: Azure OpenAI Integration");
        System.out.println("  - Module 02: Advanced Prompt Engineering");
        System.out.println("  - Module 03: RAG Implementation");
        System.out.println();
        System.out.println("Additional Resources:");
        System.out.println("  - LangChain4j Documentation: https://docs.langchain4j.dev/");
        System.out.println("  - GitHub Models Guide: https://docs.github.com/en/models");
        System.out.println();
    }
}
