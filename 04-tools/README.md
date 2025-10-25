# Module 04: AI Agents with Tools

## What You'll Learn

So far, you've learned how to have conversations with AI, structure prompts effectively, and ground responses in your documents. But there's still a fundamental limitation: language models can only generate text. They can't check the weather, perform calculations, query databases, or interact with external systems.

Tools change this. By giving the model access to functions it can call, you transform it from a text generator into an agent that can take actions. The model decides when it needs a tool, which tool to use, and what parameters to pass. Your code executes the function and returns the result. The model incorporates that result into its response.

## Understanding AI Agents with Tools

An AI agent with tools follows a reasoning and acting pattern (ReAct):

1. User asks a question
2. Agent reasons about what it needs to know
3. Agent decides if it needs a tool to answer
4. If yes, agent calls the appropriate tool with the right parameters
5. Tool executes and returns data
6. Agent incorporates the result and provides the final answer

This happens automatically. You define the tools and their descriptions. The model handles the decision-making about when and how to use them.

## How Tool Calling Works

**Tool Definitions**

You define functions with clear descriptions and parameter specifications. The model sees these descriptions in its system prompt and understands what each tool does.

**Decision Making**

When a user asks "What's the weather in Seattle?", the model recognizes it needs the weather tool. It generates a function call with the location parameter set to "Seattle".

**Execution**

Your code intercepts the function call, executes the actual weather lookup (via API or database), and returns the result to the model.

**Response Generation**

The model receives the weather data and formats it into a natural language response for the user.

## Tool Chaining

The real power comes from chaining multiple tools. A user might ask "What's the weather in Tokyo and convert it to Fahrenheit?" The agent:

1. Calls the weather tool for Tokyo (gets temperature in Celsius)
2. Calls the temperature conversion tool (converts to Fahrenheit)
3. Combines both results into a coherent answer

This happens in a single conversation turn. The agent orchestrates multiple tool calls autonomously.

## Quick Start

### Use Existing Azure Resources

```bash
# From Module 01 deployment
cd 04-tools
source ../.env
./start.sh
```

Open http://localhost:8084 in your browser.

## Using the Application

The application provides a web interface where you can interact with an AI agent that has access to weather and temperature conversion tools.

**Try Simple Tool Usage**

Start with a straightforward request: "Convert 100 degrees Fahrenheit to Celsius". The agent recognizes it needs the temperature conversion tool, calls it with the right parameters, and returns the result. Notice how natural this feels - you didn't specify which tool to use or how to call it.

**Test Tool Chaining**

Now try something more complex: "What's the weather in Seattle and convert it to Fahrenheit?" Watch the agent work through this in steps. It first gets the weather (which returns Celsius), recognizes it needs to convert to Fahrenheit, calls the conversion tool, and combines both results into one response.

**Observe the Reasoning**

The interface shows you which tools were called. This visibility into the agent's decision-making helps you understand how it breaks down complex requests into tool executions.

**Experiment with Different Requests**

Try various combinations:
- Weather lookups: "What's the weather in Tokyo?"
- Temperature conversions: "What is 25°C in Kelvin?"
- Combined queries: "Check the weather in Paris and tell me if it's above 20°C"

Notice how the agent interprets natural language and maps it to appropriate tool calls.

## Key Concepts

**ReAct Pattern (Reasoning and Acting)**

The agent alternates between reasoning (deciding what to do) and acting (using tools). This pattern enables autonomous problem-solving rather than just responding to instructions.

**Tool Descriptions Matter**

The quality of your tool descriptions directly affects how well the agent uses them. Clear, specific descriptions help the model understand when and how to call each tool.

**Session Management**

Like the conversation memory in Module 01, tool-based agents maintain session context. The agent remembers previous tool calls and results within a session, enabling multi-turn interactions.

**Error Handling**

Tools can fail - APIs timeout, parameters might be invalid, external services go down. Production agents need error handling so the model can explain problems or try alternatives.

## Available Tools

**Weather Tools** (mock data for demonstration):
- Get current weather for a location
- Get multi-day forecast

**Temperature Conversion Tools**:
- Celsius to Fahrenheit
- Fahrenheit to Celsius
- Celsius to Kelvin
- Kelvin to Celsius
- Fahrenheit to Kelvin
- Kelvin to Fahrenheit

These are simple examples, but the pattern extends to any function: database queries, API calls, calculations, file operations, or system commands.

## When to Use Tool-Based Agents

**Use tools when:**
- Answering requires real-time data (weather, stock prices, inventory)
- You need to perform calculations beyond simple math
- Accessing databases or APIs
- Taking actions (sending emails, creating tickets, updating records)
- Combining multiple data sources

**Don't use tools when:**
- Questions can be answered from general knowledge
- Response is purely conversational
- Tool latency would make the experience too slow

## Next Steps

**Next Module:** [05-mcp - Model Context Protocol (MCP)](../05-mcp/README.md)