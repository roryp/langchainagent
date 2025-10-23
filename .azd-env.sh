#!/bin/bash

# Azure Developer CLI (azd) environment integration
# This script loads environment variables from azd and creates/updates .env file

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env"

echo "Loading Azure Developer CLI (azd) environment..."

# Check if azd is installed
if ! command -v azd &> /dev/null; then
    echo "Error: Azure Developer CLI (azd) is not installed"
    echo "Install it from: https://learn.microsoft.com/azure/developer/azure-developer-cli/install-azd"
    exit 1
fi

# Get environment variables from azd
AZURE_ENV_NAME=$(azd env get-value AZURE_ENV_NAME 2>/dev/null || echo "")
AZURE_LOCATION=$(azd env get-value AZURE_LOCATION 2>/dev/null || echo "")
AZURE_OPENAI_ENDPOINT=$(azd env get-value AZURE_OPENAI_ENDPOINT 2>/dev/null || echo "")
AZURE_OPENAI_API_KEY=$(azd env get-value AZURE_OPENAI_KEY 2>/dev/null || echo "")
AZURE_OPENAI_DEPLOYMENT=$(azd env get-value AZURE_OPENAI_DEPLOYMENT 2>/dev/null || echo "")
AZURE_OPENAI_EMBEDDING_DEPLOYMENT=$(azd env get-value AZURE_OPENAI_EMBEDDING_DEPLOYMENT 2>/dev/null || echo "")
AZURE_OPENAI_NAME=$(azd env get-value AZURE_OPENAI_NAME 2>/dev/null || echo "")
AZURE_RESOURCE_GROUP_ID=$(azd env get-value AZURE_RESOURCE_GROUP_ID 2>/dev/null || echo "")
AZURE_RESOURCE_GROUP_NAME=$(azd env get-value AZURE_RESOURCE_GROUP_NAME 2>/dev/null || echo "")
AZURE_SUBSCRIPTION_ID=$(azd env get-value AZURE_SUBSCRIPTION_ID 2>/dev/null || echo "")
AZURE_TENANT_ID=$(azd env get-value AZURE_TENANT_ID 2>/dev/null || echo "")

# Fallback to default deployment names if not set
if [ -z "$AZURE_OPENAI_DEPLOYMENT" ]; then
    AZURE_OPENAI_DEPLOYMENT="gpt-5"
fi

if [ -z "$AZURE_OPENAI_EMBEDDING_DEPLOYMENT" ]; then
    AZURE_OPENAI_EMBEDDING_DEPLOYMENT="text-embedding-3-small"
fi

# Validate required variables
if [ -z "$AZURE_OPENAI_ENDPOINT" ]; then
    echo "Error: AZURE_OPENAI_ENDPOINT not found in azd environment"
    echo "Set it with: azd env set AZURE_OPENAI_ENDPOINT <value>"
    exit 1
fi

if [ -z "$AZURE_OPENAI_API_KEY" ]; then
    echo "Error: AZURE_OPENAI_API_KEY not found in azd environment"
    echo "Set it with: azd env set AZURE_OPENAI_API_KEY <value>"
    exit 1
fi

# Create/update .env file
echo "Creating/updating .env file from azd environment..."

cat > "$ENV_FILE" << EOF
AZURE_ENV_NAME="$AZURE_ENV_NAME"
AZURE_LOCATION="$AZURE_LOCATION"
AZURE_OPENAI_DEPLOYMENT="$AZURE_OPENAI_DEPLOYMENT"
AZURE_OPENAI_EMBEDDING_DEPLOYMENT="$AZURE_OPENAI_EMBEDDING_DEPLOYMENT"
AZURE_OPENAI_ENDPOINT="$AZURE_OPENAI_ENDPOINT"
AZURE_OPENAI_API_KEY="$AZURE_OPENAI_API_KEY"
AZURE_OPENAI_KEY="$AZURE_OPENAI_API_KEY"
AZURE_OPENAI_NAME="$AZURE_OPENAI_NAME"
AZURE_RESOURCE_GROUP_ID="$AZURE_RESOURCE_GROUP_ID"
AZURE_RESOURCE_GROUP_NAME="$AZURE_RESOURCE_GROUP_NAME"
AZURE_SUBSCRIPTION_ID="$AZURE_SUBSCRIPTION_ID"
AZURE_TENANT_ID="$AZURE_TENANT_ID"
EOF

echo "✓ Environment variables successfully loaded from azd"
echo "✓ .env file created/updated at: $ENV_FILE"
echo ""
echo "Configuration:"
echo "  AZURE_OPENAI_ENDPOINT: $AZURE_OPENAI_ENDPOINT"
echo "  AZURE_OPENAI_DEPLOYMENT: $AZURE_OPENAI_DEPLOYMENT"
echo "  AZURE_OPENAI_EMBEDDING_DEPLOYMENT: $AZURE_OPENAI_EMBEDDING_DEPLOYMENT"
echo ""
echo "You can now run: ./start-all.sh"
