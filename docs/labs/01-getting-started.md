# Lab 01 — Getting Started

This lab walks you through building and running a minimal chat
application using LangChain4j with Azure OpenAI.  Follow these steps
to get started:

1. **Configure your environment.**  Copy `.env.example` to `.env` in
   the project root and fill in the values for `AZURE_OPENAI_ENDPOINT`,
   `AZURE_OPENAI_API_KEY` and `AZURE_OPENAI_DEPLOYMENT`.
2. **Run the application.**  From the root of the repository, execute
   the following script to build and start the Spring Boot
   application:

   ```bash
   scripts/run-getting-started.sh
   ```

   This script sources your `.env` file, generates a Maven wrapper if
   necessary and then uses it to run the application on port 8080.
3. **Call the chat endpoint.**  Once the application is running,
   issue a POST request to `/api/chat` with a JSON payload
   containing a `message` field:

   ```bash
   curl -s http://localhost:8080/api/chat \
     -H 'Content-Type: application/json' \
     -d '{"message":"Give me a fun fact about the Jacaranda trees in Johannesburg."}'
   ```

   The response will include your prompt and the model‑generated answer.

If you prefer to build the application image, run
`mvn -pl 01-getting-started -am package` and build the Docker image
from the `01-getting-started` context.  Use `docker-compose.yml` to
launch the container with your environment variables.