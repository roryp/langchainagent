package com.example.langchain4j.quickstart;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.util.concurrent.CompletableFuture;

/**
 * Streaming Chat Example
 * 
 * Demonstrates streaming responses where tokens arrive word-by-word,
 * similar to how ChatGPT displays responses. This provides a better
 * user experience for longer responses.
 * 
 * GitHub Models uses an OpenAI-compatible API.
 */
public class StreamingChatExample {

    public static void main(String[] args) {
        // Check if GitHub token is set
        String githubToken = System.getenv("GITHUB_TOKEN");
        if (githubToken == null || githubToken.isEmpty()) {
            System.err.println("ERROR: GITHUB_TOKEN environment variable is not set!");
            System.err.println("Please set it with: export GITHUB_TOKEN=\"your_token_here\"");
            System.exit(1);
        }

        System.out.println("=".repeat(60));
        System.out.println("Streaming Chat Example with GitHub Models");
        System.out.println("=".repeat(60));
        System.out.println();

        // Create a streaming chat model using GitHub Models
        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .baseUrl("https://models.inference.ai.azure.com")
                .apiKey(githubToken)
                .modelName("gpt-4o-mini")
                .logRequests(true)
                .logResponses(true)
                .build();

        String prompt = "Write a short haiku about Java programming";
        System.out.println("Prompt: " + prompt);
        System.out.println();
        System.out.println("Streaming Response:");
        System.out.println("-".repeat(60));

        // Create a future to wait for completion
        CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();

        // Start streaming
        model.chat(prompt, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                // Print each token as it arrives (like ChatGPT)
                System.out.print(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                // Called when streaming is complete
                System.out.println();
                System.out.println("-".repeat(60));
                System.out.println();
                System.out.println("[OK] Streaming completed!");
                System.out.println("Total tokens used: " + completeResponse.tokenUsage().totalTokenCount());
                futureResponse.complete(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                // Handle any errors
                System.err.println();
                System.err.println("ERROR: " + error.getMessage());
                futureResponse.completeExceptionally(error);
            }
        });

        // Wait for streaming to complete
        futureResponse.join();
    }
}
