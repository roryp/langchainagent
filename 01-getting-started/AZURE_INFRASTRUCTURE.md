# Azure Infrastructure Setup - Implementation Summary

## Overview
Successfully created a complete Azure infrastructure setup for the LangChain4j Getting Started module using Azure Developer CLI (azd) and Bicep.

## Files Created

### Root Configuration
1. **`azure.yaml`** - Azure Developer CLI configuration
   - Defines the service name, project structure, and deployment target
   - Configures Docker build context and file locations

### Infrastructure as Code (Bicep)
2. **`infra/main.bicep`** - Main infrastructure template
   - Subscription-scoped deployment
   - Creates resource group with proper tags
   - Orchestrates all resource deployments
   - Configures outputs for azd integration

3. **`infra/main.bicepparam`** - Parameters file
   - Uses environment variables for configuration
   - Provides defaults for local development

### Core Modules

#### Security
4. **`infra/core/security/managed-identity.bicep`**
   - User-Assigned Managed Identity for passwordless authentication
   - Outputs principal ID and client ID for role assignments

#### Monitoring
5. **`infra/core/monitor/log-analytics.bicep`**
   - Log Analytics Workspace for centralized logging
   - 30-day retention (configurable)
   - PerGB2018 pricing tier

#### AI Services
6. **`infra/core/ai/cognitiveservices.bicep`**
   - Azure OpenAI resource deployment
   - Support for multiple model deployments
   - Automatic role assignment (Cognitive Services OpenAI User)
   - **Deployments included:**
     - `gpt-4o-mini` (chat model) - 20K TPM
     - `text-embedding-3-small` (embedding model) - 20K TPM

#### Container Hosting
7. **`infra/core/host/container-registry.bicep`**
   - Azure Container Registry (Basic SKU)
   - AcrPull role assignment to managed identity
   - Admin user disabled for security

8. **`infra/core/host/container-apps-environment.bicep`**
   - Container Apps managed environment
   - Integrated with Log Analytics
   - Zone redundancy disabled (can be enabled for production)

9. **`infra/core/host/container-app.bicep`**
   - Container App configuration
   - User-assigned identity attached
   - Registry authentication using managed identity
   - Base image: `mcr.microsoft.com/azuredocs/containerapps-helloworld:latest`
   - CORS enabled for API access
   - Auto-scaling configuration (1-10 replicas)
   - Resource allocation: 1 CPU, 2GB memory

### Documentation
10. **`infra/README.md`** - Comprehensive infrastructure documentation
    - Architecture overview
    - Resource descriptions
    - Deployment instructions
    - Configuration guide
    - Troubleshooting section
    - Cost optimization tips
    - Security best practices

11. **`.env.example`** - Environment variables template
    - Template for local development
    - Documents required configuration values

## Key Features

### Model Deployments
✅ **GPT-4o-mini (Latest Model)**
- Model version: `2024-07-18`
- Capacity: 20K tokens per minute (TPM)
- Standard SKU
- Deployment name: `gpt-4o-mini`

✅ **Text-Embedding-3-Small**
- Latest embedding model from OpenAI
- Capacity: 20K TPM
- Standard SKU
- Deployment name: `text-embedding-3-small`
- **Supports all use cases:**
  - Module 01: Not needed (chat only)
  - Module 02: RAG - document embeddings and semantic search
  - Module 03: Agents & Tools - potential for semantic routing
  - Module 04: Production - full feature set

### Security
✅ **Managed Identity Authentication (Passwordless)**
- No API keys stored in configuration
- Uses Azure AD authentication
- Automatic token refresh
- Principle of least privilege

✅ **Role-Based Access Control**
- AcrPull role for container registry access
- Cognitive Services OpenAI User role for API access
- Scoped to specific resources only

### Deployment Workflow
✅ **Azure Developer CLI Integration**
- Simple `azd up` deploys everything
- Automatic Docker build and push
- Environment variable configuration
- Output capture for application use

✅ **Infrastructure as Code Best Practices**
- Modular Bicep structure
- Reusable core components
- Parameterized deployments
- Proper tagging for resource management

### Compliance with Requirements
✅ **AZD Rules Compliance**
- ✅ User-Assigned Managed Identity exists
- ✅ Resource Group has `azd-env-name` tag
- ✅ Environment parameters configured
- ✅ Container App has `azd-service-name` tag
- ✅ AcrPull role assigned before container apps
- ✅ Base container image specified
- ✅ CORS enabled
- ✅ Container Apps Environment connected to Log Analytics
- ✅ Outputs include RESOURCE_GROUP_ID and AZURE_CONTAINER_REGISTRY_ENDPOINT

✅ **Bicep Best Practices**
- ✅ Proper naming conventions (az{prefix}{token})
- ✅ UniqueString used for resource tokens
- ✅ main.bicep and main.bicepparam files created
- ✅ Module references use relative paths
- ✅ All resources properly tagged

## Environment Variables Set

The infrastructure automatically configures these environment variables in the Container App:

1. **AZURE_OPENAI_ENDPOINT** - Azure OpenAI service endpoint URL
2. **AZURE_OPENAI_DEPLOYMENT** - Set to `gpt-4o-mini`
3. **AZURE_OPENAI_EMBEDDING_DEPLOYMENT** - Set to `text-embedding-3-small`
4. **SPRING_PROFILES_ACTIVE** - Set to `azure`

## Deployment Commands

```bash
# Initialize (one time)
cd 01-getting-started
azd init

# Provision infrastructure
azd provision

# Deploy application
azd deploy

# Or do both at once
azd up

# Monitor application
azd monitor

# Clean up
azd down
```

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     Azure Subscription                       │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │           Resource Group: rg-{env-name}                 │ │
│  │                                                          │ │
│  │  ┌─────────────────────┐      ┌──────────────────────┐ │ │
│  │  │  Log Analytics      │◄─────│  Container Apps Env  │ │ │
│  │  │  Workspace          │      │  cae-{token}         │ │ │
│  │  └─────────────────────┘      └──────┬───────────────┘ │ │
│  │                                       │                  │ │
│  │  ┌─────────────────────┐      ┌──────▼───────────────┐ │ │
│  │  │  Container Registry │      │   Container App      │ │ │
│  │  │  acr{token}         │◄─────┤   ca-{token}         │ │ │
│  │  └─────────────────────┘      │   (Java App)         │ │ │
│  │            ▲                   └──────┬───────────────┘ │ │
│  │            │                          │                  │ │
│  │  ┌─────────┴────────────┐            │                  │ │
│  │  │  Managed Identity    │            │ Uses ▼           │ │
│  │  │  id-{token}          │            │                  │ │
│  │  └──────────────────────┘            │                  │ │
│  │                                       │                  │ │
│  │  ┌──────────────────────────────┐    │                  │ │
│  │  │  Azure OpenAI                │◄───┘                  │ │
│  │  │  aoai-{token}                │                       │ │
│  │  │                              │                       │ │
│  │  │  ┌────────────────────────┐ │                       │ │
│  │  │  │ gpt-4o-mini           │ │                       │ │
│  │  │  │ (20K TPM)              │ │                       │ │
│  │  │  └────────────────────────┘ │                       │ │
│  │  │                              │                       │ │
│  │  │  ┌────────────────────────┐ │                       │ │
│  │  │  │ text-embedding-3-small │ │                       │ │
│  │  │  │ (20K TPM)              │ │                       │ │
│  │  │  └────────────────────────┘ │                       │ │
│  │  └──────────────────────────────┘                       │ │
│  └──────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## Model Information

### GPT-4o-mini
- **Purpose**: Chat completion for all modules
- **Context Window**: 128K tokens
- **Training Data**: Up to October 2023
- **Capabilities**: 
  - Text generation
  - Conversation
  - Function calling
  - JSON mode
  - Reasoning

### Text-Embedding-3-Small
- **Purpose**: Text embeddings for RAG and semantic search
- **Dimensions**: 1536
- **Max Input**: 8191 tokens
- **Use Cases**:
  - Document indexing
  - Semantic search
  - Similarity comparison
  - Clustering

## Next Steps

1. **Review the infrastructure documentation**: See `infra/README.md` for detailed instructions
2. **Run deployment**: Execute `azd up` to provision and deploy
3. **Test the application**: Use the provided curl commands to test endpoints
4. **Configure monitoring**: Set up alerts and dashboards in Azure Portal
5. **Extend for other modules**: Copy infrastructure pattern for modules 02, 03, 04

## Cost Estimates (Per Month)

- **Container App**: ~$50 (1 vCore, 2GB RAM, always on)
- **Container Registry**: ~$5 (Basic tier)
- **Log Analytics**: ~$5 (30-day retention, low volume)
- **Azure OpenAI**: Variable based on usage
  - gpt-4o-mini: $0.150 per 1M input tokens, $0.600 per 1M output tokens
  - text-embedding-3-small: $0.020 per 1M tokens
- **Total Fixed**: ~$60/month + OpenAI usage

**Development Tip**: Set minReplicas to 0 to scale to zero when not in use, reducing Container App costs to near $0.

## Support for All Modules

This infrastructure is designed to support all modules in the course:

- ✅ **Module 01 (Getting Started)**: Provides gpt-4o-mini for chat
- ✅ **Module 02 (RAG)**: Provides text-embedding-3-small for document embeddings
- ✅ **Module 03 (Agents & Tools)**: Both models support function calling
- ✅ **Module 04 (Production)**: Full monitoring and security setup included

Simply adjust the Container App code and environment variables as you progress through the modules!
