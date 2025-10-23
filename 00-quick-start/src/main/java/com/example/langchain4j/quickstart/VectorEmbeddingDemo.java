package com.example.langchain4j.quickstart;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;

/**
 * VectorEmbeddingDemo - Understanding Semantic Representations
 * 
 * Embeddings are the foundation of modern AI applications. They convert text into
 * numerical vectors that capture semantic meaning, enabling:
 * - Similarity search and recommendations
 * - Semantic clustering and classification  
 * - RAG (Retrieval Augmented Generation) systems
 * - Content deduplication and matching
 * 
 * This example shows how to generate embeddings and measure semantic similarity.
 * GitHub Models provides OpenAI-compatible embedding endpoints.
 */
public class VectorEmbeddingDemo {

    public static void main(String[] args) {
        // Validate GitHub authentication
        String githubToken = System.getenv("GITHUB_TOKEN");
        if (githubToken == null || githubToken.isEmpty()) {
            System.err.println("ERROR: GITHUB_TOKEN environment variable not configured!");
            System.err.println("   Set it with: export GITHUB_TOKEN=\"your_token_here\"");
            System.exit(1);
        }

        printSectionHeader("Vector Embedding Demonstration");

        // Initialize embedding model with GitHub Models endpoint
        OpenAiEmbeddingModel model = OpenAiEmbeddingModel.builder()
                .baseUrl("https://models.inference.ai.azure.com")
                .apiKey(githubToken)
                .modelName("text-embedding-3-small")  // Generates 1536-dim vectors
                .logRequests(true)
                .logResponses(true)
                .build();

        // Demo 1: Generate a single embedding
        System.out.println("Demo 1: Creating a Text Embedding");
        System.out.println("-".repeat(60));
        String text1 = "Modern AI systems use vector embeddings to understand semantic relationships between concepts";
        System.out.println("Input Text: " + text1);
        System.out.println();

        Response<Embedding> response1 = model.embed(text1);
        Embedding embedding1 = response1.content();
        
        System.out.println("[SUCCESS] Embedding generated successfully!");
        System.out.println("Vector dimensions: " + embedding1.dimension());
        System.out.println();
        System.out.println("First 10 vector components:");
        float[] vector1 = embedding1.vector();
        for (int i = 0; i < 10; i++) {
            System.out.printf("   Position[%d]: %.6f\n", i, vector1[i]);
        }
        System.out.println("   ... (" + (vector1.length - 10) + " more dimensions)");
        System.out.println();

        // Demo 2: Semantic similarity comparison
        System.out.println("Demo 2: Measuring Semantic Similarity");
        System.out.println("-".repeat(60));
        
        String text2 = "Neural networks utilize vector representations for semantic analysis";
        String text3 = "The weather forecast predicts sunny skies this weekend";
        
        System.out.println("Sentence A: " + text1);
        System.out.println("Sentence B: " + text2);
        System.out.println("Sentence C: " + text3);
        System.out.println();

        Response<Embedding> response2 = model.embed(text2);
        Response<Embedding> response3 = model.embed(text3);
        
        Embedding embedding2 = response2.content();
        Embedding embedding3 = response3.content();

        // Compute cosine similarity scores
        double similarityAB = computeCosineSimilarity(embedding1, embedding2);
        double similarityAC = computeCosineSimilarity(embedding1, embedding3);
        double similarityBC = computeCosineSimilarity(embedding2, embedding3);

        System.out.println("\nSimilarity Analysis (range: -1 to +1):");
        System.out.printf("   A <-> B: %.4f  (semantically related)\n", similarityAB);
        System.out.printf("   A <-> C: %.4f  (semantically unrelated)\n", similarityAC);
        System.out.printf("   B <-> C: %.4f  (semantically unrelated)\n", similarityBC);
        System.out.println();

        System.out.println("[SUCCESS] Embedding demonstration complete!");
        System.out.println();
        System.out.println("Key Insights:");
        System.out.println("   - Semantically similar texts have high similarity scores");
        System.out.println("   - Unrelated texts show low similarity scores");
        System.out.println("   - This enables powerful semantic search capabilities!");
    }

    /**
     * Computes cosine similarity between two embedding vectors
     * 
     * Returns a value in [-1, 1] where:
     * - +1 indicates identical semantic meaning
     * - 0 indicates no semantic relationship
     * - -1 indicates opposite meaning (rare in practice)
     * 
     * @param emb1 First embedding vector
     * @param emb2 Second embedding vector
     * @return Cosine similarity score
     */
    private static double computeCosineSimilarity(Embedding emb1, Embedding emb2) {
        float[] vec1 = emb1.vector();
        float[] vec2 = emb2.vector();

        if (vec1.length != vec2.length) {
            throw new IllegalArgumentException("Vector dimensions must match: " 
                + vec1.length + " vs " + vec2.length);
        }

        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            magnitude1 += vec1[i] * vec1[i];
            magnitude2 += vec2[i] * vec2[i];
        }

        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));
    }

    private static void printSectionHeader(String title) {
        System.out.println("=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
        System.out.println();
    }
}
