#!/usr/bin/env bash
set -euo pipefail
# Run the Getting Started module locally using the provided
# environment variables.  This script expects a `.env` file at the
# repository root.  For convenience it sources the variables and
# launches the Spring Boot application via the Maven wrapper.

# Load environment variables from `.env` if it exists.
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs) || true
fi

cd 01-getting-started

# Ensure the Maven wrapper exists; generate it on the fly if missing.
if [ ! -f mvnw ]; then
  mvn -N wrapper
fi

# Build and run the Spring Boot application, skipping tests for faster startup.
./mvnw -q -DskipTests spring-boot:run