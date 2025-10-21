# 03 - Prompt Engineering

Learn the art and science of crafting effective prompts for large language models!

## What You'll Learn

- **Prompt Templates**: Create reusable, parameterized prompts
- **Few-Shot Learning**: Teach models through examples
- **Output Parsers**: Structure and validate LLM responses
- **System Messages**: Set behavior and constraints
- **Advanced Techniques**: Chain-of-thought, role-playing, and more

## Learning Objectives

By the end of this module, you will be able to:
- Design effective prompts for various tasks
- Use templates to create dynamic, reusable prompts
- Extract structured data from LLM responses
- Apply few-shot learning techniques
- Understand prompt engineering best practices

---

## Concepts

### What is Prompt Engineering?

**Prompt Engineering** is the practice of designing and refining input text (prompts) to get desired outputs from language models. Good prompts can dramatically improve:
- Response quality
- Consistency
- Task-specific performance
- Output structure

### Key Components

1. **System Message**: Sets the AI's role and behavior
2. **User Message**: The actual question or task
3. **Examples** (Few-shot): Sample inputs/outputs for learning
4. **Context**: Background information needed for the task
5. **Instructions**: Specific formatting or constraints

---

## Project Structure

```
03-prompt-engineering/
 src/main/java/com/example/langchain4j/prompts/
    app/
       Application.java           # Main Spring Boot application
       PromptController.java      # REST API endpoints
       PromptService.java         # Prompt examples
    config/
       LangChainConfig.java       # LangChain4j configuration
    model/
        PromptRequest.java         # Request DTOs
        PromptResponse.java        # Response DTOs
        Person.java                # Example data class
 src/main/resources/
    application.yaml               # Configuration
    prompts/
        email-template.txt         # Email generation prompt
        code-reviewer.txt          # Code review prompt
        data-extractor.txt         # Data extraction prompt
 pom.xml
 README.md
```

---

## Quick Start

### Prerequisites

Complete [Module 00: Course Setup](../00-course-setup/README.md) and set environment variables.

### 1. Build the Project

```bash
cd 03-prompt-engineering
mvn clean package
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The server starts at `http://localhost:8083`

### 3. Test Endpoints

```bash
# Basic prompt template
curl -X POST http://localhost:8083/api/prompts/template \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Alice",
    "topic": "machine learning"
  }'

# Few-shot learning
curl -X POST http://localhost:8083/api/prompts/few-shot \
  -H 'Content-Type: application/json' \
  -d '{"text": "The quick brown fox"}'

# Structured output
curl -X POST http://localhost:8083/api/prompts/extract \
  -H 'Content-Type: application/json' \
  -d '{"text": "John Doe is 30 years old and lives in Seattle"}'
```

---

## Examples

### Example 1: Simple Template

**Code:**
```java
@Service
public class PromptService {
    
    @Autowired
    private ChatLanguageModel chatModel;
    
    public String generateEmail(String recipient, String topic) {
        PromptTemplate template = PromptTemplate.from(
            "Write a professional email to {{recipient}} about {{topic}}. " +
            "Keep it concise and friendly."
        );
        
        Prompt prompt = template.apply(Map.of(
            "recipient", recipient,
            "topic", topic
        ));
        
        return chatModel.generate(prompt.text());
    }
}
```

**Usage:**
```bash
curl -X POST http://localhost:8083/api/prompts/email \
  -H 'Content-Type: application/json' \
  -d '{
    "recipient": "Bob",
    "topic": "project deadline"
  }'
```

**Response:**
```
Subject: Project Deadline Update

Hi Bob,

I wanted to reach out regarding our project deadline. Could we schedule 
a quick call to discuss the timeline and ensure we're aligned?

Looking forward to hearing from you!

Best regards
```

---

### Example 2: Few-Shot Learning

Teach the model through examples:

**Code:**
```java
public String classifyWithFewShot(String text) {
    String prompt = """
        Classify the sentiment as POSITIVE, NEGATIVE, or NEUTRAL.
        
        Examples:
        Input: "I love this product!"
        Output: POSITIVE
        
        Input: "This is the worst experience ever."
        Output: NEGATIVE
        
        Input: "The package arrived on time."
        Output: NEUTRAL
        
        Input: "%s"
        Output:
        """.formatted(text);
    
    return chatModel.generate(prompt);
}
```

**Usage:**
```bash
curl -X POST http://localhost:8083/api/prompts/sentiment \
  -H 'Content-Type: application/json' \
  -d '{"text": "This exceeded all my expectations!"}'
```

**Response:**
```json
{
  "sentiment": "POSITIVE",
  "confidence": "high"
}
```

---

### Example 3: Structured Output with AiServices

Extract structured data from text:

**Code:**
```java
public interface PersonExtractor {
    
    @SystemMessage("Extract person information from the text. " +
                   "If information is missing, use null.")
    Person extractPerson(String text);
}

@Data
public class Person {
    private String name;
    private Integer age;
    private String city;
}

// Usage
PersonExtractor extractor = AiServices.create(PersonExtractor.class, chatModel);
Person person = extractor.extractPerson(
    "John Doe is 30 years old and lives in Seattle"
);
```

**Response:**
```json
{
  "name": "John Doe",
  "age": 30,
  "city": "Seattle"
}
```

---

### Example 4: Chain-of-Thought Prompting

Encourage step-by-step reasoning:

**Code:**
```java
public String solveWithReasoning(String problem) {
    String prompt = """
        Solve this problem step by step. Show your reasoning.
        
        Problem: %s
        
        Let's think through this:
        Step 1:
        """.formatted(problem);
    
    return chatModel.generate(prompt);
}
```

**Usage:**
```bash
curl -X POST http://localhost:8083/api/prompts/reason \
  -H 'Content-Type: application/json' \
  -d '{"problem": "If Alice has 3 apples and gives 1 to Bob, then buys 2 more, how many does she have?"}'
```

**Response:**
```
Let's think through this:
Step 1: Alice starts with 3 apples
Step 2: She gives 1 to Bob, leaving her with 3 - 1 = 2 apples
Step 3: She buys 2 more apples, so 2 + 2 = 4 apples

Answer: Alice has 4 apples.
```

---

### Example 5: Role-Based Prompts

Assign specific roles to the AI:

**Code:**
```java
public String reviewCode(String code) {
    String systemMessage = """
        You are an expert code reviewer. Review the code for:
        - Best practices
        - Potential bugs
        - Performance issues
        - Security concerns
        
        Be constructive and specific.
        """;
    
    return chatModel.generate(
        SystemMessage.from(systemMessage),
        UserMessage.from("Review this code:\n\n" + code)
    );
}
```

---

## Prompt Patterns

### 1. **Instruction + Context + Question**

```
Context: You are a travel advisor helping plan vacations.
Task: Suggest a 3-day itinerary for Tokyo.
Constraints: Budget-friendly, family-oriented activities.
```

### 2. **Format Specification**

```
List 5 programming languages.
Format your response as:
1. Language Name - Use Case
2. ...
```

### 3. **Delimitation**

```
Summarize the text between triple quotes:
'''
[Long text here]
'''
```

### 4. **Negative Prompting**

```
Explain quantum computing.
Do NOT use complex mathematical formulas.
Do NOT assume prior physics knowledge.
```

---

## API Reference

### POST `/api/prompts/template`

Generate text using a template.

**Request:**
```json
{
  "template": "Write a {{style}} poem about {{topic}}",
  "variables": {
    "style": "haiku",
    "topic": "spring"
  }
}
```

**Response:**
```json
{
  "result": "Cherry blossoms fall\nPetals dance in gentle breeze\nSpring awakens life"
}
```

---

### POST `/api/prompts/few-shot`

Classify or generate with examples.

**Request:**
```json
{
  "examples": [
    {"input": "apple", "output": "fruit"},
    {"input": "carrot", "output": "vegetable"}
  ],
  "input": "banana"
}
```

**Response:**
```json
{
  "output": "fruit"
}
```

---

### POST `/api/prompts/extract`

Extract structured data.

**Request:**
```json
{
  "text": "The meeting is on March 15, 2024 at 2:00 PM in Room 305",
  "schema": {
    "date": "string",
    "time": "string",
    "location": "string"
  }
}
```

**Response:**
```json
{
  "date": "March 15, 2024",
  "time": "2:00 PM",
  "location": "Room 305"
}
```

---

## Best Practices

### Do's

1. **Be Specific**: Clear instructions produce better results
2. **Use Examples**: Show the model what you want (few-shot)
3. **Set Constraints**: Specify format, length, tone
4. **Iterate**: Test and refine your prompts
5. **Use Templates**: Reuse successful patterns

### Don'ts

1. **Don't Be Vague**: "Tell me about AI" → "Explain how neural networks work in 3 sentences"
2. **Don't Overwhelm**: Keep prompts focused on one task
3. **Don't Assume**: Provide all necessary context
4. **Don't Ignore Output**: Validate and parse responses

---

## Troubleshooting

### Issue: Inconsistent Outputs

**Solution:**
- Set `temperature=0` for deterministic responses
- Use more specific instructions
- Add output format examples

### Issue: Model Ignores Instructions

**Solution:**
- Move instructions to system message
- Use stronger language ("You MUST...")
- Add examples of desired behavior

### Issue: Output Not Structured

**Solution:**
- Use `AiServices` with defined interfaces
- Specify exact JSON schema
- Add format validation in code

---

## Testing

Run unit tests:

```bash
mvn test
```

Test specific prompt patterns:

```bash
# Test template rendering
curl -X POST http://localhost:8083/api/prompts/test/template

# Test few-shot learning
curl -X POST http://localhost:8083/api/prompts/test/few-shot

# Test output parsing
curl -X POST http://localhost:8083/api/prompts/test/parse
```

---

## Learn More

- **LangChain4j Prompt Templates**: [docs.langchain4j.dev/tutorials/prompts](https://docs.langchain4j.dev/)
- **OpenAI Prompt Engineering Guide**: [platform.openai.com/docs/guides/prompt-engineering](https://platform.openai.com/docs/guides/prompt-engineering)
- **Prompt Engineering Course**: [learnprompting.org](https://learnprompting.org/)

---

## Exercises

See [CHALLENGES.md](CHALLENGES.md) for hands-on exercises including:
- Dynamic template creation
- Multi-turn conversations with prompts
- Custom output parsers
- Prompt optimization techniques

---

## Related Modules

- **Previous**: [02-rag](../02-rag/README.md)
- **Next**: [04-tools](../04-tools/README.md)

---

## License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.
