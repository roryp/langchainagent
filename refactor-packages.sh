#!/bin/bash

# Script to refactor package names from dev.rory to com.example
# Usage: bash refactor-packages.sh

set -e

OLD_PACKAGE="dev.rory.azure.langchain4j"
NEW_PACKAGE="com.example.langchain4j"
OLD_PATH="dev/rory/azure/langchain4j"
NEW_PATH="com/example/langchain4j"

echo "===================================="
echo "Package Refactoring Script"
echo "===================================="
echo "OLD: $OLD_PACKAGE"
echo "NEW: $NEW_PACKAGE"
echo "===================================="

# Function to refactor a module
refactor_module() {
    local module=$1
    echo ""
    echo "Processing module: $module"
    
    if [ ! -d "$module/src" ]; then
        echo "  Skipping $module (no src directory)"
        return
    fi
    
    # Find all Java files and update package declarations and imports
    echo "  Updating Java files..."
    find "$module/src" -name "*.java" -type f | while read -r file; do
        if grep -q "$OLD_PACKAGE" "$file"; then
            echo "    - $file"
            sed -i "s|$OLD_PACKAGE|$NEW_PACKAGE|g" "$file"
        fi
    done
    
    # Move directories
    echo "  Moving directory structure..."
    if [ -d "$module/src/main/java/$OLD_PATH" ]; then
        mkdir -p "$module/src/main/java/com/example"
        mv "$module/src/main/java/$OLD_PATH" "$module/src/main/java/com/example/langchain4j"
        # Clean up old directory structure
        rm -rf "$module/src/main/java/dev"
        echo "    - Moved main/java"
    fi
    
    if [ -d "$module/src/test/java/$OLD_PATH" ]; then
        mkdir -p "$module/src/test/java/com/example"
        mv "$module/src/test/java/$OLD_PATH" "$module/src/test/java/com/example/langchain4j"
        # Clean up old directory structure
        rm -rf "$module/src/test/java/dev"
        echo "    - Moved test/java"
    fi
}

# Refactor each module
refactor_module "01-getting-started"
refactor_module "02-rag"
refactor_module "03-agents-tools"

echo ""
echo "===================================="
echo "Refactoring Complete!"
echo "===================================="
echo ""
echo "Next steps:"
echo "1. Rebuild the project: mvn clean compile"
echo "2. Run tests: mvn test"
echo "3. Review changes: git diff"
echo "4. Commit: git add -A && git commit -m 'refactor: rename packages from dev.rory to com.example'"
echo ""
