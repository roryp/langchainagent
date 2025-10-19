package dev.rory.azure.langchain4j.rag.service;

import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.rory.azure.langchain4j.rag.model.dto.RagRequest;
import dev.rory.azure.langchain4j.rag.model.dto.RagResponse;
import dev.rory.azure.langchain4j.rag.model.dto.SourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for Retrieval-Augmented Generation (RAG).
 * Simplified version that uses basic chat without retrieval.
 */
@Service
public class RagService {

    private static final Logger log = LoggerFactory.getLogger(RagService.class);

    private final AzureOpenAiChatModel chatModel;

    public RagService(AzureOpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Answer a question using the chat model.
     * Note: This is a simplified version without actual retrieval.
     *
     * @param request RAG request with question
     * @return RAG response with answer
     */
    public RagResponse ask(RagRequest request) {
        log.info("Processing RAG request: '{}'", request.question());

        try {
            // Generate answer using chat model
            String answer = chatModel.chat(request.question());

            // Create empty source list (retrieval not yet implemented)
            List<SourceReference> sources = new ArrayList<>();

            return new RagResponse(
                answer,
                request.conversationId(),
                sources
            );

        } catch (Exception e) {
            log.error("RAG processing failed", e);
            throw new RuntimeException("Failed to process question: " + e.getMessage(), e);
        }
    }
}
