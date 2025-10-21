package com.example.langchain4j.quickstart;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;

/**
 * Embedding Example
 * 
 * Demonstrates how to convert text into numerical vectors (embeddings).
 * Embeddings are fundamental for:
 * - Semantic search
 * - Similarity comparison
 * - RAG (Retrieval Augmented Generation)
 * - Classification and clustering
 * 
 * GitHub Models uses an OpenAI-compatible API.
 */
public class EmbeddingExample {

    public static void main(String[] args) {
        // Check if GitHub token is set
        String githubToken = System.getenv("GITHUB_TOKEN");
        if (githubToken == null || githubToken.isEmpty()) {
            System.err.println("ERROR: GITHUB_TOKEN environment variable is not set!");
            System.err.println("Please set it with: export GITHUB_TOKEN=\"your_token_here\"");
            System.exit(1);
        }

        System.out.println("=".repeat(60));
        System.out.println("Embedding Example with GitHub Models");
        System.out.println("=".repeat(60));
        System.out.println();

        // Create an embedding model using GitHub Models
        OpenAiEmbeddingModel model = OpenAiEmbeddingModel.builder()
                .baseUrl("https://models.inference.ai.azure.com")
                .apiKey(githubToken)
                .modelName("text-embedding-3-small")  // 1536-dimensional embeddings
                .logRequests(true)
                .logResponses(true)
                .build();

        // Example 1: Embed a single text
        System.out.println("Example 1: Single Text Embedding");
        System.out.println("-".repeat(60));
        String text1 = "LangChain4j is a Java framework for building AI applications";
        System.out.println("Text: " + text1);
        System.out.println();

        Response<Embedding> response1 = model.embed(text1);
        Embedding embedding1 = response1.content();
        
        System.out.println("Embedding created successfully!");
        System.out.println("Vector dimensions: " + embedding1.dimension());
        System.out.println();
        System.out.println("First 10 dimensions of the vector:");
        float[] vector1 = embedding1.vector();
        for (int i = 0; i < 10; i++) {
            System.out.printf("  [%d] %.6f\n", i, vector1[i]);
        }
        System.out.println("  ...");
        System.out.println();

        // Example 2: Compare similar texts
        System.out.println("Example 2: Comparing Similar Texts");
        System.out.println("-".repeat(60));
        
        String text2 = "Java AI framework for developers";
        String text3 = "I love eating pizza for dinner";
        
        System.out.println("Text A: " + text1);
        System.out.println("Text B: " + text2);
        System.out.println("Text C: " + text3);
        System.out.println();

        Response<Embedding> response2 = model.embed(text2);
        Response<Embedding> response3 = model.embed(text3);
        
        Embedding embedding2 = response2.content();
        Embedding embedding3 = response3.content();

        // Calculate cosine similarity
        double similarity_AB = cosineSimilarity(embedding1, embedding2);
        double similarity_AC = cosineSimilarity(embedding1, embedding3);
        double similarity_BC = cosineSimilarity(embedding2, embedding3);

        System.out.println("Similarity Scores (cosine similarity, -1 to 1):");
        System.out.printf("  Text A <-> Text B: %.4f (related topics)\n", similarity_AB);
        System.out.printf("  Text A <-> Text C: %.4f (unrelated topics)\n", similarity_AC);
        System.out.printf("  Text B <-> Text C: %.4f (unrelated topics)\n", similarity_BC);
        System.out.println();

        System.out.println("[OK] Embedding examples completed!");
        System.out.println();
        System.out.println("Key insights:");
        System.out.println("   - Similar texts have higher similarity scores");
        System.out.println("   - Unrelated texts have lower similarity scores");
        System.out.println("   - This is the foundation for semantic search!");
    }

    /**
     * Calculate cosine similarity between two embeddings
     * Returns a value between -1 and 1, where:
     * - 1 means identical
     * - 0 means orthogonal (no similarity)
     * - -1 means opposite
     */
    private static double cosineSimilarity(Embedding e1, Embedding e2) {
        float[] v1 = e1.vector();
        float[] v2 = e2.vector();

        if (v1.length != v2.length) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < v1.length; i++) {
            dotProduct += v1[i] * v2[i];
            norm1 += v1[i] * v1[i];
            norm2 += v2[i] * v2[i];
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
