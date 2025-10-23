#!/bin/bash

# Start all LangChain4j applications
# This script sources the centralized .env file and starts both modules

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env"

# Check if .env exists
if [ ! -f "$ENV_FILE" ]; then
    echo "Error: .env file not found at $ENV_FILE"
    echo "Please copy .env.example to .env and fill in your credentials"
    exit 1
fi

# Source environment variables
echo "Loading environment variables from $ENV_FILE"
set -a
source "$ENV_FILE"
set +a

# Verify required variables are set
if [ -z "$AZURE_OPENAI_ENDPOINT" ] || [ -z "$AZURE_OPENAI_API_KEY" ] || [ -z "$AZURE_OPENAI_DEPLOYMENT" ]; then
    echo "Error: Missing required environment variables"
    echo "Please ensure AZURE_OPENAI_ENDPOINT, AZURE_OPENAI_API_KEY, and AZURE_OPENAI_DEPLOYMENT are set in .env"
    exit 1
fi

echo "Environment variables loaded successfully"
echo "AZURE_OPENAI_ENDPOINT: $AZURE_OPENAI_ENDPOINT"
echo "AZURE_OPENAI_DEPLOYMENT: $AZURE_OPENAI_DEPLOYMENT"

# Function to start an application
start_app() {
    local module=$1
    local port=$2
    local jar_file=$3
    
    echo ""
    echo "Starting $module on port $port..."
    
    cd "$SCRIPT_DIR/$module"
    
    if [ ! -f "$jar_file" ]; then
        echo "Error: JAR file not found at $jar_file"
        echo "Please build the module first: mvn clean package -DskipTests"
        return 1
    fi
    
    # Check if port is already in use
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 || netstat -ano | grep ":$port" >/dev/null 2>&1; then
        echo "Warning: Port $port is already in use. Skipping $module"
        return 0
    fi
    
    # Start application in background with environment variables
    AZURE_OPENAI_ENDPOINT="$AZURE_OPENAI_ENDPOINT" \
    AZURE_OPENAI_API_KEY="$AZURE_OPENAI_API_KEY" \
    AZURE_OPENAI_DEPLOYMENT="$AZURE_OPENAI_DEPLOYMENT" \
    AZURE_OPENAI_EMBEDDING_DEPLOYMENT="$AZURE_OPENAI_EMBEDDING_DEPLOYMENT" \
    nohup java -jar "$jar_file" > "$module.log" 2>&1 &
    local pid=$!
    echo "Started $module with PID $pid"
    
    # Wait a moment and check if it's still running
    sleep 2
    if ps -p $pid > /dev/null 2>&1; then
        echo "✓ $module is running on port $port"
    else
        echo "✗ $module failed to start. Check $module/$module.log for details"
    fi
}

# Start module 01-introduction (port 8080)
start_app "01-introduction" 8080 "target/introduction-0.1.0.jar"

# Start module 02-prompt-engineering (port 8083)
start_app "02-prompt-engineering" 8083 "target/prompt-engineering-0.1.0.jar"

# Start module 03-rag (port 8081)
start_app "03-rag" 8081 "target/rag-0.1.0.jar"

echo ""
echo "============================================"
echo "All applications started!"
echo "01-introduction:      http://localhost:8080"
echo "02-prompt-engineering: http://localhost:8083"
echo "03-rag:               http://localhost:8081"
echo "============================================"
echo ""
echo "To stop all applications, run: ./stop-all.sh"
