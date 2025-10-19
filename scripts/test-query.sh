#!/bin/bash
# Test script for RAG query endpoint
# Usage: ./test-query.sh [HOST_URL]
# Example: ./test-query.sh https://ca-rag-ozhotol5yje76.livelyplant-8c187f15.eastus2.azurecontainerapps.io

set -e

# Configuration
HOST="${1:-http://localhost:8081}"
RAG_ENDPOINT="$HOST/api/rag/ask"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}  RAG Query Test Suite${NC}"
echo -e "${BLUE}================================================${NC}"
echo ""
echo -e "${YELLOW}Target:${NC} $RAG_ENDPOINT"
echo ""

# Test 1: Valid question
echo -e "${YELLOW}Test 1: Asking a valid question...${NC}"
QUESTION='{"question": "What are the key features of Azure OpenAI and LangChain4j?", "conversationId": "test-session-1"}'

RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X POST "$RAG_ENDPOINT" \
  -H "Content-Type: application/json" \
  -d "$QUESTION")

HTTP_STATUS=$(echo "$RESPONSE" | tr -d '\n' | sed -e 's/.*HTTP_STATUS://')
BODY=$(echo "$RESPONSE" | sed -e 's/HTTP_STATUS:.*//')

if [ "$HTTP_STATUS" -eq 200 ]; then
    echo -e "${GREEN}✓ Success!${NC}"
    echo "Response:"
    echo "$BODY" | jq '.' 2>/dev/null || echo "$BODY"
    
    # Check if sources are included
    SOURCES=$(echo "$BODY" | jq '.sources | length' 2>/dev/null || echo "0")
    echo ""
    echo "Number of sources: $SOURCES"
else
    echo -e "${RED}✗ Failed with status $HTTP_STATUS${NC}"
    echo "Response: $BODY"
    exit 1
fi

echo ""

# Test 2: Empty question (should fail)
echo -e "${YELLOW}Test 2: Sending empty question (should fail)...${NC}"
EMPTY_QUESTION='{"question": "", "conversationId": "test-session-2"}'

RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X POST "$RAG_ENDPOINT" \
  -H "Content-Type: application/json" \
  -d "$EMPTY_QUESTION")

HTTP_STATUS=$(echo "$RESPONSE" | tr -d '\n' | sed -e 's/.*HTTP_STATUS://')
BODY=$(echo "$RESPONSE" | sed -e 's/HTTP_STATUS:.*//')

if [ "$HTTP_STATUS" -eq 400 ]; then
    echo -e "${GREEN}✓ Correctly rejected empty question${NC}"
    echo "Response: $BODY" | jq '.' 2>/dev/null || echo "$BODY"
else
    echo -e "${RED}✗ Expected status 400, got $HTTP_STATUS${NC}"
    echo "Response: $BODY"
fi

echo ""

# Test 3: Question with no relevant context
echo -e "${YELLOW}Test 3: Asking question with no relevant documents...${NC}"
UNRELATED_QUESTION='{"question": "What is the capital of France?", "conversationId": "test-session-3"}'

RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X POST "$RAG_ENDPOINT" \
  -H "Content-Type: application/json" \
  -d "$UNRELATED_QUESTION")

HTTP_STATUS=$(echo "$RESPONSE" | tr -d '\n' | sed -e 's/.*HTTP_STATUS://')
BODY=$(echo "$RESPONSE" | sed -e 's/HTTP_STATUS:.*//')

if [ "$HTTP_STATUS" -eq 200 ]; then
    echo -e "${GREEN}✓ Request processed${NC}"
    echo "Response:"
    echo "$BODY" | jq '.answer' 2>/dev/null || echo "$BODY"
    echo ""
    echo "(Should indicate no relevant documents found)"
else
    echo -e "${RED}✗ Failed with status $HTTP_STATUS${NC}"
    echo "Response: $BODY"
fi

echo ""

# Test 4: Conversational question
echo -e "${YELLOW}Test 4: Follow-up question with conversation ID...${NC}"
FOLLOWUP='{"question": "Can you explain more about function calling?", "conversationId": "test-session-1"}'

RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X POST "$RAG_ENDPOINT" \
  -H "Content-Type: application/json" \
  -d "$FOLLOWUP")

HTTP_STATUS=$(echo "$RESPONSE" | tr -d '\n' | sed -e 's/.*HTTP_STATUS://')
BODY=$(echo "$RESPONSE" | sed -e 's/HTTP_STATUS:.*//')

if [ "$HTTP_STATUS" -eq 200 ]; then
    echo -e "${GREEN}✓ Success!${NC}"
    echo "Response:"
    echo "$BODY" | jq '.answer' 2>/dev/null || echo "$BODY"
else
    echo -e "${RED}✗ Failed with status $HTTP_STATUS${NC}"
    echo "Response: $BODY"
fi

echo ""

# Test 5: Health check
echo -e "${YELLOW}Test 5: Health check...${NC}"
HEALTH_RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X GET "$HOST/api/rag/health")

HTTP_STATUS=$(echo "$HEALTH_RESPONSE" | tr -d '\n' | sed -e 's/.*HTTP_STATUS://')
BODY=$(echo "$HEALTH_RESPONSE" | sed -e 's/HTTP_STATUS:.*//')

if [ "$HTTP_STATUS" -eq 200 ]; then
    echo -e "${GREEN}✓ Service is healthy${NC}"
    echo "Response: $BODY"
else
    echo -e "${RED}✗ Health check failed with status $HTTP_STATUS${NC}"
    echo "Response: $BODY"
fi

echo ""
echo -e "${GREEN}All tests completed!${NC}"
echo ""
echo -e "${YELLOW}Note:${NC} Make sure to upload documents first using test-upload.sh"
echo "to get relevant answers with sources."
