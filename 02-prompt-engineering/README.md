# 02 - Prompt Engineering with GPT-5

Learn the art and science of crafting effective prompts for large language models using **GPT-5 best practices** from OpenAI's official guide!

## 🎯 What's New

This module implements **Practical prompting patterns** inspired by [OpenAI's GPT-5 prompting guide](https://github.com/openai/openai-cookbook/blob/main/examples/gpt-5/gpt-5_prompting_guide.ipynb):

1. **Low Eagerness** - Fast, focused responses (2 steps max)
2. **High Eagerness** - Thorough, autonomous exploration
3. **Tool Preambles** - Progress updates for multi-step tasks
4. **Self-Reflecting Code** - Quality-driven code generation
5. **Structured Analysis** - Framework-based code review
6. **Multi-Turn Chat** - Context-aware conversations
7. **Step-by-Step Reasoning** - Explicit logic walkthrough
8. **Constrained Output** - Format & length compliance

## 🚀 Quick Start

```bash
# 1. Set environment variables for local development
export AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"
export AZURE_OPENAI_API_KEY="your-key"
export AZURE_OPENAI_DEPLOYMENT="gpt-5"

# 2. Build and run locally
cd 02-prompt-engineering
mvn spring-boot:run

# 3. Test all patterns
curl http://localhost:8080/api/prompts/focused -X POST \
  -H "Content-Type: application/json" \
  -d '{"prompt":"What is 15% of 200?"}'
```

## What You'll Learn

### Traditional Techniques
- **Prompt Templates**: Create reusable, parameterized prompts
- **Few-Shot Learning**: Teach models through examples
- **Output Parsers**: Structure and validate LLM responses
- **System Messages**: Set behavior and constraints

### GPT-5 Best Practices (NEW!)
- **Agentic Eagerness Control**: Low vs. high autonomy patterns
- **Tool Preambles**: Progress updates for multi-step tasks
- **Self-Reflecting Code Generation**: Internal quality rubrics
- **Structured Extraction**: Precise, type-safe data extraction
- **Multi-Turn Conversations**: Context-aware interactions

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
02-prompt-engineering/
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

**Deploy GPT-5 to Azure OpenAI using the existing Bicep infrastructure:**

```bash
# Easy way - use the deploy script
chmod +x deploy-gpt5.sh
./deploy-gpt5.sh

# Or manually from 01-introduction:
cd ../01-introduction

# Edit infra/main.bicep and change:
# Deployment name in Azure
# - gpt-4o-mini → gpt-5 (when available in your region)
# - version: '2024-07-18' → version: '0125'

azd up
```

The `azd` deployment automatically sets your environment variables:
- `AZURE_OPENAI_ENDPOINT`
- `AZURE_OPENAI_API_KEY`  
- `AZURE_OPENAI_DEPLOYMENT`

### 1. Build the Project

```bash
cd 02-prompt-engineering
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
- Use more specific instructions
- Add output format examples

### Issue: Model Ignores Instructions

**Solution:**
- Move instructions to system message
- Use stronger language ("You MUST...")
- Add examples of desired behavior

---

## Testing

Test prompt patterns:

```bash
# Focused response
curl -X POST http://localhost:8083/api/gpt5/focused \
  -H "Content-Type: application/json" \
  -d '{"problem":"What is 7 times 8?"}'

# Autonomous problem solving  
curl -X POST http://localhost:8083/api/gpt5/autonomous \
  -H "Content-Type: application/json" \
  -d '{"problem":"Design a caching strategy"}'
```

---

---

## 🚀 GPT-5 Prompting Patterns

This module implements **8 practical prompting patterns** inspired by concepts from [OpenAI's GPT-5 prompting guide](https://github.com/openai/openai-cookbook/blob/main/examples/gpt-5/gpt-5_prompting_guide.ipynb):

### 1. **Low Eagerness** (Fast, Focused)
```bash
curl -X POST http://localhost:8083/api/gpt5/focused \
  -H 'Content-Type: application/json' \
  -d '{"problem": "What is 15% of 200?"}'
```
- Max 2 reasoning steps
- Quick, direct answers
- Use for: calculations, lookups, simple questions

### 2. **High Eagerness** (Thorough, Autonomous)
```bash
curl -X POST http://localhost:8083/api/gpt5/autonomous \
  -H 'Content-Type: application/json' \
  -d '{"problem": "Design a scalable microservices architecture"}'
```
- Complete exploration
- Autonomous decision-making
- Use for: system design, complex research, architecture

### 3. **Tool Preambles** (Progress Updates)
```bash
curl -X POST http://localhost:8083/api/gpt5/task \
  -H 'Content-Type: application/json' \
  -d '{"task": "Migrate API to OAuth2"}'
```
- Clear upfront plan
- Step-by-step narration
- Distinct summary
- Use for: multi-step workflows, user-facing tasks

### 4. **Self-Reflecting Code Generation**
```bash
curl -X POST http://localhost:8083/api/gpt5/code \
  -H 'Content-Type: application/json' \
  -d '{"requirement": "Create file upload service with S3 integration"}'
```
- Internal quality rubrics (7 criteria)
- Iterative improvement
- Production-quality output
- Use for: code generation, high-quality requirements

### 5. **Structured Analysis**
```bash
curl -X POST http://localhost:8083/api/gpt5/analyze \
  -H 'Content-Type: application/json' \
  -d '{"code": "public void process(String s) { return s.toLowerCase(); }"}'
```
- Framework-based evaluation
- Consistent feedback format
- Use for: code review, quality assessment

### 6. **Multi-Turn Conversations**
```bash
# First message
curl -X POST http://localhost:8083/api/gpt5/chat \
  -H 'Content-Type: application/json' \
  -d '{"message": "What is Spring Boot?", "sessionId": "user123"}'

# Follow-up (maintains context)
curl -X POST http://localhost:8083/api/gpt5/chat \
  -H 'Content-Type: application/json' \
  -d '{"message": "Show me an example", "sessionId": "user123"}'
```
- Context preservation
- Natural conversation flow
- Use for: interactive assistants, complex Q&A

### 7. **Step-by-Step Reasoning**
```bash
curl -X POST http://localhost:8083/api/gpt5/reason \
  -H 'Content-Type: application/json' \
  -d '{"problem": "If Alice has 3 apples and gives 1 to Bob, then buys 2 more, how many?"}'
```
- Explicit reasoning process
- Clear verification
- Use for: math problems, logic puzzles

### 8. **Constrained Output**
```bash
curl -X POST http://localhost:8083/api/gpt5/constrained \
  -H 'Content-Type: application/json' \
  -d '{"topic": "machine learning", "format": "bullet points", "maxWords": 100}'
```
- Strict format adherence
- Word count limits
- Use for: summaries, specific formats

### Run All Tests
```bash
chmod +x test-gpt5.sh
./test-gpt5.sh
```

---

## 📖 Key Concepts

### XML-Based Prompt Structure
All GPT-5 patterns use XML tags for clear organization:

```xml
<context_gathering>
- Search depth: low/medium/high
- Stop criteria
- Reasoning approach
</context_gathering>

<persistence>
- Autonomy level
- When to ask for help
- Error handling
</persistence>

<self_reflection>
Quality rubric:
1. Correctness
2. Quality
3. Best practices
... iterate until all ✓
</self_reflection>

<tool_preambles>
- Restate goal
- Outline plan
- Narrate steps
- Summarize completion
</tool_preambles>
```

### When to Use Each Pattern

| Pattern | Use Case | Speed | Quality |
|---------|----------|-------|---------|
| Low Eagerness | Simple queries | ⚡⚡⚡ | ⭐⭐ |
| High Eagerness | Complex tasks | ⚡ | ⭐⭐⭐ |
| Tool Preambles | Multi-step workflows | ⚡⚡ | ⭐⭐⭐ |
| Self-Reflection | Code generation | ⚡ | ⭐⭐⭐⭐ |
| Structured Analysis | Code review | ⚡⚡ | ⭐⭐⭐ |
| Multi-Turn | Conversations | ⚡⚡ | ⭐⭐⭐ |
| Reasoning | Logic problems | ⚡ | ⭐⭐⭐ |
| Constrained | Format-specific | ⚡⚡ | ⭐⭐ |

---

## 🔧 Configuration

### Reasoning Effort Settings (GPT-5)
```yaml
# application.yaml
azure:
  openai:
    reasoning-effort: medium  # Default, balanced
    # low = faster responses (simple tasks)
    # medium = balanced (general tasks)
    # high = deeper reasoning (complex analysis)
```

### Multiple Model Beans
```java
@Autowired
private ChatLanguageModel chatModel;  // Default (medium)

@Autowired
@Qualifier("quickModel")
private ChatLanguageModel quickModel;  // Low reasoning effort

@Autowired
@Qualifier("thoroughModel")
private ChatLanguageModel thoroughModel;  // High reasoning effort
```

---

## 💡 Examples in Code

### Quick Usage
```java
@Autowired
private Gpt5PromptService gpt5Service;

// Fast answer
String result = gpt5Service.solveFocused("What is 15% of 200?");

// Thorough analysis
String design = gpt5Service.solveAutonomous(
    "Design a caching strategy for high-traffic API"
);

// High-quality code
String code = gpt5Service.generateCodeWithReflection(
    "Create email validation service with proper error handling"
);

// Multi-turn conversation
String response = gpt5Service.continueConversation(
    "Tell me about Spring Boot", 
    "user123"
);
```

### Structured Extraction
```java
@Autowired
private Gpt5StructuredExtractor extractor;

String text = "John Doe is 35 and lives in San Francisco";
PersonInfo person = extractor.extractPerson(text);

System.out.println(person.fullName());  // "John Doe"
System.out.println(person.age());       // 35
System.out.println(person.location());  // "San Francisco"
```

---

## 📚 Resources

- **OpenAI GPT-5 Guide**: [Official Cookbook](https://github.com/openai/openai-cookbook/blob/main/examples/gpt-5/gpt-5_prompting_guide.ipynb)
- **LangChain4j Docs**: [docs.langchain4j.dev](https://docs.langchain4j.dev/)
- **Spring Boot**: [docs.spring.io](https://docs.spring.io/spring-boot/docs/current/reference/html/)

---

## Exercises

See [CHALLENGES.md](CHALLENGES.md) for hands-on exercises including:
- Dynamic template creation
- Multi-turn conversations with prompts
- Custom output parsers
- Prompt optimization techniques

---

## Related Modules

- **Previous**: [01-introduction](../01-introduction/README.md)
- **Next**: [03-rag](../03-rag/README.md)

---

## License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.
