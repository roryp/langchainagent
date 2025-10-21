# 00 - Course Setup

Welcome to the **LangChain4j for Beginners** course! This module will help you set up your development environment and verify that everything is ready for the hands-on exercises.

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Required Software](#required-software)
- [Azure Account Setup](#azure-account-setup)
- [Environment Setup](#environment-setup)
- [Verification Steps](#verification-steps)
- [Troubleshooting](#troubleshooting)
- [Next Steps](#next-steps)

---

## ✅ Prerequisites

Before starting this course, you should have:

- **Basic Java Knowledge**: Understanding of Java syntax, classes, and Maven
- **REST API Familiarity**: Basic understanding of HTTP requests/responses
- **Command Line Skills**: Ability to navigate directories and run commands
- **Text Editor/IDE**: VS Code, IntelliJ IDEA, or Eclipse

**Nice to Have (Not Required):**
- Experience with Spring Boot
- Basic understanding of AI/LLMs
- Familiarity with Azure services

---

## 🛠️ Required Software

### 1. Java Development Kit (JDK)

**Required Version:** Java 21 or higher

**Check if installed:**
```bash
java -version
```

**Expected output:**
```
openjdk version "21.0.x" ...
```

**Installation:**

- **Windows**: Download from [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)
- **macOS**: 
  ```bash
  brew install openjdk@21
  ```
- **Linux (Ubuntu/Debian)**:
  ```bash
  sudo apt update
  sudo apt install openjdk-21-jdk
  ```

### 2. Apache Maven

**Required Version:** 3.9.0 or higher

**Check if installed:**
```bash
mvn -version
```

**Expected output:**
```
Apache Maven 3.9.x ...
Java version: 21.0.x ...
```

**Installation:**

- **Windows**: Download from [Maven website](https://maven.apache.org/download.cgi) and add to PATH
- **macOS**:
  ```bash
  brew install maven
  ```
- **Linux (Ubuntu/Debian)**:
  ```bash
  sudo apt update
  sudo apt install maven
  ```

### 3. Azure CLI

**Required for:** Deploying applications to Azure

**Check if installed:**
```bash
az --version
```

**Installation:**

- **Windows**: Download from [Azure CLI website](https://aka.ms/installazurecliwindows)
- **macOS**:
  ```bash
  brew install azure-cli
  ```
- **Linux**:
  ```bash
  curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash
  ```

### 4. Azure Developer CLI (azd)

**Required for:** Infrastructure deployment

**Check if installed:**
```bash
azd version
```

**Installation:**

- **Windows (PowerShell)**:
  ```powershell
  powershell -ex AllSigned -c "Invoke-RestMethod 'https://aka.ms/install-azd.ps1' | Invoke-Expression"
  ```
- **macOS/Linux**:
  ```bash
  curl -fsSL https://aka.ms/install-azd.sh | bash
  ```

### 5. Git

**Check if installed:**
```bash
git --version
```

**Installation:**

- **Windows**: Download from [Git website](https://git-scm.com/download/win)
- **macOS**: `brew install git`
- **Linux**: `sudo apt install git`

### 6. Code Editor (Choose One)

**Recommended: Visual Studio Code**
- Download from [code.visualstudio.com](https://code.visualstudio.com/)
- Install extensions:
  - Extension Pack for Java
  - Spring Boot Extension Pack
  - Azure Tools

**Alternatives:**
- IntelliJ IDEA (Community or Ultimate)
- Eclipse IDE for Java

### 7. Docker (Optional)

**Required for:** Local containerization testing

**Check if installed:**
```bash
docker --version
```

**Installation:**
- Download Docker Desktop from [docker.com](https://www.docker.com/products/docker-desktop)

---

## ☁️ Azure Account Setup

### 1. Create Azure Account

If you don't have an Azure account:

1. Go to [azure.microsoft.com/free](https://azure.microsoft.com/free)
2. Click **Start free**
3. Sign in with Microsoft account or create one
4. Complete the verification process

**Free Tier Includes:**
- $200 credit for 30 days
- 12 months of free services
- Always-free services

### 2. Create Azure OpenAI Resource

**Steps:**

1. **Login to Azure Portal**:
   ```bash
   az login
   ```

2. **Apply for Azure OpenAI Access** (if first time):
   - Go to [aka.ms/oai/access](https://aka.ms/oai/access)
   - Fill out the application form
   - Approval typically takes 1-2 business days

3. **Create Azure OpenAI Service**:
   - Go to [Azure Portal](https://portal.azure.com)
   - Search for "Azure OpenAI"
   - Click **Create**
   - Fill in details:
     - **Subscription**: Your subscription
     - **Resource Group**: Create new (e.g., `rg-langchain4j-course`)
     - **Region**: East US 2 (recommended)
     - **Name**: Unique name (e.g., `openai-langchain4j-course`)
     - **Pricing Tier**: Standard S0

4. **Deploy Required Models**:
   
   After resource is created, deploy these models:
   
   **Model 1: GPT-4o-mini (for chat)**
   - Go to your Azure OpenAI resource
   - Click **Model deployments** → **Create new deployment**
   - Select model: `gpt-4o-mini`
   - Deployment name: `gpt-4o-mini`
   - Click **Create**
   
   **Model 2: text-embedding-3-small (for embeddings)**
   - Click **Create new deployment** again
   - Select model: `text-embedding-3-small`
   - Deployment name: `text-embedding-3-small`
   - Click **Create**

5. **Get Credentials**:
   
   You'll need these for the course:
   
   - **Endpoint**: Go to **Keys and Endpoint** section
     - Example: `https://your-resource.openai.azure.com/`
   
   - **API Key**: Copy **KEY 1** (keep secure!)

---

## 🔧 Environment Setup

### 1. Clone the Repository

```bash
git clone https://github.com/roryp/langchainagent.git
cd langchainagent
```

### 2. Set Environment Variables

**Windows (PowerShell):**
```powershell
$env:AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"
$env:AZURE_OPENAI_API_KEY="your-api-key-here"
$env:AZURE_OPENAI_DEPLOYMENT="gpt-4o-mini"
$env:AZURE_OPENAI_EMBEDDING_DEPLOYMENT="text-embedding-3-small"
```

**Windows (Command Prompt):**
```cmd
set AZURE_OPENAI_ENDPOINT=https://your-resource.openai.azure.com/
set AZURE_OPENAI_API_KEY=your-api-key-here
set AZURE_OPENAI_DEPLOYMENT=gpt-4o-mini
set AZURE_OPENAI_EMBEDDING_DEPLOYMENT=text-embedding-3-small
```

**macOS/Linux (Bash):**
```bash
export AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"
export AZURE_OPENAI_API_KEY="your-api-key-here"
export AZURE_OPENAI_DEPLOYMENT="gpt-4o-mini"
export AZURE_OPENAI_EMBEDDING_DEPLOYMENT="text-embedding-3-small"
```

**Persistent Setup (Recommended):**

Create a `.env` file or add to your shell profile:

```bash
# .env file (load with direnv or manually source)
AZURE_OPENAI_ENDPOINT=https://your-resource.openai.azure.com/
AZURE_OPENAI_API_KEY=your-api-key-here
AZURE_OPENAI_DEPLOYMENT=gpt-4o-mini
AZURE_OPENAI_EMBEDDING_DEPLOYMENT=text-embedding-3-small
```

### 3. Build the Project

```bash
# From the root directory
mvn clean install
```

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

---

## ✅ Verification Steps

Let's verify everything is set up correctly!

### Step 1: Verify Java and Maven

```bash
java -version
mvn -version
```

✅ **Success**: Both commands show version 21+ for Java and 3.9+ for Maven

### Step 2: Verify Azure CLI

```bash
az --version
azd version
az account show
```

✅ **Success**: All commands execute without errors and show your Azure account

### Step 3: Verify Build

```bash
mvn clean compile
```

✅ **Success**: Build completes with `BUILD SUCCESS`

### Step 4: Run Quick Test

Let's test the simplest module:

```bash
cd 01-introduction
mvn spring-boot:run
```

In another terminal:

```bash
curl -X POST http://localhost:8080/api/chat \
  -H 'Content-Type: application/json' \
  -d '{"message":"Hello! Are you working?"}'
```

✅ **Success**: You receive an AI-generated response

Press `Ctrl+C` to stop the application.

### Step 5: Verify Azure OpenAI Access

Create a test file `verify-openai.sh`:

```bash
#!/bin/bash

echo "Testing Azure OpenAI connection..."

RESPONSE=$(curl -s -X POST "$AZURE_OPENAI_ENDPOINT/openai/deployments/$AZURE_OPENAI_DEPLOYMENT/chat/completions?api-version=2024-02-15-preview" \
  -H "Content-Type: application/json" \
  -H "api-key: $AZURE_OPENAI_API_KEY" \
  -d '{
    "messages": [{"role": "user", "content": "Say hello"}],
    "max_tokens": 10
  }')

if echo "$RESPONSE" | grep -q "choices"; then
  echo "✅ Azure OpenAI is working!"
  echo "$RESPONSE" | jq -r '.choices[0].message.content'
else
  echo "❌ Error connecting to Azure OpenAI"
  echo "$RESPONSE"
  exit 1
fi
```

Run it:
```bash
bash verify-openai.sh
```

✅ **Success**: You see "✅ Azure OpenAI is working!" and a response

---

## 🐛 Troubleshooting

### Issue: `mvn: command not found`

**Solution:**
- Ensure Maven is installed
- Add Maven's `bin` directory to your PATH
- Restart your terminal

### Issue: `Java version mismatch`

**Solution:**
```bash
# Check all Java versions
java -version
javac -version

# Set JAVA_HOME to Java 21
export JAVA_HOME=/path/to/java21
export PATH=$JAVA_HOME/bin:$PATH
```

### Issue: `Azure OpenAI 401 Unauthorized`

**Solution:**
- Verify your API key is correct
- Check endpoint URL format (should end with `/`)
- Ensure deployment names match exactly

### Issue: `Azure OpenAI 403 Forbidden`

**Solution:**
- Your Azure OpenAI access request may still be pending
- Check [aka.ms/oai/access](https://aka.ms/oai/access) for status
- Try a different Azure region

### Issue: `Port 8080 already in use`

**Solution:**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# macOS/Linux
lsof -ti:8080 | xargs kill -9
```

### Issue: Build fails with dependency errors

**Solution:**
```bash
# Clear Maven cache
mvn dependency:purge-local-repository

# Rebuild
mvn clean install -U
```

---

## 📚 Additional Resources

### Documentation
- [LangChain4j Documentation](https://docs.langchain4j.dev/)
- [Azure OpenAI Documentation](https://learn.microsoft.com/azure/ai-services/openai/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

### Course Materials
- [Main README](../README.md) - Course overview
- [GLOSSARY](../docs/GLOSSARY.md) - Key terms and concepts
- [ARCHITECTURE](../docs/ARCHITECTURE.md) - System architecture overview

### Community
- [LangChain4j Discord](https://discord.gg/langchain4j)
- [GitHub Discussions](https://github.com/roryp/langchainagent/discussions)

---

## 🎯 Next Steps

Congratulations! 🎉 Your environment is ready.

**Proceed to:** [Module 01 - Introduction to LangChain4j](../01-introduction/README.md)

In the next module, you'll:
- Create your first chat application
- Learn about stateless vs stateful conversations
- Build an AI assistant with memory

---

## 📝 Checklist

Before moving to Module 01, ensure:

- [ ] Java 21+ installed and verified
- [ ] Maven 3.9+ installed and verified
- [ ] Azure CLI and azd CLI installed
- [ ] Azure OpenAI resource created
- [ ] GPT-4o-mini model deployed
- [ ] text-embedding-3-small model deployed
- [ ] Environment variables set
- [ ] Project builds successfully (`mvn clean install`)
- [ ] Quick test application runs
- [ ] Azure OpenAI connection verified

If all boxes are checked, you're ready to start! ✅

---

## ❓ Need Help?

- **Course Issues**: Open an issue on [GitHub](https://github.com/roryp/langchainagent/issues)
- **Azure OpenAI Issues**: Check [Azure OpenAI Troubleshooting](../docs/TROUBLESHOOTING.md)
- **General Questions**: Join our [Discord community](https://discord.gg/langchain4j)

---

**Previous:** [Course Overview](../README.md) | **Next:** [01 - Introduction](../01-introduction/README.md)
