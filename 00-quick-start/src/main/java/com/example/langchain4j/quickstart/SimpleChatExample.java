package com.example.langchain4j.quickstart;

import dev.langchain4j.model.openai.OpenAiChatModel;

/**
 * Simple Chat Example
 * 
 * Demonstrates basic question-answer interaction with GitHub Models.
 * This is the simplest way to use LangChain4j - just ask a question and get an answer.
 * 
 * GitHub Models uses an OpenAI-compatible API, so we use OpenAiChatModel
 * with a custom base URL pointing to GitHub's endpoint.
 */
public class SimpleChatExample {

    public static void main(String[] args) {
        // Check if GitHub token is set
        String githubToken = System.getenv("GITHUB_TOKEN");
        if (githubToken == null || githubToken.isEmpty()) {
            System.err.println("ERROR: GITHUB_TOKEN environment variable is not set!");
            System.err.println("Please set it with: export GITHUB_TOKEN=\"your_token_here\"");
            System.exit(1);
        }

        System.out.println("=".repeat(60));
        System.out.println("Simple Chat Example with GitHub Models");
        System.out.println("=".repeat(60));
        System.out.println();

        // Create a chat model using GitHub Models (gpt-4o-mini - Azure OpenAI variant)
        // GitHub Models uses OpenAI-compatible API
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("https://models.inference.ai.azure.com")
                .apiKey(githubToken)
                .modelName("gpt-4o-mini")  // Using gpt-4o-mini
                .logRequests(true)  // Show API calls in console
                .logResponses(true)
                .build();

        // Ask a question
        String question = "Explain LangChain4j in 3 short bullet points";
        System.out.println("Question: " + question);
        System.out.println();

        // Get the response
        String response = model.chat(question);

        // Print the answer
        System.out.println("Answer:");
        System.out.println(response);
        System.out.println();
        System.out.println("[OK] Example completed successfully!");
    }
}
