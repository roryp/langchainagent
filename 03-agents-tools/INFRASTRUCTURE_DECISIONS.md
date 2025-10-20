# Infrastructure Design Decisions for Module 03

## TL;DR

**We use direct Azure OpenAI integration via LangChain4j instead of Azure AI Foundry Hub/Project for cost efficiency and simplicity.**

## Decision: Direct Azure OpenAI vs Azure AI Foundry

### Current Implementation ✅

```
Java App (LangChain4j) → Azure OpenAI API
```

**Resources Required:**
- Azure OpenAI Service
- Container App
- Container Registry
- Log Analytics

**Monthly Cost Estimate:** ~$5-20 (mostly token usage)

### Alternative Considered ❌

```
Java App → Azure AI Foundry Hub → AI Project → Azure OpenAI
```

**Resources Required:**
- Azure OpenAI Service
- Azure AI Foundry Hub (Microsoft.MachineLearningServices/workspace, kind: hub)
- Azure AI Foundry Project (Microsoft.MachineLearningServices/workspace, kind: project)
- Azure Storage Account (required by Hub)
- Azure Key Vault (required by Hub)
- Container App
- Container Registry
- Log Analytics

**Monthly Cost Estimate:** ~$20-60 (includes Hub overhead)

## Why We Chose Direct Azure OpenAI

### 1. **Microsoft's Official Guidance for Java**

From [Azure AI for Java Developers](https://learn.microsoft.com/azure/developer/java/ai/azure-ai-for-java-developers):

> "Azure OpenAI Service provides REST API access to OpenAI's powerful language models... Users can access the service through REST APIs, the `langchain4j-azure-open-ai` package, or via the Azure AI Foundry portal."

**Recommendation:** `langchain4j-azure-open-ai` is the official Java SDK for Azure OpenAI.

### 2. **Azure AI Foundry Architecture Update (October 2025)**

Microsoft introduced two types of projects:

| Type | Resource Provider | Use Case |
|------|------------------|----------|
| **Azure AI Foundry project** (new) | `Microsoft.CognitiveServices/account` | Agents, Models, modern AI apps |
| **Hub-based project** (legacy) | `Microsoft.MachineLearningServices/workspace` | Prompt Flow, custom ML training |

The legacy Hub-based project (what we initially deployed) is for:
- Prompt Flow workflows
- Custom machine learning model training
- Azure Machine Learning compatibility

It is **NOT recommended** for modern agent development with Java.

### 3. **LangChain4j Native Support**

LangChain4j provides first-class Azure OpenAI integration:

```java
AzureOpenAiChatModel chatModel = AzureOpenAiChatModel.builder()
    .endpoint(endpoint)
    .apiKey(apiKey)
    .deploymentName("gpt-4o-mini")
    .build();
```

This is:
- ✅ Well-documented
- ✅ Actively maintained
- ✅ Production-ready
- ✅ No additional Azure infrastructure needed

### 4. **Cost Efficiency**

**Direct Azure OpenAI:**
- Azure OpenAI: ~$0.15/$0.60 per 1M tokens (gpt-4o-mini input/output)
- Container App: ~$0.01/hour for 0.25 vCPU, 0.5GB memory
- Total: Pay-per-use, minimal overhead

**With Azure AI Foundry Hub:**
- Same OpenAI costs
- AI Hub: ~$10-30/month base cost
- Storage Account: ~$1-5/month
- Key Vault: ~$0.03/10,000 operations
- Additional compute: Variable
- Total: Higher fixed costs

**Savings:** ~$10-50/month for a learning module

### 5. **Simplicity and Maintainability**

**Direct Azure OpenAI:**
- 4 Azure resources
- 1 SDK (`langchain4j-azure-open-ai`)
- 1 endpoint URL
- 1 API key

**With Azure AI Foundry Hub:**
- 8+ Azure resources
- Multiple SDKs (Azure ML SDK, Azure AI SDK, LangChain4j)
- Multiple endpoints (Hub, Project, OpenAI)
- Multiple credentials (managed identity, API keys)

### 6. **Deployment Speed**

**Direct Azure OpenAI:**
- Deployment time: ~3-5 minutes
- Bicep template: ~150 lines

**With Azure AI Foundry Hub:**
- Deployment time: ~10-15 minutes
- Bicep template: ~300+ lines
- Additional dependencies (storage, key vault)

## When to Use Azure AI Foundry Hub/Project

Azure AI Foundry is valuable when you need:

1. **Prompt Flow**: Visual workflow designer for LLM apps
2. **Custom ML Models**: Training and deploying custom models
3. **Azure Machine Learning**: Integration with AML workspaces
4. **Managed Compute**: Serverless or dedicated compute clusters
5. **Enterprise Governance**: Centralized hub for multiple teams
6. **Data Labeling**: Built-in data labeling tools
7. **Model Registry**: Centralized model versioning

## Migration Path (If Needed)

If you later need Azure AI Foundry capabilities:

### Option 1: Keep LangChain4j, Add Foundry Project

1. Create Azure AI Foundry project (new `Microsoft.CognitiveServices` type)
2. Connect it to your existing Azure OpenAI resource
3. Use Foundry portal for monitoring and evaluations
4. Keep your LangChain4j code unchanged

### Option 2: Switch to Azure AI Agent Service

1. Create Azure AI Foundry project
2. Configure agent in AI Foundry Studio
3. Update Java code to use Azure AI Agent Client SDK
4. Keep your tool endpoints (REST APIs work the same)

```java
// New code using Azure AI Agent Service
import com.azure.ai.agent.*;

AgentClient client = new AgentClientBuilder()
    .endpoint(foundryEndpoint)
    .credential(new DefaultAzureCredential())
    .buildClient();

AgentThread thread = client.createThread();
client.submitMessage(thread.getId(), "What's the weather?");
```

## Conclusion

For this learning module focused on **LangChain4j agent patterns**, direct Azure OpenAI integration is:

- ✅ **Officially Recommended** by Microsoft for Java developers
- ✅ **Cost-Effective** (~50% cheaper)
- ✅ **Simpler** to understand and maintain
- ✅ **Faster** to deploy
- ✅ **Production-Ready** with full LangChain4j support

The agent functionality remains identical - users learn the same concepts (agents, tools, ReAct patterns) without unnecessary infrastructure complexity.

## References

- [Azure AI for Java Developers](https://learn.microsoft.com/azure/developer/java/ai/azure-ai-for-java-developers)
- [What is Azure AI Foundry?](https://learn.microsoft.com/azure/ai-foundry/what-is-azure-ai-foundry)
- [Azure AI Foundry Architecture](https://learn.microsoft.com/azure/ai-foundry/concepts/architecture)
- [LangChain4j Azure OpenAI](https://github.com/langchain4j/langchain4j/tree/main/langchain4j-azure-open-ai)
- [Migrate from hub-based to Foundry projects](https://learn.microsoft.com/azure/ai-foundry/how-to/migrate-project)
