# 02 - Prompt Engineering with GPT-5

This module covers effective prompt design for large language models using GPT-5 patterns from OpenAI's official guide.

## Overview

This module implements **8 practical prompting patterns** for GPT-5:

### The 8 Patterns

1. **Low Eagerness (Quick & Focused)** - Get fast, direct answers for simple questions. Max 2 reasoning steps.
   - *Use for:* Math, lookups, simple facts

2. **High Eagerness (Deep & Thorough)** - Get comprehensive analysis with detailed reasoning shown in the response.
   - *Use for:* System design, complex problems, architecture decisions

3. **Task Execution (Step-by-Step Progress)** - Get a plan upfront, then narrated steps, then a summary.
   - *Use for:* Multi-step workflows, migrations, implementations

4. **Self-Reflecting Code** - Generate production-quality code with internal quality checks.
   - *Use for:* Building new features, creating services, writing APIs

5. **Structured Analysis** - Get consistent code review feedback (correctness, practices, performance, security).
   - *Use for:* Code reviews, quality assessments

6. **Multi-Turn Chat** - Have conversations that remember context across multiple messages.
   - *Use for:* Interactive help, Q&A sessions, clarifications

7. **Step-by-Step Reasoning** - See explicit visible reasoning for each step of problem-solving.
   - *Use for:* Math problems, logic puzzles, teaching moments

8. **Constrained Output** - Get responses that strictly follow format and length requirements.
   - *Use for:* Summaries, specific formats, word-count limits

## Quick Start

```bash
# 1. Set environment variables for local development
export AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"
export AZURE_OPENAI_API_KEY="your-key"
export AZURE_OPENAI_DEPLOYMENT="gpt-5"

# 2. Build and run locally
cd 02-prompt-engineering
mvn spring-boot:run

# 3. Test all patterns
curl http://localhost:8083/api/gpt5/focused -X POST \
  -H "Content-Type: application/json" \
  -d '{"problem":"What is 15% of 200?"}'
```

## What You'll Learn

### Traditional Techniques
- **Prompt Templates**: Create reusable, parameterized prompts
- **Few-Shot Learning**: Teach models through examples
- **Output Parsers**: Structure and validate LLM responses
- **System Messages**: Set behavior and constraints

### GPT-5 Practices
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
- Understand prompt engineering practices

---

## Concepts

### What is Prompt Engineering?

**Prompt Engineering** is the practice of designing and refining input text (prompts) to get desired outputs from language models. Good prompts can significantly improve:
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
       Application.java                # Main Spring Boot application
    controller/
       Gpt5PromptController.java       # REST API endpoints
    service/
       Gpt5PromptService.java          # GPT-5 prompt implementations
    config/
       LangChainConfig.java            # LangChain4j configuration
 src/main/resources/
    application.yaml                   # Configuration
 pom.xml
 README.md
```

---

## Quick Start

### Prerequisites

**Deploy Azure OpenAI resources:**

```bash
# Deploy from 01-introduction module:
cd ../01-introduction
azd up

# Export the environment variables
export AZURE_OPENAI_ENDPOINT="$(azd env get-values | grep AZURE_OPENAI_ENDPOINT | cut -d'=' -f2 | tr -d '"')"
export AZURE_OPENAI_API_KEY="$(azd env get-values | grep AZURE_OPENAI_KEY | cut -d'=' -f2 | tr -d '"')"
export AZURE_OPENAI_DEPLOYMENT="gpt-5"
```

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
# Focused response (low eagerness)
curl -X POST http://localhost:8083/api/gpt5/focused \
  -H 'Content-Type: application/json' \
  -d '{"problem": "What is 15% of 200?"}'

# Autonomous problem solving (high eagerness)
curl -X POST http://localhost:8083/api/gpt5/autonomous \
  -H 'Content-Type: application/json' \
  -d '{"problem": "Design a caching strategy"}'

# Code generation with reflection
curl -X POST http://localhost:8083/api/gpt5/code \
  -H 'Content-Type: application/json' \
  -d '{"requirement": "Create email validation service"}'
```

---

## Prompt Engineering Concepts

### Chain-of-Thought Prompting

Step-by-step reasoning by asking the model to show its work:

**Example:**
```bash
curl -X POST http://localhost:8083/api/gpt5/reason \
  -H 'Content-Type: application/json' \
  -d '{"problem": "If Alice has 3 apples and gives 1 to Bob, then buys 2 more, how many does she have?"}'
```

**Response:**
```
Let's think through this step by step:
Step 1: Alice starts with 3 apples
Step 2: She gives 1 to Bob, leaving her with 3 - 1 = 2 apples
Step 3: She buys 2 more apples, so 2 + 2 = 4 apples

Answer: Alice has 4 apples.
```

### Role-Based Prompts

Assign specific roles to the AI for improved results:

**Example:**
```bash
curl -X POST http://localhost:8083/api/gpt5/analyze \
  -H 'Content-Type: application/json' \
  -d '{"code": "public void process(String s) { return s.toLowerCase(); }"}'
```

The system uses a code reviewer role to provide structured feedback on practices, bugs, performance, and security

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

## Recommended Practices

### Do's

1. **Be Specific**: Clear instructions produce improved results
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

## GPT-5 Prompting Patterns

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

---

## Key Concepts

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
3. Practices
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
| Low Eagerness | Simple queries | Fast | Medium |
| High Eagerness | Complex tasks | Slow | High |
| Tool Preambles | Multi-step workflows | Medium | High |
| Self-Reflection | Code generation | Slow | Very High |
| Structured Analysis | Code review | Medium | High |
| Multi-Turn | Conversations | Medium | High |
| Reasoning | Logic problems | Slow | High |
| Constrained | Format-specific | Medium | Medium |

---

## Configuration

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

## Examples in Code

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

## Resources

- **OpenAI GPT-5 Guide**: [Official Cookbook](https://github.com/openai/openai-cookbook/blob/main/examples/gpt-5/gpt-5_prompting_guide.ipynb)
- **LangChain4j Docs**: [docs.langchain4j.dev](https://docs.langchain4j.dev/)
- **Spring Boot**: [docs.spring.io](https://docs.spring.io/spring-boot/docs/current/reference/html/)

---

## Next Steps

**Next Module:** [03-rag - RAG (Retrieval-Augmented Generation)](../03-rag/README.md)
