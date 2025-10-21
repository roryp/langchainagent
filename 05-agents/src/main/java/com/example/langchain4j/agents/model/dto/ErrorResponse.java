package com.example.langchain4j.agents.model.dto;

/**
 * Error response DTO.
 */
public record ErrorResponse(
    String error,
    String message
) {
}
