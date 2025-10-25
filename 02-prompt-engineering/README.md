# Module 02: Prompt Engineering with GPT-5

## What You'll Learn

In the previous module, you saw how memory enables conversational AI. Now we'll focus on how you ask questions - the prompts themselves. The way you structure your prompts dramatically affects the quality of responses you get.

We'll use Azure OpenAI's GPT-5 because it introduces reasoning control - you can tell the model how much thinking to do before answering. This makes different prompting strategies more apparent and helps you understand when to use each approach.

## Understanding Prompt Engineering

Prompt engineering is about designing input text that consistently gets you the results you need. It's not just about asking questions - it's about structuring requests so the model understands exactly what you want and how to deliver it.

Think of it like giving instructions to a colleague. "Fix the bug" is vague. "Fix the null pointer exception in UserService.java line 45 by adding a null check" is specific. Language models work the same way - specificity and structure matter.

## How This Uses LangChain4j

This module demonstrates advanced prompting patterns using the same LangChain4j foundation from previous modules, with a focus on prompt structure and reasoning control.

**Dependencies** - Same core libraries as Module 01:
```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-azure-open-ai</artifactId>
</dependency>
```

**AzureOpenAiChatModel Configuration** - Spring Boot configures the chat model with GPT-5 specific settings. The key difference from Module 01 is how we structure the prompts sent to `chatModel.chat()`, not the model setup itself.

**System and User Messages** - LangChain4j separates message types for clarity. `SystemMessage` sets the AI's behavior and context (like "You are a code reviewer"), while `UserMessage` contains the actual request. This separation lets you maintain consistent AI behavior across different user queries.

**MessageWindowChatMemory for Multi-Turn** - For the multi-turn conversation pattern, we reuse `MessageWindowChatMemory` from Module 01. Each session gets its own memory instance stored in a `Map<String, ChatMemory>`, allowing multiple concurrent conversations without context mixing.

**Prompt Templates** - The real focus here is prompt engineering, not new LangChain4j APIs. Each pattern (low eagerness, high eagerness, task execution, etc.) uses the same `chatModel.chat(prompt)` method but with carefully structured prompt strings. The XML tags, instructions, and formatting are all part of the prompt text, not LangChain4j features.

**Reasoning Control** - GPT-5's reasoning effort is controlled through prompt instructions like "maximum 2 reasoning steps" or "explore thoroughly". These are prompt engineering techniques, not LangChain4j configurations. The library simply delivers your prompts to the model.

The key takeaway: LangChain4j provides the infrastructure (model connection, memory, message handling), while this module teaches you how to craft effective prompts within that infrastructure.

## The Core Patterns

Not all problems need the same approach. Some questions need quick answers, others need deep thinking. Some need visible reasoning, others just need results. This module covers eight prompting patterns - each optimized for different scenarios. You'll experiment with all of them to learn when each approach works best.

**Low Eagerness (Quick & Focused)** - For simple questions where you want fast, direct answers. The model does minimal reasoning - maximum 2 steps. Use this for calculations, lookups, or straightforward questions.

**High Eagerness (Deep & Thorough)** - For complex problems where you want comprehensive analysis. The model explores thoroughly and shows detailed reasoning. Use this for system design, architecture decisions, or complex research.

**Task Execution (Step-by-Step Progress)** - For multi-step workflows. The model provides an upfront plan, narrates each step as it works, then gives a summary. Use this for migrations, implementations, or any multi-step process.

**Self-Reflecting Code** - For generating production-quality code. The model generates code, checks it against quality criteria, and improves it iteratively. Use this when building new features or services.

**Structured Analysis** - For consistent evaluation. The model reviews code using a fixed framework (correctness, practices, performance, security). Use this for code reviews or quality assessments.

**Multi-Turn Chat** - For conversations that need context. The model remembers previous messages and builds on them. Use this for interactive help sessions or complex Q&A.

**Step-by-Step Reasoning** - For problems requiring visible logic. The model shows explicit reasoning for each step. Use this for math problems, logic puzzles, or when you need to understand the thinking process.

**Constrained Output** - For responses with specific format requirements. The model strictly follows format and length rules. Use this for summaries or when you need precise output structure.

## Quick Start

### Use Existing Azure Resources

```bash
# From Module 01 deployment
cd 02-prompt-engineering
source ../.env
mvn spring-boot:run
```

Open http://localhost:8083 in your browser.

## Exploring the Patterns

The web interface lets you experiment with different prompting strategies. Each pattern solves different problems - try them to see when each approach shines.

**Start with Low vs High Eagerness**

Ask a simple question like "What is 15% of 200?" using Low Eagerness. You'll get an instant, direct answer. Now ask something complex like "Design a caching strategy for a high-traffic API" using High Eagerness. Watch how the model slows down and provides detailed reasoning. Same model, same question structure - but the prompt tells it how much thinking to do.

**Generate Production-Quality Code**

Try the Self-Reflecting Code pattern with "Create an email validation service". Instead of just generating code and stopping, the model generates, evaluates against quality criteria, identifies weaknesses, and improves. You'll see it iterate until the code meets production standards. This is the difference between a quick prototype and code you'd actually deploy.

**Experience Multi-Turn Conversations**

Ask "What is Spring Boot?" then immediately follow up with "Show me an example". The model remembers your first question and gives you a Spring Boot example specifically. Without memory, that second question would be too vague. This is how you build conversational experiences that feel natural.

**Compare How Reasoning Appears**

Pick a math problem and try it with both Step-by-Step Reasoning and Low Eagerness. Low eagerness just gives you the answer - fast but opaque. Step-by-step shows you every calculation and decision. Neither approach is "better" - it depends on whether you need to understand the process or just get the result.

## What You're Really Learning

**Reasoning Effort Changes Everything**

GPT-5 lets you control computational effort through your prompts. Low effort means fast responses with minimal exploration. High effort means the model takes time to think deeply. You're learning to match effort to task complexity - don't waste time on simple questions, but don't rush complex decisions either.

**Structure Guides Behavior**

Notice the XML tags in the prompts? They're not decorative. Models follow structured instructions more reliably than freeform text. When you need multi-step processes or complex logic, structure helps the model track where it is and what comes next.

**Quality Through Self-Evaluation**

The self-reflecting patterns work by making quality criteria explicit. Instead of hoping the model "does it right", you tell it exactly what "right" means: correct logic, error handling, performance, security. The model can then evaluate its own output and improve. This turns code generation from a lottery into a process.

**Context Is Finite**

Multi-turn conversations work by including message history with each request. But there's a limit - every model has a maximum token count. As conversations grow, you'll need strategies to keep relevant context without hitting that ceiling. This module shows you how memory works; later you'll learn when to summarize, when to forget, and when to retrieve.

## Choosing Your Approach

You've seen eight patterns. Here's when each one makes sense:

**Low Eagerness** - You need quick facts or simple calculations. Speed matters more than thoroughness.

**High Eagerness** - You're making architectural decisions or designing complex systems. Take the time to think it through.

**Task Execution** - You're running multi-step processes and users need progress updates. Migrations, deployments, anything with visible stages.

**Self-Reflecting Code** - You're generating code that will run in production. Quality matters more than speed.

**Structured Analysis** - You're reviewing code or assessing quality. You need consistent evaluation every time.

**Multi-Turn Chat** - You're building conversational interfaces. Users ask follow-up questions and expect the AI to remember context.

**Step-by-Step Reasoning** - You're solving problems where the logic matters as much as the answer. Teaching scenarios, debugging, complex calculations.

**Constrained Output** - You need responses in specific formats. Summaries with exact word counts, JSON output, structured reports.

## Next Steps

**Next Module:** [03-rag - RAG (Retrieval-Augmented Generation)](../03-rag/README.md)
