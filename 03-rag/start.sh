#!/bin/bash

# Start script for 03-rag module
# Sources environment variables from parent .env file

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
ENV_FILE="$ROOT_DIR/.env"

# Check if .env exists
if [ ! -f "$ENV_FILE" ]; then
    echo "Error: .env file not found at $ENV_FILE"
    echo "Please run 'azd up' from 01-introduction module first"
    echo "Or run: bash $ROOT_DIR/.azd-env.sh"
    exit 1
fi

# Source environment variables
echo "Loading environment variables from $ENV_FILE"
set -a
source "$ENV_FILE"
set +a

# Verify required variables
if [ -z "$AZURE_OPENAI_ENDPOINT" ] || [ -z "$AZURE_OPENAI_API_KEY" ] || [ -z "$AZURE_OPENAI_DEPLOYMENT" ] || [ -z "$AZURE_OPENAI_EMBEDDING_DEPLOYMENT" ]; then
    echo "Error: Missing required environment variables"
    exit 1
fi

echo "Starting 03-rag on port 8081..."
echo "Configuration:"
echo "  Endpoint: $AZURE_OPENAI_ENDPOINT"
echo "  Chat Model: $AZURE_OPENAI_DEPLOYMENT"
echo "  Embedding Model: $AZURE_OPENAI_EMBEDDING_DEPLOYMENT"
echo ""

mvn spring-boot:run
