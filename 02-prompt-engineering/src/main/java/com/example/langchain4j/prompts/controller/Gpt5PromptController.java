package com.example.langchain4j.prompts.controller;

import com.example.langchain4j.prompts.service.Gpt5PromptService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller demonstrating GPT-5 prompting patterns.
 * 
 * Test these endpoints to see different GPT-5 prompting techniques in action.
 */
@RestController
@RequestMapping("/api/gpt5")
@CrossOrigin(origins = "*")
public class Gpt5PromptController {

    @Autowired
    private Gpt5PromptService gpt5Service;

    /**
     * Quick, focused response (low eagerness)
     * 
     * Example:
     * POST /api/gpt5/focused
     * {"problem": "What is 15% of 200?"}
     */
    @PostMapping("/focused")
    public ResponseEntity<PromptResponse> solveFocused(@RequestBody ProblemRequest request) {
        String result = gpt5Service.solveFocused(request.getProblem());
        return ResponseEntity.ok(new PromptResponse(result));
    }

    /**
     * Thorough, autonomous response (high eagerness)
     * 
     * Example:
     * POST /api/gpt5/autonomous
     * {"problem": "Design a caching strategy for a high-traffic REST API"}
     */
    @PostMapping("/autonomous")
    public ResponseEntity<PromptResponse> solveAutonomous(@RequestBody ProblemRequest request) {
        String result = gpt5Service.solveAutonomous(request.getProblem());
        return ResponseEntity.ok(new PromptResponse(result));
    }

    /**
     * Task execution with progress updates
     * 
     * Example:
     * POST /api/gpt5/task
     * {"task": "Create a Spring Boot REST endpoint for user management"}
     */
    @PostMapping("/task")
    public ResponseEntity<PromptResponse> executeTask(@RequestBody TaskRequest request) {
        String result = gpt5Service.executeWithPreamble(request.getTask());
        return ResponseEntity.ok(new PromptResponse(result));
    }

    /**
     * Generate code with self-reflection
     * 
     * Example:
     * POST /api/gpt5/code
     * {"requirement": "Create a service that validates email addresses"}
     */
    @PostMapping("/code")
    public ResponseEntity<PromptResponse> generateCode(@RequestBody CodeRequest request) {
        String result = gpt5Service.generateCodeWithReflection(request.getRequirement());
        return ResponseEntity.ok(new PromptResponse(result));
    }

    /**
     * Analyze code with structured feedback
     * 
     * Example:
     * POST /api/gpt5/analyze
     * {"code": "public void process(String s) { return s.toLowerCase(); }"}
     */
    @PostMapping("/analyze")
    public ResponseEntity<PromptResponse> analyzeCode(@RequestBody CodeAnalysisRequest request) {
        String result = gpt5Service.analyzeCode(request.getCode());
        return ResponseEntity.ok(new PromptResponse(result));
    }

    /**
     * Multi-turn conversation
     * 
     * Example:
     * POST /api/gpt5/chat
     * {"message": "Tell me about Spring Boot", "sessionId": "user123"}
     */
    @PostMapping("/chat")
    public ResponseEntity<PromptResponse> chat(@RequestBody ChatRequest request) {
        String result = gpt5Service.continueConversation(
            request.getMessage(), 
            request.getSessionId()
        );
        return ResponseEntity.ok(new PromptResponse(result));
    }

    /**
     * Clear a specific chat session
     * 
     * Example:
     * DELETE /api/gpt5/chat/user123
     */
    @DeleteMapping("/chat/{sessionId}")
    public ResponseEntity<String> clearSession(@PathVariable String sessionId) {
        gpt5Service.clearSession(sessionId);
        return ResponseEntity.ok("Session cleared: " + sessionId);
    }

    /**
     * Generate constrained output
     * 
     * Example:
     * POST /api/gpt5/constrained
     * {"topic": "machine learning", "format": "bullet points", "maxWords": 100}
     */
    @PostMapping("/constrained")
    public ResponseEntity<PromptResponse> generateConstrained(@RequestBody ConstrainedRequest request) {
        String result = gpt5Service.generateConstrained(
            request.getTopic(),
            request.getFormat(),
            request.getMaxWords()
        );
        return ResponseEntity.ok(new PromptResponse(result));
    }

    /**
     * Step-by-step reasoning
     * 
     * Example:
     * POST /api/gpt5/reason
     * {"problem": "If a train leaves NYC at 2pm going 60mph, and another leaves Boston at 3pm going 80mph, when do they meet?"}
     */
    @PostMapping("/reason")
    public ResponseEntity<PromptResponse> solveWithReasoning(@RequestBody ProblemRequest request) {
        String result = gpt5Service.solveWithReasoning(request.getProblem());
        return ResponseEntity.ok(new PromptResponse(result));
    }

    // Request/Response DTOs

    @Data
    public static class ProblemRequest {
        private String problem;
    }

    @Data
    public static class TaskRequest {
        private String task;
    }

    @Data
    public static class CodeRequest {
        private String requirement;
    }

    @Data
    public static class CodeAnalysisRequest {
        private String code;
    }

    @Data
    public static class ChatRequest {
        private String message;
        private String sessionId;
    }

    @Data
    public static class ConstrainedRequest {
        private String topic;
        private String format;
        private int maxWords;
    }

    @Data
    public static class PromptResponse {
        private String result;
        private long timestamp;

        public PromptResponse(String result) {
            this.result = result;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
