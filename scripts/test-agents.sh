#!/bin/bash
# Test script for Agents & Tools module
# Usage: ./test-agents.sh [HOST_URL]
# Example: ./test-agents.sh https://ca-agents-ozhotol5yje76.livelyplant-8c187f15.eastus2.azurecontainerapps.io

set -e

# Configuration
HOST="${1:-http://localhost:8082}"
AGENT_ENDPOINT="$HOST/api/agent"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}  Agents & Tools Test Suite${NC}"
echo -e "${BLUE}================================================${NC}"
echo ""
echo -e "${YELLOW}Target:${NC} $AGENT_ENDPOINT"
echo ""

# Test 1: Start Agent Session
echo -e "${YELLOW}Test 1: Starting agent session...${NC}"
RESPONSE=$(curl -s -X POST "$AGENT_ENDPOINT/start")
SESSION_ID=$(echo "$RESPONSE" | grep -o '"sessionId":"[^"]*' | cut -d'"' -f4)

if [ -n "$SESSION_ID" ]; then
    echo -e "${GREEN}✓ Session started: $SESSION_ID${NC}"
else
    echo -e "${RED}✗ Failed to start session${NC}"
    echo "$RESPONSE"
    exit 1
fi
echo ""

# Test 2: Weather Tool
echo -e "${YELLOW}Test 2: Testing weather tool...${NC}"
WEATHER_REQUEST='{
  "message": "What is the weather in New York and London?",
  "sessionId": "'$SESSION_ID'"
}'

RESPONSE=$(curl -s -X POST "$AGENT_ENDPOINT/execute" \
  -H "Content-Type: application/json" \
  -d "$WEATHER_REQUEST")

if echo "$RESPONSE" | grep -q "weather"; then
    echo -e "${GREEN}✓ Weather tool executed successfully${NC}"
    echo "Response excerpt: $(echo "$RESPONSE" | head -c 200)..."
else
    echo -e "${RED}✗ Weather tool failed${NC}"
    echo "$RESPONSE"
fi
echo ""

# Test 3: Calculator Tool
echo -e "${YELLOW}Test 3: Testing calculator tool...${NC}"
CALC_REQUEST='{
  "message": "Calculate the square root of 144 and then multiply it by 5",
  "sessionId": "'$SESSION_ID'"
}'

RESPONSE=$(curl -s -X POST "$AGENT_ENDPOINT/execute" \
  -H "Content-Type: application/json" \
  -d "$CALC_REQUEST")

if echo "$RESPONSE" | grep -q "60"; then
    echo -e "${GREEN}✓ Calculator tool executed successfully (√144 × 5 = 60)${NC}"
else
    echo -e "${RED}✗ Calculator tool failed${NC}"
    echo "$RESPONSE"
fi
echo ""

# Test 4: Multi-Tool Task
echo -e "${YELLOW}Test 4: Testing multi-tool task...${NC}"
MULTI_REQUEST='{
  "message": "What is the weather in Seattle? Also calculate 25 celsius to fahrenheit",
  "sessionId": "'$SESSION_ID'"
}'

RESPONSE=$(curl -s -X POST "$AGENT_ENDPOINT/execute" \
  -H "Content-Type: application/json" \
  -d "$MULTI_REQUEST")

TOOL_COUNT=$(echo "$RESPONSE" | grep -o '"toolExecutions":\[[^]]*\]' | grep -o '"toolName"' | wc -l)

if [ "$TOOL_COUNT" -gt 1 ]; then
    echo -e "${GREEN}✓ Multiple tools used: $TOOL_COUNT tools executed${NC}"
else
    echo -e "${YELLOW}⚠ Only $TOOL_COUNT tool(s) used${NC}"
fi
echo ""

# Test 5: List Available Tools
echo -e "${YELLOW}Test 5: Getting available tools...${NC}"
RESPONSE=$(curl -s -X GET "$AGENT_ENDPOINT/tools")

if echo "$RESPONSE" | grep -q "Weather Tool"; then
    echo -e "${GREEN}✓ Tools list retrieved${NC}"
    echo "Available tools:"
    echo "$RESPONSE" | grep -o '"[^"]*Tool[^"]*"' | sed 's/"//g' | sed 's/^/  - /'
else
    echo -e "${RED}✗ Failed to get tools${NC}"
fi
echo ""

# Test 6: Chat Interface
echo -e "${YELLOW}Test 6: Testing chat interface...${NC}"
CHAT_REQUEST='{
  "message": "Hello! Can you help me with some calculations?",
  "sessionId": "'$SESSION_ID'"
}'

RESPONSE=$(curl -s -X POST "$AGENT_ENDPOINT/chat" \
  -H "Content-Type: application/json" \
  -d "$CHAT_REQUEST")

if echo "$RESPONSE" | grep -q "answer"; then
    echo -e "${GREEN}✓ Chat interface working${NC}"
else
    echo -e "${RED}✗ Chat interface failed${NC}"
fi
echo ""

# Test 7: Clear Session
echo -e "${YELLOW}Test 7: Clearing session...${NC}"
RESPONSE=$(curl -s -X DELETE "$AGENT_ENDPOINT/session/$SESSION_ID")

if echo "$RESPONSE" | grep -q "cleared"; then
    echo -e "${GREEN}✓ Session cleared${NC}"
else
    echo -e "${RED}✗ Failed to clear session${NC}"
fi
echo ""

# Health Check
echo -e "${YELLOW}Health Check...${NC}"
RESPONSE=$(curl -s -X GET "$AGENT_ENDPOINT/health")

if echo "$RESPONSE" | grep -q "healthy"; then
    echo -e "${GREEN}✓ Agent service is healthy${NC}"
else
    echo -e "${RED}✗ Health check failed${NC}"
fi

echo ""
echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}  Test Suite Complete${NC}"
echo -e "${BLUE}================================================${NC}"
