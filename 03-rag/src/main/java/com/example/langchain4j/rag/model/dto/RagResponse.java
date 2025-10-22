package com.example.langchain4j.rag.model.dto;

import java.util.List;

/**
 * Response DTO for RAG answers with sources.
 */
public record RagResponse(
    String answer,
    String conversationId,
    List<SourceReference> sources
) {
}
