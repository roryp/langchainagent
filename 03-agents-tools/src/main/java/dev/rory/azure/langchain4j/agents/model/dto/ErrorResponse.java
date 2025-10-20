package dev.rory.azure.langchain4j.agents.model.dto;

/**
 * Error response DTO.
 */
public record ErrorResponse(
    String error,
    String message
) {
}
