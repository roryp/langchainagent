package com.example.langchain4j.quickstart;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.util.concurrent.CompletableFuture;

/**
 * RealTimeResponseDemo - Streaming Chat Implementation
 * 
 * This demonstrates how to implement streaming responses for a better user experience.
 * Instead of waiting for the complete response, tokens are delivered incrementally
 * as they're generated - similar to the ChatGPT interface.
 * 
 * Benefits of streaming:
 * - Improved perceived performance
 * - Better UX for long-form content
 * - Ability to show progress to users
 * 
 * Uses GitHub Models with OpenAI-compatible streaming API.
 */
public class StreamingResponseDemo {

    public static void main(String[] args) {
        // Verify GitHub token is available
        String githubToken = System.getenv("GITHUB_TOKEN");
        if (githubToken == null || githubToken.isEmpty()) {
            System.err.println("ERROR: GITHUB_TOKEN not found in environment!");
            System.err.println("   Configure with: export GITHUB_TOKEN=\"your_token_here\"");
            System.exit(1);
        }

        displayHeader("Real-Time Streaming Response Demo");

        // Build streaming-capable model instance
        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .baseUrl("https://models.inference.ai.azure.com")
                .apiKey(githubToken)
                .modelName("gpt-4o-mini")
                .logRequests(true)
                .logResponses(true)
                .build();

        String prompt = "Tell me an interesting fact about machine learning and explain why it matters";
        System.out.println("User Input: " + prompt);
        System.out.println();
        System.out.println("Assistant (streaming):");
        System.out.println("-".repeat(60));

        // Use CompletableFuture to handle async streaming
        CompletableFuture<ChatResponse> streamCompletion = new CompletableFuture<>();

        // Initialize streaming with custom handler
        model.chat(prompt, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String token) {
                // Render each token immediately as it arrives
                System.out.print(token);
            }

            @Override
            public void onCompleteResponse(ChatResponse finalResponse) {
                // Streaming finished successfully
                System.out.println();
                System.out.println("-".repeat(60));
                System.out.println();
                System.out.println("[SUCCESS] Stream complete!");
                System.out.printf("Token usage: %d tokens%n", 
                    finalResponse.tokenUsage().totalTokenCount());
                streamCompletion.complete(finalResponse);
            }

            @Override
            public void onError(Throwable error) {
                // Handle streaming errors
                System.err.println();
                System.err.println("ERROR: Streaming error: " + error.getMessage());
                streamCompletion.completeExceptionally(error);
            }
        });

        // Block until streaming completes
        streamCompletion.join();
    }

    private static void displayHeader(String title) {
        System.out.println("=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
        System.out.println();
    }
}
