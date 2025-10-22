package com.example.langchain4j.prompts.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

/**
 * Structured extraction interface following GPT-5 best practices.
 * 
 * Uses clear system messages with explicit extraction rules
 * for predictable, high-quality outputs.
 */
public interface Gpt5StructuredExtractor {

    /**
     * Extract person information with GPT-5 precision guidelines
     */
    @SystemMessage("""
        You are a precise information extraction system.
        
        <extraction_rules>
        - Extract ONLY explicitly stated information from the text
        - Do not infer, assume, or extrapolate details
        - Use null for any missing information
        - Be consistent with data types and formats
        - Validate extracted data before returning
        - If information is ambiguous, prefer null over guessing
        </extraction_rules>
        
        <output_format>
        Return valid JSON matching the PersonInfo schema exactly.
        All fields must be present in the response, even if null.
        Ensure proper data types for each field.
        </output_format>
        """)
    @UserMessage("""
        Extract person information from this text:
        {{text}}
        """)
    PersonInfo extractPerson(@V("text") String text);

    /**
     * Extract task list with priority and categorization
     */
    @SystemMessage("""
        You are a task extraction specialist.
        
        <extraction_rules>
        - Identify all actionable items in the text
        - Categorize tasks by type (feature, bug, improvement, etc.)
        - Infer priority from context clues (urgent, asap, low priority, etc.)
        - Extract any mentioned deadlines or time constraints
        - Group related tasks together
        </extraction_rules>
        
        <priority_inference>
        - HIGH: Words like "urgent", "critical", "asap", "immediately"
        - MEDIUM: Words like "soon", "important", "should"
        - LOW: Words like "eventually", "nice to have", "consider"
        - Default to MEDIUM if unclear
        </priority_inference>
        """)
    @UserMessage("Extract tasks from: {{text}}")
    TaskList extractTasks(@V("text") String text);

    /**
     * Extract meeting information with comprehensive details
     */
    @SystemMessage("""
        <extraction_guidelines>
        Extract meeting details with high precision:
        
        Date/Time Extraction:
        - Parse various date formats (MM/DD/YYYY, Month DD, etc.)
        - Handle relative dates (today, tomorrow, next week)
        - Extract exact times or time ranges
        - Identify timezone if mentioned
        
        Participant Extraction:
        - List all mentioned attendees
        - Identify the organizer if stated
        - Note if attendance is optional/required
        
        Location Extraction:
        - Physical locations (room numbers, addresses)
        - Virtual locations (Zoom, Teams, meet links)
        - Hybrid meeting indicators
        
        Content Extraction:
        - Main meeting topic/purpose
        - Agenda items if listed
        - Expected outcomes or decisions needed
        </extraction_guidelines>
        """)
    @UserMessage("Extract meeting info from: {{text}}")
    MeetingInfo extractMeeting(@V("text") String text);

    /**
     * Extract code review feedback in structured format
     */
    @SystemMessage("""
        <code_review_extraction>
        You are extracting structured feedback from code review comments.
        
        Categorization:
        - BUG: Actual defects or errors
        - STYLE: Formatting, naming, conventions
        - PERFORMANCE: Efficiency concerns
        - SECURITY: Vulnerabilities or risks
        - DOCUMENTATION: Missing or unclear docs
        - BEST_PRACTICE: Better approaches or patterns
        
        Severity Levels:
        - CRITICAL: Must fix before merge
        - HIGH: Should fix before merge
        - MEDIUM: Fix soon after merge
        - LOW: Nice to have improvement
        
        For each issue found:
        - Extract the file/line if mentioned
        - Capture the concern description
        - Note any suggested fixes
        - Assign appropriate category and severity
        </code_review_extraction>
        """)
    @UserMessage("Extract review feedback from: {{text}}")
    CodeReviewFeedback extractReviewFeedback(@V("text") String text);

    // Data classes for structured outputs

    record PersonInfo(
        String fullName,
        Integer age,
        String occupation,
        String location,
        String email,
        String phone,
        List<String> skills
    ) {}

    record TaskList(
        List<Task> tasks,
        String context
    ) {}

    record Task(
        String title,
        String description,
        Priority priority,
        TaskType type,
        String deadline,
        List<String> tags
    ) {}

    enum Priority {
        HIGH, MEDIUM, LOW
    }

    enum TaskType {
        FEATURE, BUG, IMPROVEMENT, DOCUMENTATION, RESEARCH, OTHER
    }

    record MeetingInfo(
        String title,
        String date,
        String time,
        String timezone,
        String location,
        LocationType locationType,
        List<String> participants,
        String organizer,
        String agenda,
        String purpose,
        Integer durationMinutes
    ) {}

    enum LocationType {
        PHYSICAL, VIRTUAL, HYBRID
    }

    record CodeReviewFeedback(
        List<ReviewIssue> issues,
        String overallSentiment,
        List<String> positiveNotes
    ) {}

    record ReviewIssue(
        String file,
        Integer lineNumber,
        IssueCategory category,
        IssueSeverity severity,
        String description,
        String suggestedFix
    ) {}

    enum IssueCategory {
        BUG, STYLE, PERFORMANCE, SECURITY, DOCUMENTATION, BEST_PRACTICE
    }

    enum IssueSeverity {
        CRITICAL, HIGH, MEDIUM, LOW
    }
}
