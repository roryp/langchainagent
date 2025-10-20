# 03-agents-tools

This module will be filled with:

- Agents and tools goals
- Lab instructions
- Sample code

In upcoming labs you will learn how to leverage LangChain4j's agent
framework to call external tools and APIs from your Java
applications.
The business logic is in langchain4j-agentic
The agent is hosted on Azure AI Agent Service, which calls the LC4j tool via OpenAPI.
The Java client is thin and only speaks threads → messages → runs to the hosted agent.
This is the recommended bridge when you want LC4j agentic behavior and Azure’s managed agent runtime/tools