package com.example.langchain4j.quickstart;

/**
 * Run All Examples
 * 
 * Convenience class to run all quickstart examples in sequence.
 * This demonstrates the key features of LangChain4j with GitHub Models.
 */
public class RunAllExamples {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  LangChain4j Quick Start - GitHub Models Demo");
        System.out.println("=".repeat(60));
        System.out.println();

        // Check if GitHub token is set
        String githubToken = System.getenv("GITHUB_TOKEN");
        if (githubToken == null || githubToken.isEmpty()) {
            System.err.println("ERROR: GITHUB_TOKEN environment variable is not set!");
            System.err.println();
            System.err.println("To fix this:");
            System.err.println("  1. Get a GitHub token: https://github.com/settings/tokens");
            System.err.println("  2. Set environment variable:");
            System.err.println("     - macOS/Linux: export GITHUB_TOKEN=\"your_token_here\"");
            System.err.println("     - Windows:     $env:GITHUB_TOKEN=\"your_token_here\"");
            System.err.println();
            System.exit(1);
        }

        System.out.println("[OK] GitHub token found");
        System.out.println();
        
        try {
            // Example 1: Simple Chat
            System.out.println();
            System.out.println("Running Example 1 of 4...");
            System.out.println();
            SimpleChatExample.main(args);
            
            waitForUser();

            // Example 2: Streaming Chat
            System.out.println();
            System.out.println("Running Example 2 of 4...");
            System.out.println();
            StreamingChatExample.main(args);
            
            waitForUser();

            // Example 3: Function Calling
            System.out.println();
            System.out.println("Running Example 3 of 4...");
            System.out.println();
            FunctionCallingExample.main(args);
            
            waitForUser();

            // Example 4: Embeddings
            System.out.println();
            System.out.println("Running Example 4 of 4...");
            System.out.println();
            EmbeddingExample.main(args);

            // Summary
            System.out.println();
            System.out.println("=".repeat(60));
            System.out.println("  All Examples Completed Successfully!");
            System.out.println("=".repeat(60));
            System.out.println();
            System.out.println("What you learned:");
            System.out.println("  * Simple chat completion with GPT-4o-mini");
            System.out.println("  * Streaming responses for better UX");
            System.out.println("  * Function calling (AI calling your Java methods)");
            System.out.println("  * Text embeddings for semantic search");
            System.out.println();
            System.out.println("Next steps:");
            System.out.println("  - Check out Module 01: Introduction with Azure OpenAI");
            System.out.println("  - Check out Module 02: Prompt Engineering");
            System.out.println("  - Check out Module 03: RAG (Retrieval Augmented Generation)");
            System.out.println();
            System.out.println("Resources:");
            System.out.println("  - LangChain4j Docs: https://docs.langchain4j.dev/");
            System.out.println("  - GitHub Models: https://docs.github.com/en/models");
            System.out.println();

        } catch (Exception e) {
            System.err.println();
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Wait for user to press Enter before continuing
     */
    private static void waitForUser() {
        System.out.println();
        System.out.println("Press Enter to continue to the next example...");
        try {
            System.in.read();
        } catch (Exception e) {
            // Ignore
        }
    }
}
