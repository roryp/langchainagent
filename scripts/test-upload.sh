#!/bin/bash
# Test script for RAG document upload endpoint
# Usage: ./test-upload.sh [HOST_URL]
# Example: ./test-upload.sh https://ca-rag-ozhotol5yje76.livelyplant-8c187f15.eastus2.azurecontainerapps.io

set -e

# Configuration
HOST="${1:-http://localhost:8081}"
UPLOAD_ENDPOINT="$HOST/api/documents/upload"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}  RAG Document Upload Test Suite${NC}"
echo -e "${BLUE}================================================${NC}"
echo ""
echo -e "${YELLOW}Target:${NC} $UPLOAD_ENDPOINT"
echo ""

# Create a test text file
TEST_FILE="test-document.txt"
echo "Creating test document..."
cat > "$TEST_FILE" << 'EOF'
# Azure OpenAI and LangChain4j

Azure OpenAI Service provides REST API access to OpenAI's powerful language models.
LangChain4j is a Java framework for building applications with large language models.

## Key Features

- Chat completion with GPT-4 and GPT-3.5 Turbo
- Text embeddings for semantic search
- Function calling for tool use
- Streaming responses for real-time interaction

## Use Cases

1. Customer support chatbots
2. Document Q&A systems
3. Code generation assistants
4. Content summarization
5. Semantic search applications

The combination of Azure OpenAI and LangChain4j enables developers to build 
production-ready AI applications with enterprise-grade security and scalability.
EOF

# Test 1: Upload text file
echo -e "${YELLOW}Test 1: Uploading text document...${NC}"
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X POST "$UPLOAD_ENDPOINT" \
  -F "file=@$TEST_FILE")

HTTP_STATUS=$(echo "$RESPONSE" | tr -d '\n' | sed -e 's/.*HTTP_STATUS://')
BODY=$(echo "$RESPONSE" | sed -e 's/HTTP_STATUS:.*//')

if [ "$HTTP_STATUS" -eq 200 ]; then
    echo -e "${GREEN}✓ Success!${NC}"
    echo "Response: $BODY" | jq '.' 2>/dev/null || echo "$BODY"
else
    echo -e "${RED}✗ Failed with status $HTTP_STATUS${NC}"
    echo "Response: $BODY"
    rm -f "$TEST_FILE"
    exit 1
fi

echo ""

# Test 2: Upload empty file
echo -e "${YELLOW}Test 2: Uploading empty file (should fail)...${NC}"
EMPTY_FILE="empty.txt"
touch "$EMPTY_FILE"

RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X POST "$UPLOAD_ENDPOINT" \
  -F "file=@$EMPTY_FILE")

HTTP_STATUS=$(echo "$RESPONSE" | tr -d '\n' | sed -e 's/.*HTTP_STATUS://')
BODY=$(echo "$RESPONSE" | sed -e 's/HTTP_STATUS:.*//')

if [ "$HTTP_STATUS" -eq 400 ]; then
    echo -e "${GREEN}✓ Correctly rejected empty file${NC}"
    echo "Response: $BODY" | jq '.' 2>/dev/null || echo "$BODY"
else
    echo -e "${RED}✗ Expected status 400, got $HTTP_STATUS${NC}"
    echo "Response: $BODY"
fi

echo ""

# Test 3: Health check
echo -e "${YELLOW}Test 3: Health check...${NC}"
HEALTH_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X GET "$HOST/api/documents/health")

HTTP_STATUS=$(echo "$HEALTH_RESPONSE" | tr -d '\n' | sed -e 's/.*HTTP_STATUS://')
BODY=$(echo "$HEALTH_RESPONSE" | sed -e 's/HTTP_STATUS:.*//')

if [ "$HTTP_STATUS" -eq 200 ]; then
    echo -e "${GREEN}✓ Service is healthy${NC}"
    echo "Response: $BODY"
else
    echo -e "${RED}✗ Health check failed with status $HTTP_STATUS${NC}"
    echo "Response: $BODY"
fi

# Cleanup
echo ""
echo "Cleaning up test files..."
rm -f "$TEST_FILE" "$EMPTY_FILE"

echo ""
echo -e "${GREEN}All tests completed!${NC}"
