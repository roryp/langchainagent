# Module 02: Prompt Engineering with GPT-5

## What You'll Learn

In the previous module, you saw how memory enables conversational AI. Now we'll focus on how you ask questions - the prompts themselves. The way you structure your prompts dramatically affects the quality of responses you get.

We'll use Azure OpenAI's GPT-5 because it introduces reasoning control - you can tell the model how much thinking to do before answering. This makes different prompting strategies more apparent and helps you understand when to use each approach.

## Understanding Prompt Engineering

Prompt engineering is about designing input text that consistently gets you the results you need. It's not just about asking questions - it's about structuring requests so the model understands exactly what you want and how to deliver it.

Think of it like giving instructions to a colleague. "Fix the bug" is vague. "Fix the null pointer exception in UserService.java line 45 by adding a null check" is specific. Language models work the same way - specificity and structure matter.

## The Core Patterns

Different tasks need different approaches. Here are eight patterns that cover most real-world scenarios:

**Low Eagerness (Quick & Focused)** - For simple questions where you want fast, direct answers. The model does minimal reasoning - maximum 2 steps. Use this for calculations, lookups, or straightforward questions.

**High Eagerness (Deep & Thorough)** - For complex problems where you want comprehensive analysis. The model explores thoroughly and shows detailed reasoning. Use this for system design, architecture decisions, or complex research.

**Task Execution (Step-by-Step Progress)** - For multi-step workflows. The model provides an upfront plan, narrates each step as it works, then gives a summary. Use this for migrations, implementations, or any multi-step process.

**Self-Reflecting Code** - For generating production-quality code. The model generates code, checks it against quality criteria, and improves it iteratively. Use this when building new features or services.

**Structured Analysis** - For consistent evaluation. The model reviews code using a fixed framework (correctness, practices, performance, security). Use this for code reviews or quality assessments.

**Multi-Turn Chat** - For conversations that need context. The model remembers previous messages and builds on them. Use this for interactive help sessions or complex Q&A.

**Step-by-Step Reasoning** - For problems requiring visible logic. The model shows explicit reasoning for each step. Use this for math problems, logic puzzles, or when you need to understand the thinking process.

**Constrained Output** - For responses with specific format requirements. The model strictly follows format and length rules. Use this for summaries or when you need precise output structure.

## Prerequisites

- Azure subscription with Azure OpenAI access (from Module 01)
- Java 21, Maven 3.9+
- Completed Module 01 deployment

## Quick Start

### Use Existing Azure Resources

```bash
# From Module 01 deployment
cd 02-prompt-engineering
source ../.env
mvn spring-boot:run
```

Open http://localhost:8083 in your browser.

## Using the Application

The application provides a web interface where you can try each pattern and see the differences.

**Try Low vs High Eagerness**

Start with a simple question like "What is 15% of 200?" using Low Eagerness. You'll get a fast, direct answer. Now try a complex problem like "Design a caching strategy for a high-traffic API" using High Eagerness. Notice how the model takes more time and provides detailed reasoning.

**Experiment with Code Generation**

Use the Self-Reflecting Code pattern to generate a service. Try "Create an email validation service". Watch how the model generates code, evaluates it against quality criteria, and improves it. This is how you get production-ready code instead of quick prototypes.

**Test Multi-Turn Conversations**

Ask "What is Spring Boot?" then follow up with "Show me an example". The model maintains context from your first question. This demonstrates how conversational memory works in practice.

**Compare Reasoning Styles**

Try the same math problem with both Step-by-Step Reasoning and Low Eagerness. The reasoning pattern shows you the logic explicitly, while low eagerness just gives you the answer. Each has its place depending on your needs.

## Key Concepts

**Reasoning Effort Control**

GPT-5 lets you control how much thinking the model does. Low effort is fast but basic. High effort is slower but more thorough. Most tasks work fine with medium (the default).

**XML Structure for Clarity**

The patterns use XML tags to organize instructions clearly. This isn't just formatting - it helps the model understand the structure of your request and follow complex multi-step instructions reliably.

**Quality Rubrics**

Self-reflecting patterns work by giving the model explicit quality criteria. Instead of "write good code", you specify: correct logic, proper error handling, performance considerations, security checks. The model evaluates its own output against these criteria and improves it.

**Context Windows**

Multi-turn conversations work by including previous messages with each request. But every model has a limit on total tokens. When conversations get long, you need strategies to maintain relevant context without hitting limits.

## When to Use Each Pattern

**Low Eagerness** - Simple queries, calculations, quick facts. Fast response matters more than deep analysis.

**High Eagerness** - Architecture decisions, complex design problems, thorough research. Quality matters more than speed.

**Task Execution** - Multi-step workflows where users need progress updates. Migration scripts, deployment processes.

**Self-Reflecting Code** - Production code generation. When code quality really matters.

**Structured Analysis** - Code reviews, quality assessments. When you need consistent evaluation frameworks.

**Multi-Turn Chat** - Interactive help, troubleshooting sessions, exploratory conversations.

**Step-by-Step Reasoning** - Teaching moments, logic problems, when you need to understand the thinking.

**Constrained Output** - Summaries, specific formats, when structure is non-negotiable.

## Next Steps

**Next Module:** [03-rag - RAG (Retrieval-Augmented Generation)](../03-rag/README.md)
