# LangChain4j for Beginners

A course for building AI applications with LangChain4j and Azure OpenAI GPT-5, from basic chat to AI agents.

## Table of Contents

1. [Quick Start](00-quick-start/) - Get started with LangChain4j
2. [Introduction](01-introduction/) - Learn the fundamentals of LangChain4j
3. [Prompt Engineering](02-prompt-engineering/) - Master effective prompt design
4. [RAG (Retrieval-Augmented Generation)](03-rag/) - Build intelligent knowledge-based systems
5. [Tools](04-tools/) - Integrate external tools and APIs with AI agents
6. [MCP (Model Context Protocol)](05-mcp/) - Work with the Model Context Protocol

Start with [Quick Start](00-quick-start/) and progress through the modules.

---

## Quick Setup

### 1. Configure Credentials (One-Time Setup)

All modules share a centralized configuration. Set it up once:

**Option A: Manual Configuration**

```bash
# Copy the example environment file
cp .env.example .env

# Edit .env and add your Azure OpenAI credentials
```

**Option B: Use Azure Developer CLI (azd)**

```bash
# After deploying infrastructure with azd up
./azd-env.sh              # Loads credentials from azd environment
```

### 2. Build and Run

```bash
# Build all modules
mvn clean package -DskipTests

# Start all applications
./start-all.sh
```

This starts:
- **01-introduction**: http://localhost:8080
- **02-prompt-engineering**: http://localhost:8083

To stop all applications:
```bash
./stop-all.sh
```

### 3. Start Individual Modules

```bash
# Start module 01 only
cd 01-introduction && ./start.sh

# Start module 02 only
cd 02-prompt-engineering && ./start.sh
```

---

## Environment Variables

All modules use these shared environment variables from the root `.env` file:

| Variable | Description | Example |
|----------|-------------|---------|
| `AZURE_OPENAI_ENDPOINT` | Your Azure OpenAI resource endpoint | `https://your-resource.openai.azure.com/` |
| `AZURE_OPENAI_API_KEY` | Your Azure OpenAI API key | `your-api-key-here` |
| `AZURE_OPENAI_DEPLOYMENT` | Deployment name for GPT model | `gpt-5` |
| `AZURE_OPENAI_EMBEDDING_DEPLOYMENT` | Deployment name for embeddings | `text-embedding-3-small` |

**Benefits of Centralized Configuration:**
- ✅ Single source of truth - one `.env` file for all modules
- ✅ No duplicate credentials across modules
- ✅ Easy to update and maintain
- ✅ Azure Developer CLI (azd) integration
- ✅ CI/CD ready

---

## Deployment

### Azure Developer CLI (azd) Integration

#### Prerequisites

Install Azure Developer CLI:
```bash
# Windows
winget install microsoft.azd

# macOS
brew install azd

# Linux
curl -fsSL https://aka.ms/install-azd.sh | bash
```

#### Setting up azd Environment

If you've deployed infrastructure with azd:

```bash
# Initialize (if not already done)
azd init

# Set environment variables
azd env set AZURE_OPENAI_ENDPOINT "https://your-resource.openai.azure.com/"
azd env set AZURE_OPENAI_API_KEY "your-api-key"
azd env set AZURE_OPENAI_DEPLOYMENT "gpt-5"
azd env set AZURE_OPENAI_EMBEDDING_DEPLOYMENT "text-embedding-3-small"

# Load variables into .env
./azd-env.sh

# Start applications
./start-all.sh
```

#### After Deployment

When you deploy with `azd up`, the infrastructure provisions Azure OpenAI resources:

```bash
# Load the deployed resource credentials
./azd-env.sh

# Build and start applications
mvn clean package -DskipTests
./start-all.sh
```

### CI/CD Integration

In GitHub Actions or other CI/CD:

```yaml
- name: Set up environment
  run: |
    echo "AZURE_OPENAI_ENDPOINT=${{ secrets.AZURE_OPENAI_ENDPOINT }}" >> .env
    echo "AZURE_OPENAI_API_KEY=${{ secrets.AZURE_OPENAI_API_KEY }}" >> .env
    echo "AZURE_OPENAI_DEPLOYMENT=${{ secrets.AZURE_OPENAI_DEPLOYMENT }}" >> .env

- name: Build applications
  run: mvn clean package -DskipTests

- name: Start applications
  run: ./start-all.sh
```

---

## Troubleshooting

### Port Already in Use

```bash
# Stop all applications
./stop-all.sh

# Or manually kill specific ports
# Port 8080 (module 01)
lsof -ti:8080 | xargs kill -9  # Mac/Linux
netstat -ano | findstr :8080    # Windows (then taskkill)

# Port 8083 (module 02)
lsof -ti:8083 | xargs kill -9  # Mac/Linux
netstat -ano | findstr :8083    # Windows (then taskkill)
```

### Missing Environment Variables

If you see "Could not resolve placeholder" errors:

1. Check that `.env` exists in the root directory
2. Verify all required variables are set in `.env`
3. Ensure there are no spaces around the `=` in `.env`

```bash
# Verify .env file
cat .env

# Test loading
source .env
echo $AZURE_OPENAI_ENDPOINT
```

### Application Won't Start

```bash
# Check logs
tail -f 01-introduction/01-introduction.log
tail -f 02-prompt-engineering/02-prompt-engineering.log

# Verify JAR files exist
ls -lh 01-introduction/target/introduction-0.1.0.jar
ls -lh 02-prompt-engineering/target/prompt-engineering-0.1.0.jar

# Rebuild if needed
mvn clean package -DskipTests
```

### azd-env.sh Fails

```bash
# Install Azure Developer CLI (if not installed)
winget install microsoft.azd  # Windows
brew install azd              # macOS

# Set variables manually in azd
azd env set AZURE_OPENAI_ENDPOINT "https://..."
azd env set AZURE_OPENAI_API_KEY "..."
azd env set AZURE_OPENAI_DEPLOYMENT "gpt-5"

# Try again
./azd-env.sh
```

---

## Script Reference

| Script | Description |
|--------|-------------|
| `start-all.sh` | Start all applications (loads `.env`, starts on ports 8080 & 8083) |
| `stop-all.sh` | Stop all applications (kills processes on configured ports) |
| `azd-env.sh` | Load environment from Azure Developer CLI into `.env` |
| `01-introduction/start.sh` | Start module 01 only (sources root `.env`) |
| `02-prompt-engineering/start.sh` | Start module 02 only (sources root `.env`) |

## Module Ports

| Module | Port | URL |
|--------|------|-----|
| 01-introduction | 8080 | http://localhost:8080 |
| 02-prompt-engineering | 8083 | http://localhost:8083 |

---

## Security Notes

- **Never commit `.env` to version control** - it contains secrets
- `.env` is already in `.gitignore`
- Use `.env.example` as a template
- For production, use proper secret management (Azure Key Vault, etc.)
- When using azd, secrets are stored securely in your azd environment

---

## Testing the Setup

Verify everything works:

```bash
# 1. Check .env exists and has credentials
cat .env

# 2. Start all apps
./start-all.sh

# 3. Test module 01 UI
curl http://localhost:8080/

# 4. Test module 02 UI
curl http://localhost:8083/

# 5. Test API calls
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Hello"}'

curl -X POST http://localhost:8083/api/gpt5/focused \
  -H "Content-Type: application/json" \
  -d '{"problem":"What is 5+5?"}'

# 6. Stop all apps
./stop-all.sh
```

---

## License

MIT License - See [LICENSE](LICENSE) file for details.