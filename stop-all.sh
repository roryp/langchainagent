#!/bin/bash

# Stop all LangChain4j applications

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "Stopping all LangChain4j applications..."

# Function to stop applications on a specific port
stop_on_port() {
    local port=$1
    local module=$2
    
    if command -v lsof &> /dev/null; then
        # Use lsof if available (Mac/Linux)
        local pids=$(lsof -ti:$port 2>/dev/null)
        if [ ! -z "$pids" ]; then
            echo "Stopping $module on port $port (PIDs: $pids)"
            kill -9 $pids 2>/dev/null
        fi
    else
        # Use netstat for Windows Git Bash
        local pids=$(netstat -ano | grep ":$port" | awk '{print $5}' | sort -u)
        if [ ! -z "$pids" ]; then
            echo "Stopping $module on port $port"
            for pid in $pids; do
                taskkill //F //PID $pid 2>/dev/null
            done
        fi
    fi
}

# Stop module 01-introduction (port 8080)
stop_on_port 8080 "01-introduction"

# Stop module 02-prompt-engineering (port 8083)
stop_on_port 8083 "02-prompt-engineering"

# Stop module 03-rag (port 8081)
stop_on_port 8081 "03-rag"

# Also try to find and kill any Java processes running our JARs
pkill -f "introduction-0.1.0.jar" 2>/dev/null
pkill -f "prompt-engineering-0.1.0.jar" 2>/dev/null
pkill -f "rag-0.1.0.jar" 2>/dev/null

echo ""
echo "All applications stopped."
