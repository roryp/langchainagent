# Azure Infrastructure for LangChain4j Getting Started

This directory contains the Azure infrastructure as code (IaC) using Bicep and Azure Developer CLI (azd) for deploying the LangChain4j Getting Started application.

## Prerequisites

- [Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli) (version 2.50.0 or later)
- [Azure Developer CLI (azd)](https://learn.microsoft.com/azure/developer/azure-developer-cli/install-azd) (version 1.5.0 or later)
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- An Azure subscription with permissions to create resources

## Architecture

The infrastructure deploys the following Azure resources:

### Core Resources
- **Resource Group**: Container for all resources
- **User-Assigned Managed Identity**: For secure authentication between services

### Compute
- **Azure Container Registry (ACR)**: Stores the container images
- **Azure Container Apps Environment**: Hosts the application
- **Container App**: Runs the Spring Boot application

### AI Services
- **Azure OpenAI**: Cognitive Services with two model deployments:
  - **gpt-4o-mini**: Chat completion model (20K TPM capacity)
  - **text-embedding-3-small**: Embedding model for RAG (20K TPM capacity)

### Monitoring
- **Log Analytics Workspace**: Centralized logging and monitoring

## Resources Created

| Resource Type | Resource Name Pattern | Purpose |
|--------------|----------------------|---------|
| Resource Group | `rg-{environmentName}` | Contains all resources |
| User-Assigned Managed Identity | `id-{resourceToken}` | Secure service-to-service authentication |
| Log Analytics Workspace | `log-{resourceToken}` | Centralized logging |
| Container Registry | `acr{resourceToken}` | Container image storage |
| Container Apps Environment | `cae-{resourceToken}` | Container hosting environment |
| Container App | `ca-{resourceToken}` | Application runtime |
| Azure OpenAI | `aoai-{resourceToken}` | AI model hosting |

*Note: `{resourceToken}` is a unique string generated from subscription ID, environment name, and location*

## Quick Start

### 1. Initialize Azure Developer CLI

```bash
cd 01-getting-started
azd init
```

When prompted:
- Use the current directory
- Confirm the environment name (default: `langchain4j-dev`)

### 2. Provision Azure Resources

```bash
azd provision
```

You'll be prompted to:
- Select your Azure subscription
- Choose a location (recommended: `eastus` for Azure OpenAI availability)
- Provide a principal ID (optional, your user ID will be used by default)

This will:
- Create all Azure resources defined in `infra/main.bicep`
- Configure role assignments and permissions
- Output connection information

### 3. Deploy the Application

```bash
# Build and push the Docker image
azd deploy
```

This will:
- Build the Docker image using the Dockerfile
- Push the image to Azure Container Registry
- Update the Container App with the new image
- Set environment variables for Azure OpenAI connection

### 4. Access the Application

After deployment, get the application URL:

```bash
azd env get-values
```

Look for `APP_URL` in the output. You can test the endpoints:

```bash
# Test basic chat
curl -X POST https://{your-app-url}/api/chat \
  -H 'Content-Type: application/json' \
  -d '{"message":"Hello from Azure!"}'

# Start a conversation
curl -X POST https://{your-app-url}/api/conversation/start

# Send a message (use the conversationId from previous response)
curl -X POST https://{your-app-url}/api/conversation/chat \
  -H 'Content-Type: application/json' \
  -d '{"conversationId":"YOUR_ID","message":"Tell me about Azure OpenAI"}'
```

## Configuration

### Environment Variables

The infrastructure automatically configures these environment variables in the Container App:

- `AZURE_OPENAI_ENDPOINT`: Azure OpenAI service endpoint
- `AZURE_OPENAI_DEPLOYMENT`: Chat model deployment name (gpt-4o-mini)
- `AZURE_OPENAI_EMBEDDING_DEPLOYMENT`: Embedding model deployment name (text-embedding-3-small)
- `SPRING_PROFILES_ACTIVE`: Set to 'azure' for Azure-specific configuration

### Authentication

The application uses **Managed Identity** (passwordless) authentication by default. This is more secure than using API keys.

The infrastructure:
1. Creates a User-Assigned Managed Identity
2. Assigns the "Cognitive Services OpenAI User" role to the identity
3. Configures the Container App to use this identity
4. LangChain4j automatically uses Azure DefaultAzureCredential

### Customizing Model Deployments

To change model deployments, edit `infra/main.bicep` and modify the `openAiDeployments` parameter:

```bicep
param openAiDeployments array = [
  {
    name: 'gpt-4'  // Change model name
    model: {
      format: 'OpenAI'
      name: 'gpt-4'
      version: '0613'  // Change version
    }
    sku: {
      name: 'Standard'
      capacity: 30  // Change capacity (TPM in thousands)
    }
  }
  // Add more deployments...
]
```

Available models and versions: https://learn.microsoft.com/azure/ai-services/openai/concepts/models

### Scaling Configuration

The Container App is configured with:
- **Min replicas**: 1 (always running)
- **Max replicas**: 10 (scales based on HTTP traffic)
- **CPU**: 1.0 cores
- **Memory**: 2 GiB

To change scaling, edit `infra/core/host/container-app.bicep`:

```bicep
scale: {
  minReplicas: 1  // Minimum number of instances
  maxReplicas: 10  // Maximum number of instances
  rules: [
    {
      name: 'http-rule'
      http: {
        metadata: {
          concurrentRequests: '10'  // Requests per instance
        }
      }
    }
  ]
}
```

## Cost Optimization

### Development/Testing
For dev/test environments, you can reduce costs:

1. **OpenAI Capacity**: Reduce from 20K to 10K TPM
2. **Container App**: Set `minReplicas: 0` (scale to zero when idle)
3. **Log Analytics**: Reduce `retentionInDays` from 30 to 7

Edit `infra/main.bicep` and `infra/core/host/container-app.bicep` accordingly.

### Production
For production:
- Increase OpenAI capacity based on usage
- Enable zone redundancy for Container Apps Environment
- Consider Premium SKU for Container Registry
- Implement proper monitoring and alerts

## Monitoring and Logging

### View Container App Logs

```bash
# Using Azure CLI
az containerapp logs show \
  --name ca-{resourceToken} \
  --resource-group rg-{environmentName} \
  --follow

# Using azd
azd monitor
```

### View Azure OpenAI Metrics

Go to Azure Portal → Your OpenAI resource → Metrics:
- Token-Based Utilization
- HTTP Request Rate
- Time To Response

### Application Insights

The Container Apps Environment is configured with Log Analytics. Logs are automatically collected:

```bash
# Query logs using Azure CLI
az monitor log-analytics query \
  --workspace {workspace-id} \
  --analytics-query "ContainerAppConsoleLogs_CL | where TimeGenerated > ago(1h)"
```

## Troubleshooting

### Deployment Fails

**Issue**: `azd provision` fails with quota or capacity errors

**Solution**: 
1. Try a different region (Azure OpenAI availability varies)
2. Check your subscription quotas:
   ```bash
   az vm list-usage --location eastus -o table
   ```

**Issue**: Container Registry access denied

**Solution**: Verify the managed identity has AcrPull role:
```bash
az role assignment list --assignee {managed-identity-principal-id} --all
```

### Application Not Starting

**Issue**: Container app shows "ImagePullBackOff"

**Solution**:
1. Ensure the image was pushed successfully:
   ```bash
   az acr repository list --name acr{resourceToken}
   ```
2. Check Container App logs for authentication issues

**Issue**: 401 Unauthorized from Azure OpenAI

**Solution**:
1. Verify managed identity role assignment
2. Check that `AZURE_OPENAI_ENDPOINT` environment variable is set correctly
3. Ensure model deployments are complete (can take a few minutes)

### Performance Issues

**Issue**: Slow response times

**Solution**:
1. Check OpenAI token usage and throttling
2. Increase Container App replicas or resources
3. Verify network connectivity between services

## Updating Infrastructure

To update the infrastructure after making changes to Bicep files:

```bash
# Preview changes
azd provision --preview

# Apply changes
azd provision
```

## Clean Up

To delete all resources:

```bash
# Delete all resources
azd down

# Delete everything including the environment
azd down --purge
```

**Warning**: This will permanently delete all resources and data.

## Security Best Practices

1. **Use Managed Identity**: Already configured by default
2. **Network Security**: For production, enable private endpoints:
   - Edit `infra/core/ai/cognitiveservices.bicep`
   - Set `publicNetworkAccess: 'Disabled'`
   - Add private endpoint configuration

3. **Secrets Management**: For any additional secrets, use Azure Key Vault:
   ```bash
   az keyvault create --name kv-{resourceToken} --resource-group rg-{environmentName}
   ```

4. **RBAC**: Use least privilege principle for role assignments

## Additional Resources

- [Azure Container Apps Documentation](https://learn.microsoft.com/azure/container-apps/)
- [Azure OpenAI Service Documentation](https://learn.microsoft.com/azure/ai-services/openai/)
- [Azure Developer CLI Documentation](https://learn.microsoft.com/azure/developer/azure-developer-cli/)
- [Bicep Documentation](https://learn.microsoft.com/azure/azure-resource-manager/bicep/)
- [LangChain4j Azure OpenAI Integration](https://docs.langchain4j.dev/integrations/language-models/azure-open-ai)

## Support

For issues:
1. Check the [troubleshooting section](#troubleshooting) above
2. Review Azure Container Apps logs
3. Check Azure OpenAI service health
4. Open an issue in the repository

## License

See the root [LICENSE](../../LICENSE) file for details.
