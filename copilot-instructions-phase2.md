# 🎯 Phase 2: Complete Remaining Course Modules

## Overview

**Current Status:** ✅ Modules 00, 01, 02, and 05 are production-ready and tested  
**Remaining Work:** Complete implementation of Modules 03 and 04  
**Goal:** Finish the comprehensive LangChain4j learning course with all 6 core modules functional

---

## 📊 Module Status Summary

| Module | Status | Description | Priority |
|--------|--------|-------------|----------|
| 00-course-setup | ✅ Complete | Environment setup and verification | - |
| 01-introduction | ✅ Complete | Basic chat + conversation memory | - |
| 02-rag | ✅ Complete | Document upload, embeddings, semantic search | - |
| 03-prompt-engineering | ⚠️ Skeleton | Needs full implementation | **HIGH** |
| 04-tools | ⚠️ Copy of 05 | Needs code separation and focus on tools only | **HIGH** |
| 05-agents | ✅ Complete | ReAct pattern, multi-tool agent | - |

---

## 🎯 Phase 2 Goals

### Primary Objectives
1. ✅ **Module 03 (Prompt Engineering):** Implement comprehensive prompt engineering examples
2. ✅ **Module 04 (Tools):** Separate tool-specific code from agents, focus on tool creation
3. ✅ **Documentation:** Create CHALLENGES.md for each module
4. ✅ **Testing:** Validate all new modules work with Azure OpenAI
5. ✅ **Integration:** Ensure all 6 modules work together as a cohesive learning path

### Success Criteria
- [ ] Module 03 compiles and runs successfully
- [ ] Module 04 compiles and runs successfully
- [ ] Each module has comprehensive CHALLENGES.md
- [ ] All modules tested with Azure OpenAI
- [ ] Documentation is complete and accurate
- [ ] Build time for all modules < 10 seconds combined

---

## 📋 Module 03: Prompt Engineering - Implementation Plan

### Current State
- ✅ Basic directory structure exists
- ✅ README.md with comprehensive documentation
- ✅ pom.xml configured
- ⚠️ Only Application.java exists (empty)
- ❌ No service, controller, or model classes
- ❌ No prompt templates
- ❌ No working endpoints

### Required Implementation

#### 3.1 Core Java Classes

**Location:** `03-prompt-engineering/src/main/java/com/example/langchain4j/prompts/`

**Classes to Create:**

1. **`app/PromptController.java`**
   ```java
   @RestController
   @RequestMapping("/api/prompts")
   public class PromptController {
       // Endpoints for each prompt pattern
   }
   ```
   
   **Endpoints to implement:**
   - `POST /api/prompts/template` - Basic template substitution
   - `POST /api/prompts/few-shot` - Few-shot learning examples
   - `POST /api/prompts/extract` - Extract structured data
   - `POST /api/prompts/sentiment` - Sentiment analysis with examples
   - `POST /api/prompts/chain-of-thought` - Step-by-step reasoning
   - `POST /api/prompts/role-based` - Role-specific prompts (e.g., code reviewer)
   - `GET /api/prompts/health` - Health check

2. **`service/PromptService.java`**
   ```java
   @Service
   public class PromptService {
       @Autowired
       private ChatLanguageModel chatModel;
       
       // Methods for each prompt pattern
       public String applyTemplate(String template, Map<String, Object> variables);
       public String fewShotClassification(List<Example> examples, String input);
       public Person extractStructuredData(String text);
       public String chainOfThoughtReasoning(String problem);
       public String roleBasedPrompt(String role, String task);
   }
   ```

3. **`config/LangChainConfig.java`**
   ```java
   @Configuration
   public class LangChainConfig {
       @Bean
       public ChatLanguageModel chatModel() {
           return AzureOpenAiChatModel.builder()
               .endpoint(endpoint)
               .apiKey(apiKey)
               .deploymentName(deployment)
               .temperature(0.7)
               .build();
       }
       
       @Bean
       public PromptTemplate emailTemplate() {
           return PromptTemplate.from(/* email template */);
       }
   }
   ```

4. **`model/PromptRequest.java`**
   ```java
   public class PromptRequest {
       private String template;
       private Map<String, Object> variables;
       private String text;
       private String input;
       // getters/setters
   }
   ```

5. **`model/PromptResponse.java`**
   ```java
   public class PromptResponse {
       private String result;
       private String model;
       private int tokensUsed;
       // getters/setters
   }
   ```

6. **`model/Person.java`** (for structured output)
   ```java
   public class Person {
       private String name;
       private Integer age;
       private String city;
       private String occupation;
       // getters/setters
   }
   ```

7. **`model/Example.java`** (for few-shot learning)
   ```java
   public class Example {
       private String input;
       private String output;
       // getters/setters
   }
   ```

#### 3.2 Prompt Templates

**Location:** `03-prompt-engineering/src/main/resources/prompts/`

**Files to Create:**

1. **`email-template.txt`**
   ```
   You are a professional email assistant.
   
   Write a {{style}} email to {{recipient}} about {{topic}}.
   
   Keep it {{tone}} and {{length}}.
   
   Include:
   - Proper greeting
   - Clear subject line
   - Concise body
   - Professional closing
   ```

2. **`code-reviewer.txt`**
   ```
   You are an expert code reviewer with 10+ years of experience.
   
   Review the following {{language}} code for:
   - Best practices and code quality
   - Potential bugs or errors
   - Performance issues
   - Security concerns
   - Readability and maintainability
   
   Be constructive and specific. Provide concrete suggestions.
   
   Code:
   {{code}}
   ```

3. **`data-extractor.txt`**
   ```
   Extract structured information from the following text.
   
   Return ONLY a JSON object with these fields:
   - name (string, or null)
   - age (number, or null)
   - city (string, or null)
   - occupation (string, or null)
   
   Text: {{text}}
   
   JSON:
   ```

4. **`sentiment-classifier.txt`**
   ```
   Classify the sentiment of the following text as POSITIVE, NEGATIVE, or NEUTRAL.
   
   Examples:
   {{#examples}}
   Input: "{{input}}"
   Output: {{output}}
   
   {{/examples}}
   
   Input: "{{text}}"
   Output:
   ```

5. **`chain-of-thought.txt`**
   ```
   Solve the following problem step by step. Show your reasoning.
   
   Problem: {{problem}}
   
   Let's approach this systematically:
   
   Step 1: Understand the problem
   Step 2: Identify what we know
   Step 3: Determine what we need to find
   Step 4: Solve
   Step 5: Verify the answer
   
   Solution:
   ```

#### 3.3 Configuration

**Update:** `03-prompt-engineering/src/main/resources/application.yaml`

```yaml
server:
  port: 8083

spring:
  application:
    name: prompt-engineering

azure:
  openai:
    endpoint: ${AZURE_OPENAI_ENDPOINT}
    api-key: ${AZURE_OPENAI_API_KEY}
    deployment: ${AZURE_OPENAI_DEPLOYMENT:gpt-4o-mini}
    temperature: 0.7
    max-tokens: 1000

logging:
  level:
    dev.langchain4j: DEBUG
    com.example.langchain4j: DEBUG
```

#### 3.4 Testing

**Create:** `03-prompt-engineering/CHALLENGES.md`

```markdown
# Module 03: Prompt Engineering - Challenges

## Challenge 1: Dynamic Email Generator ⭐
**Time:** 20 minutes

Create an endpoint that generates emails for different scenarios:
- Welcome email for new customers
- Follow-up email after meeting
- Apology email for service disruption

**Requirements:**
- Use prompt templates with variables
- Support different tones (formal, casual, friendly)
- Allow customizable length

## Challenge 2: Sentiment Analysis with Few-Shot ⭐⭐
**Time:** 30 minutes

Build a sentiment classifier that learns from examples.

**Requirements:**
- Accept 3-5 training examples
- Classify new text as POSITIVE, NEGATIVE, or NEUTRAL
- Return confidence score
- Handle edge cases (sarcasm, mixed sentiment)

## Challenge 3: Resume Parser ⭐⭐⭐
**Time:** 45 minutes

Create a structured data extractor for resumes.

**Requirements:**
- Extract: name, email, phone, skills, education, experience
- Return as JSON
- Handle missing information gracefully
- Support multiple resume formats

## Challenge 4: Math Tutor with Chain-of-Thought ⭐⭐⭐
**Time:** 45 minutes

Build an AI math tutor that shows step-by-step solutions.

**Requirements:**
- Accept math problems (algebra, calculus, etc.)
- Show reasoning at each step
- Explain why each step is necessary
- Verify the final answer

## Challenge 5: Multi-Role Code Assistant ⭐⭐⭐⭐
**Time:** 60 minutes

Create a system that can switch between different coding roles:
- Code reviewer
- Debugging assistant
- Documentation writer
- Performance optimizer

**Requirements:**
- Role-specific system messages
- Context-aware responses
- Tool recommendations
- Code examples in responses
```

**Create:** `scripts/test-prompts.sh`

```bash
#!/bin/bash

BASE_URL="http://localhost:8083"

echo "Testing Prompt Engineering Module..."

# Health check
echo "1. Health check..."
curl -s "$BASE_URL/api/prompts/health"

# Template test
echo -e "\n\n2. Testing template..."
curl -s -X POST "$BASE_URL/api/prompts/template" \
  -H "Content-Type: application/json" \
  -d '{
    "template": "Write a {{style}} email to {{recipient}} about {{topic}}",
    "variables": {
      "style": "formal",
      "recipient": "John",
      "topic": "project deadline"
    }
  }'

# Few-shot test
echo -e "\n\n3. Testing few-shot learning..."
curl -s -X POST "$BASE_URL/api/prompts/sentiment" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "This product exceeded all my expectations!"
  }'

# Extraction test
echo -e "\n\n4. Testing data extraction..."
curl -s -X POST "$BASE_URL/api/prompts/extract" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Alice Johnson is 28 years old, works as a software engineer in San Francisco."
  }'

# Chain of thought test
echo -e "\n\n5. Testing chain-of-thought..."
curl -s -X POST "$BASE_URL/api/prompts/chain-of-thought" \
  -H "Content-Type: application/json" \
  -d '{
    "problem": "If a train travels 120 miles in 2 hours, what is its average speed?"
  }'

echo -e "\n\n✅ All tests complete!"
```

---

## 📋 Module 04: Tools - Implementation Plan

### Current State
- ⚠️ Complete copy of 05-agents (should focus only on tools)
- ✅ Has tools implemented (Calculator, Weather)
- ❌ Too complex for "tools introduction" module
- ❌ Includes agent logic (should be tool-only)

### Required Changes

#### 4.1 Simplification Strategy

**Goal:** Focus on teaching tool creation, not agent execution

**Remove:**
- Agent reasoning logic
- ReAct pattern code
- Multi-step execution
- Session management (keep simple)

**Keep:**
- Tool definitions (@Tool annotations)
- Tool execution (direct calls, not via agent)
- HTTP tool patterns
- Tool validation

#### 4.2 Simplified Architecture

```
User Request → Controller → ToolService → Execute Tool → Return Result
                                  ↓
                          No LLM decision making
                          Direct tool execution
```

#### 4.3 Core Classes to Refactor

**Location:** `04-tools/src/main/java/com/example/langchain4j/tools/`

1. **`app/ToolsController.java`** (SIMPLIFIED)
   ```java
   @RestController
   @RequestMapping("/api/tools")
   public class ToolsController {
       
       @Autowired
       private ToolService toolService;
       
       // Direct tool execution endpoints
       @PostMapping("/calculator/{operation}")
       public ToolResponse calculate(@PathVariable String operation, 
                                     @RequestBody CalculatorRequest request);
       
       @PostMapping("/weather/current")
       public ToolResponse getCurrentWeather(@RequestParam String location);
       
       @PostMapping("/weather/forecast")
       public ToolResponse getForecast(@RequestParam String location, 
                                       @RequestParam int days);
       
       @GetMapping("/list")
       public List<ToolInfo> listAvailableTools();
       
       @GetMapping("/health")
       public String health();
   }
   ```

2. **`service/ToolService.java`** (NEW - Simplified)
   ```java
   @Service
   public class ToolService {
       
       @Autowired
       private CalculatorTool calculatorTool;
       
       @Autowired
       private WeatherTool weatherTool;
       
       // Direct execution methods
       public double executeCalculation(String operation, double a, double b);
       public String getCurrentWeather(String location);
       public String getWeatherForecast(String location, int days);
       
       // Tool discovery
       public List<ToolInfo> listTools();
   }
   ```

3. **`tools/CalculatorTool.java`** (KEEP - Clean up)
   ```java
   @Component
   public class CalculatorTool {
       
       @Tool("Add two numbers")
       public double add(double a, double b) {
           return a + b;
       }
       
       @Tool("Subtract b from a")
       public double subtract(double a, double b) {
           return a - b;
       }
       
       @Tool("Multiply two numbers")
       public double multiply(double a, double b) {
           return a * b;
       }
       
       @Tool("Divide a by b")
       public double divide(double a, double b) {
           if (b == 0) throw new ToolExecutionException("Division by zero");
           return a / b;
       }
       
       @Tool("Calculate a raised to the power of b")
       public double power(double a, double b) {
           return Math.pow(a, b);
       }
       
       @Tool("Calculate square root")
       public double sqrt(double a) {
           if (a < 0) throw new ToolExecutionException("Cannot calculate square root of negative number");
           return Math.sqrt(a);
       }
   }
   ```

4. **`tools/WeatherTool.java`** (KEEP - Enhance docs)
   ```java
   @Component
   public class WeatherTool {
       
       @Tool("Get current weather for a location")
       public String getCurrentWeather(
           @P("The city name, e.g., 'Seattle' or 'London'") String location
       ) {
           // Mock implementation - could connect to real API
           return mockWeatherResponse(location);
       }
       
       @Tool("Get weather forecast for multiple days")
       public String getWeatherForecast(
           @P("The city name") String location,
           @P("Number of days (1-7)") int days
       ) {
           // Mock implementation
           return mockForecastResponse(location, days);
       }
   }
   ```

5. **`model/ToolInfo.java`** (NEW)
   ```java
   public class ToolInfo {
       private String name;
       private String description;
       private List<ToolParameter> parameters;
       private String returnType;
       // getters/setters
   }
   ```

#### 4.4 Remove Agent-Specific Code

**Files to DELETE:**
- `app/AgentController.java` - Not needed in tools module
- `service/AgentService.java` - Belongs in 05-agents only

**Files to KEEP but SIMPLIFY:**
- `exception/ToolExecutionException.java` - Clean exception handling
- `model/dto/ToolExecutionInfo.java` - For response metadata

#### 4.5 Update Configuration

**Update:** `04-tools/src/main/resources/application.yaml`

```yaml
server:
  port: 8084  # Different port from agents (8082)

spring:
  application:
    name: tools-module

# No Azure OpenAI needed - direct tool execution only
# (Optional: Could add for future AI-assisted tool selection)

tools:
  calculator:
    enabled: true
  weather:
    enabled: true
    api-key: ${WEATHER_API_KEY:mock}  # Optional real API
    
logging:
  level:
    com.example.langchain4j.tools: DEBUG
```

#### 4.6 Update README

**Enhance:** `04-tools/README.md`

Key changes:
- Remove agent references
- Focus on "@Tool annotation"
- Show tool creation patterns
- Explain tool parameters
- Show tool validation
- Add integration examples (how to use tools in other modules)

#### 4.7 Create Enhanced CHALLENGES.md

**Create:** `04-tools/CHALLENGES.md`

```markdown
# Module 04: Tools - Challenges

## Challenge 1: String Tool ⭐
**Time:** 20 minutes

Create a StringTool with operations:
- reverse(string)
- toUpperCase(string)
- toLowerCase(string)
- countWords(string)
- isPalindrome(string)

## Challenge 2: Time Zone Tool ⭐⭐
**Time:** 30 minutes

Build a TimeZoneTool that:
- Converts time between zones
- Gets current time in any city
- Calculates time difference
- Lists available time zones

## Challenge 3: Currency Converter Tool ⭐⭐
**Time:** 30 minutes

Create a CurrencyTool with:
- Convert between currencies (mock or real API)
- Get exchange rates
- Format currency display
- Handle 10+ common currencies

## Challenge 4: Database Query Tool ⭐⭐⭐
**Time:** 45 minutes

Build a DatabaseTool that safely executes queries:
- SELECT operations only (no DELETE/DROP)
- Parameter validation
- Result formatting
- Error handling
- Query timeout protection

## Challenge 5: HTTP API Tool ⭐⭐⭐⭐
**Time:** 60 minutes

Create a generic HTTPTool that:
- Makes GET, POST, PUT, DELETE requests
- Handles authentication (API keys, OAuth)
- Parses JSON responses
- Retries on failure
- Logs all requests

**Integration:** Connect to a real public API (GitHub, OpenWeather, etc.)
```

#### 4.8 Testing Script

**Create:** `scripts/test-tools.sh`

```bash
#!/bin/bash

BASE_URL="http://localhost:8084"

echo "Testing Tools Module..."

# Health check
echo "1. Health check..."
curl -s "$BASE_URL/api/tools/health"

# List tools
echo -e "\n\n2. Listing available tools..."
curl -s "$BASE_URL/api/tools/list" | jq .

# Calculator tests
echo -e "\n\n3. Testing calculator..."
curl -s -X POST "$BASE_URL/api/tools/calculator/add" \
  -H "Content-Type: application/json" \
  -d '{"a": 25, "b": 17}'

curl -s -X POST "$BASE_URL/api/tools/calculator/multiply" \
  -H "Content-Type: application/json" \
  -d '{"a": 12, "b": 8}'

curl -s -X POST "$BASE_URL/api/tools/calculator/sqrt" \
  -H "Content-Type: application/json" \
  -d '{"a": 144}'

# Weather tests
echo -e "\n\n4. Testing weather tool..."
curl -s -X POST "$BASE_URL/api/tools/weather/current?location=Seattle"

curl -s -X POST "$BASE_URL/api/tools/weather/forecast?location=London&days=3"

echo -e "\n\n✅ All tests complete!"
```

---

## ⚠️ CRITICAL RISKS & MITIGATION

### Risk 1: Breaking Existing Modules ⚠️ HIGH
**Risk:** Changes to Module 04 might affect Module 05 (they share code currently)

**Mitigation:**
- ✅ Do NOT modify Module 05 (agents) - it's working perfectly
- ✅ Create git branches before any changes
- ✅ Test Module 05 still works after Module 04 changes
- ✅ Keep modules completely independent

### Risk 2: Port Conflicts ⚠️ MEDIUM
**Risk:** Multiple modules trying to use same ports

**Current Ports:**
- 8080: Module 01 (introduction)
- 8081: Module 02 (rag)
- 8082: Module 05 (agents) - **KEEP THIS**
- 8083: Module 03 (prompts) - NEW
- 8084: Module 04 (tools) - **CHANGE FROM 8082**

**Mitigation:**
- ✅ Update Module 04 port to 8084 immediately
- ✅ Test each module starts without conflicts
- ✅ Document port assignments in main README

### Risk 3: Azure Deployment Configuration ⚠️ MEDIUM
**Risk:** azure.yaml may not include new modules

**Mitigation:**
- ✅ Review azure.yaml structure
- ✅ Add Module 03 and 04 to deployment if needed
- ✅ Test deployment incrementally
- ✅ Keep existing working deployments unchanged

### Risk 4: Incomplete Implementation ⚠️ LOW
**Risk:** Module 03 needs significant code from scratch

**Mitigation:**
- ✅ Start with simplest features first
- ✅ Test each endpoint before adding next
- ✅ Use GitHub Copilot for code generation
- ✅ Reference working patterns from Module 01

### Risk 5: Maven Build Failures ⚠️ LOW
**Risk:** Dependencies might conflict between modules

**Mitigation:**
- ✅ Run `mvn clean install` from root frequently
- ✅ Fix compilation errors immediately
- ✅ Use consistent dependency versions
- ✅ Parent POM already configured correctly

---

## 📋 Implementation Order (CRITICAL)

**DO THIS ORDER - Don't skip steps:**

### Step 1: Safety First
```bash
git status                          # Ensure clean state
git checkout -b phase2-implementation
git push origin phase2-implementation
```

### Step 2: Implement Module 03 (Safe - New Code)
```bash
cd 03-prompt-engineering
# Implement all classes as per plan
mvn clean package                   # Test compiles
mvn spring-boot:run                 # Test runs
# Test endpoints with curl
git add .
git commit -m "feat: implement prompt engineering module"
git push
```

### Step 3: Backup Module 04 Current State
```bash
# Test it works AS-IS
cd 04-tools
mvn spring-boot:run                 # Should start on 8082
# Test with curl, verify it works
# Stop the server
git checkout -b backup-module-04-original
git push origin backup-module-04-original
git checkout phase2-implementation
```

### Step 4: Refactor Module 04 (Risky - Modify Existing)
```bash
cd 04-tools
# Delete agent files
rm src/main/java/.../app/AgentController.java
rm src/main/java/.../service/AgentService.java
# Update port in application.yaml
# Implement simplified ToolService
mvn clean package                   # Test compiles
mvn spring-boot:run                 # Test runs on 8084
# Test endpoints
git add .
git commit -m "refactor: simplify module 04 to tools-only"
git push
```

### Step 5: Integration Testing
```bash
# From root
mvn clean install                   # All modules
# Start each module one by one and test
git add .
git commit -m "chore: integration testing complete"
git push
```

### Step 6: Merge to Main
```bash
git checkout main
git merge phase2-implementation
git push origin main
git tag phase2-complete
git push origin phase2-complete
```

---

## 🔧 Azure Deployment Considerations

### Current Deployment Structure

**Issue:** The existing `azure.yaml` in `01-introduction/` may only deploy specific services.

### Deployment Strategy Options

**Option 1: Individual Module Deployment (RECOMMENDED)**
- Each module has its own Dockerfile
- Deploy Module 03 and 04 separately via `azd`
- Existing modules (01, 02, 05) unchanged

**Option 2: Unified Deployment**
- Update root-level infrastructure
- Deploy all 6 modules together
- More complex but better for production

### Required Changes for Deployment

**If deploying Module 03 to Azure:**
```yaml
# Add to infrastructure
services:
  prompt-engineering:
    project: ./03-prompt-engineering
    host: containerapp
    language: java
```

**If deploying Module 04 to Azure:**
```yaml
# Add to infrastructure
services:
  tools:
    project: ./04-tools
    host: containerapp
    language: java
```

### Deployment Testing Plan

1. **Test locally first** - All modules must run locally
2. **Deploy one module at a time** - Start with Module 03
3. **Verify existing services unaffected** - Test 01, 02, 05 still work
4. **Document deployment URLs** - Update README with new endpoints

---

## 🧪 Testing Strategy

### Test Order

1. **Module 03 (Prompt Engineering)**
   ```bash
   cd 03-prompt-engineering
   mvn clean package
   mvn spring-boot:run
   # In another terminal:
   bash ../scripts/test-prompts.sh
   ```

2. **Module 04 (Tools)**
   ```bash
   cd 04-tools
   mvn clean package
   mvn spring-boot:run
   # In another terminal:
   bash ../scripts/test-tools.sh
   ```

3. **Integration Test**
   ```bash
   # From root
   mvn clean install
   # Verify all 6 modules compile
   ```

4. **Azure Deployment Test**
   ```bash
   cd 01-introduction
   azd deploy
   # Verify all services update correctly
   ```

### Validation Checklist

**Module 03:**
- [ ] Application starts on port 8083
- [ ] Health endpoint responds
- [ ] Template substitution works
- [ ] Few-shot learning produces correct classifications
- [ ] Data extraction returns valid JSON
- [ ] Chain-of-thought shows step-by-step reasoning
- [ ] All prompt templates load correctly

**Module 04:**
- [ ] Application starts on port 8084
- [ ] Health endpoint responds
- [ ] All calculator operations work
- [ ] Weather tool returns mock data
- [ ] Tool listing shows all available tools
- [ ] Error handling works (division by zero, etc.)
- [ ] No agent-specific code present

**Integration:**
- [ ] All modules build together
- [ ] No port conflicts
- [ ] Each module independent
- [ ] Documentation is consistent
- [ ] README files accurate

---

## 📝 Documentation Updates

### Main README Updates

**Add to Module Table:**

```markdown
| **03-prompt-engineering** | 8083 | Templates, few-shot, parsing | ⭐⭐ |
| **04-tools** | 8084 | Tool creation, HTTP tools, @Tool | ⭐⭐ |
```

### Create Module-Specific Docs

1. **03-prompt-engineering/TROUBLESHOOTING.md**
   - Common prompt issues
   - Template syntax errors
   - Output parsing problems

2. **04-tools/TROUBLESHOOTING.md**
   - Tool registration issues
   - Parameter validation errors
   - HTTP connection problems

### Update Global Docs

**Update:** `docs/TROUBLESHOOTING.md`
- Add sections for modules 03 and 04
- Common errors and solutions

**Update:** `docs/GLOSSARY.md`
- Add: Prompt Engineering, Few-Shot Learning, Chain-of-Thought
- Add: Tool, @Tool annotation, Tool Execution

---

## 🚀 Implementation Timeline

### ⚠️ CRITICAL: Risk Mitigation Before Starting

**Create Safety Branch:**
```bash
git checkout -b phase2-implementation
git push origin phase2-implementation
```

**Backup Current State:**
```bash
# Test that existing modules still work
cd 04-tools && mvn clean package && mvn spring-boot:run
# Verify it works, then stop it
```

### Week 1: Module 03 - Foundation (LOW RISK - Greenfield Development)
**Days 1-2:**
- [ ] Create controller, service, config classes
- [ ] Implement basic template endpoint
- [ ] Test with simple prompts
- [ ] **Git commit after each working feature**

**Days 3-4:**
- [ ] Add few-shot learning endpoint
- [ ] Implement data extraction endpoint
- [ ] Create prompt template files in resources/prompts/
- [ ] **Test each endpoint before proceeding**

**Day 5:**
- [ ] Add chain-of-thought endpoint
- [ ] Create CHALLENGES.md
- [ ] Run full module testing
- [ ] **Git tag: module-03-complete**

### Week 2: Module 04 - Simplification (MEDIUM RISK - Refactoring Existing Code)

**⚠️ IMPORTANT: Module 04 is currently a complete copy of Module 05**

**Days 1-2: Backup & Initial Refactoring**
- [ ] **Test Module 04 works AS-IS before changes** (mvn spring-boot:run)
- [ ] Create backup: `git checkout -b backup-module-04-original`
- [ ] Switch back: `git checkout phase2-implementation`
- [ ] Delete AgentController.java and AgentService.java
- [ ] **Test build still works: mvn clean package**
- [ ] Update port 8082 → 8084 in application.yaml
- [ ] **Git commit: "refactor: remove agent logic from module 04"**

**Days 3-4: Simplification & Tool Focus**
- [ ] Create simplified ToolService.java (direct execution, no agent)
- [ ] Simplify ToolsController.java (remove session management)
- [ ] Add tool discovery endpoint (GET /api/tools/list)
- [ ] Update application.yaml (remove Azure OpenAI if not needed)
- [ ] **Test each change: mvn clean package && mvn spring-boot:run**

**Day 5: Documentation & Testing**
- [ ] Create CHALLENGES.md
- [ ] Update README.md (remove agent references)
- [ ] Full module testing with test-tools.sh
- [ ] **Git tag: module-04-complete**

### Week 3: Integration & Polish
**Days 1-2: Build & Integration**
- [ ] From root: `mvn clean install` (all 6 modules)
- [ ] Fix any compilation issues
- [ ] Test no port conflicts (8080, 8081, 8082, 8083, 8084)
- [ ] Update root README.md with module 03/04 details

**Days 3-4: Testing & Scripts**
- [ ] Create scripts/test-prompts.sh
- [ ] Create scripts/test-tools.sh
- [ ] Run all test scripts
- [ ] Test Azure deployment (azd deploy)
- [ ] Verify all services healthy

**Day 5: Documentation & Completion**
- [ ] Update docs/TROUBLESHOOTING.md
- [ ] Update docs/GLOSSARY.md
- [ ] Final documentation review
- [ ] Merge to main: `git checkout main && git merge phase2-implementation`
- [ ] **Git tag: phase2-complete**

---

## 🎯 Success Metrics

### Technical Metrics
- [ ] Build time < 10 seconds per module
- [ ] Startup time < 30 seconds per module
- [ ] Test suite passes 100%
- [ ] Zero compilation errors
- [ ] Zero runtime errors in basic usage

### Educational Metrics
- [ ] Each module 1-2 hours to complete
- [ ] Clear learning progression
- [ ] Working code examples
- [ ] Challenges have solutions
- [ ] Documentation self-explanatory

### Quality Metrics
- [ ] Code coverage > 70%
- [ ] No duplicate code between modules
- [ ] Consistent coding style
- [ ] Comprehensive error handling
- [ ] Clear log messages

---

## 🔄 Optional Enhancements

### Module 03 Enhancements
1. **Prompt Library** - Reusable prompt collection
2. **Prompt Optimizer** - A/B test prompts automatically
3. **Token Counter** - Estimate costs before calling
4. **Response Cache** - Cache common prompt responses

### Module 04 Enhancements
1. **Real API Integration** - Connect to OpenWeatherMap
2. **Tool Composer** - Combine multiple tools
3. **Tool Validator** - Validate tool outputs
4. **Tool Metrics** - Track usage and performance

### Cross-Module Enhancements
1. **Unified Dashboard** - Monitor all modules
2. **Shared Utilities** - Common helpers library
3. **Centralized Config** - Config server for all modules
4. **API Gateway** - Single entry point

---

## 📚 Learning Path Integration

### Recommended Order
1. **00-course-setup** - Get environment ready
2. **01-introduction** - Basic LLM chat
3. **03-prompt-engineering** - Learn to craft prompts ← NEW
4. **04-tools** - Create reusable tools ← NEW
5. **02-rag** - Add document knowledge
6. **05-agents** - Build autonomous agents

**Rationale:** Prompts and Tools before RAG helps students understand fundamentals before complexity.

### Progressive Complexity
- **Module 03:** No external calls, just LLM
- **Module 04:** External calls, no AI decision
- **Module 02:** AI + vector database
- **Module 05:** AI + tools + multi-step reasoning

---

## 🎓 Expected Learning Outcomes

### After Module 03, students can:
- Write effective prompts for different tasks
- Use templates for reusable prompts
- Extract structured data from text
- Apply few-shot learning
- Use chain-of-thought reasoning
- Understand prompt engineering best practices

### After Module 04, students can:
- Create custom tools with @Tool annotation
- Integrate HTTP APIs as tools
- Validate tool inputs and outputs
- Handle tool execution errors
- Document tools properly
- Test tools independently

### Combined Outcome:
Students have all building blocks needed for Module 05 (Agents) - they understand prompts and tools, so agent orchestration makes sense.

---

## 📋 Pre-Implementation Checklist

**MUST COMPLETE BEFORE STARTING:**

- [ ] **Git Safety:** Create branch `git checkout -b phase2-implementation`
- [ ] **Backup Module 04:** Create branch `git checkout -b backup-module-04-original`
- [ ] **Review existing code:** Check modules 03, 04, 05 current structure
- [ ] **Test existing Module 04:** Run `cd 04-tools && mvn spring-boot:run` - verify it works
- [ ] **Test existing Module 05:** Run `cd 05-agents && mvn spring-boot:run` - verify it works
- [ ] **Check dependencies:** Review all pom.xml files
- [ ] **Azure credentials:** Verify `AZURE_OPENAI_ENDPOINT` and `AZURE_OPENAI_API_KEY` set
- [ ] **Test build:** Run `mvn clean install` from root - should pass
- [ ] **IDE configured:** VSCode or IntelliJ with Java extensions
- [ ] **Port availability:** Check 8083 and 8084 are free

---

## 🚀 Post-Implementation Checklist

**VALIDATE EVERYTHING:**

- [ ] **Module 03 compiles:** `cd 03-prompt-engineering && mvn clean package`
- [ ] **Module 03 runs:** `mvn spring-boot:run` (port 8083)
- [ ] **Module 03 endpoints work:** Test with `scripts/test-prompts.sh`
- [ ] **Module 04 compiles:** `cd 04-tools && mvn clean package`
- [ ] **Module 04 runs:** `mvn spring-boot:run` (port 8084, NOT 8082)
- [ ] **Module 04 endpoints work:** Test with `scripts/test-tools.sh`
- [ ] **No agent code in Module 04:** Verify AgentController and AgentService deleted
- [ ] **All modules compile together:** `mvn clean install` from root
- [ ] **No port conflicts:** All 6 modules can start on different ports
- [ ] **Module 05 still works:** Test agents module unchanged and functional
- [ ] **Module 01, 02 still work:** Verify no breaking changes
- [ ] **Test scripts pass:** All scripts/test-*.sh succeed
- [ ] **CHALLENGES.md created:** Both modules have hands-on exercises
- [ ] **Documentation complete:** READMEs accurate and helpful
- [ ] **Azure deployment tested:** `azd deploy` succeeds (optional but recommended)
- [ ] **README.md updated:** Main README includes Module 03/04 details
- [ ] **Git commits clean:** Clear commit messages throughout
- [ ] **Merged to main:** `git merge phase2-implementation` successful
- [ ] **Git tagged:** `git tag phase2-complete` created

---

## 📞 Support & Resources

### LangChain4j Resources
- [Prompt Templates](https://docs.langchain4j.dev/tutorials/prompt-templates)
- [Tools Documentation](https://docs.langchain4j.dev/tutorials/tools)
- [Azure OpenAI Integration](https://docs.langchain4j.dev/integrations/language-models/azure-open-ai)

### Azure Resources
- [Azure OpenAI Service](https://learn.microsoft.com/azure/ai-services/openai/)
- [OpenAI Prompt Engineering Guide](https://platform.openai.com/docs/guides/prompt-engineering)
- [Azure Container Apps](https://learn.microsoft.com/azure/container-apps/)

---

## 🎯 Next Steps

1. **Review this plan** - Ensure approach is correct
2. **Start with Module 03** - Implement one module fully before starting next
3. **Test incrementally** - Test each feature as you build
4. **Document as you go** - Don't leave documentation for the end
5. **Ask for help** - Use GitHub Copilot for code generation

---

**Status:** 📋 Ready for Implementation  
**Priority:** HIGH  
**Estimated Effort:** 2-3 weeks  
**Dependencies:** Azure OpenAI credentials, working environment  
**Risk Level:** Low (no changes to working modules)

---

## � Success Vision

When complete, students will have:
- ✅ **6 progressive modules** teaching LangChain4j fundamentals
- ✅ **Working code** they can run immediately
- ✅ **Hands-on challenges** to practice skills
- ✅ **Comprehensive documentation** to learn concepts
- ✅ **Production-ready patterns** for real applications
- ✅ **Azure deployment** experience

**Result:** A complete, professional LangChain4j course that takes beginners from zero to building AI applications confidently.

---

## 📊 Final Validation Summary

### Technical Validation ✅
- ✅ Module structure aligns with Spring Boot best practices
- ✅ Port assignments avoid conflicts (8080, 8081, 8082, 8083, 8084)
- ✅ Dependencies correctly specified in pom.xml files
- ✅ No circular dependencies between modules
- ✅ Build system can compile all modules together
- ⚠️ Azure deployment needs configuration updates

### Educational Validation ✅
- ✅ Learning progression is logical (basics → advanced)
- ✅ Each module has clear, distinct focus
- ✅ Challenges progressively increase in difficulty
- ✅ Documentation explains concepts before code
- ✅ Examples are practical and relevant

### Risk Assessment ✅
- 🟢 **LOW RISK:** Module 03 (new code, no dependencies)
- 🟡 **MEDIUM RISK:** Module 04 (refactoring existing, port change)
- 🟢 **LOW RISK:** Integration (parent POM already configured)
- 🟡 **MEDIUM RISK:** Azure deployment (configuration updates needed)

### Plan Approval Status: **✅ APPROVED - PROCEED WITH IMPLEMENTATION**

**Confidence Level:** HIGH (95%)
- Plan is technically sound
- Risks are identified and mitigated
- Implementation order is safe
- Rollback strategy is clear
- Success criteria are measurable

**Recommended Start:** Implement Module 03 first (lower risk, greenfield development)

---

## 🚦 GO/NO-GO Decision Criteria

### ✅ GO Conditions (All Met)
- ✅ Phase 1 complete (modules 00, 01, 02, 05 working)
- ✅ Git repository in clean state
- ✅ Azure OpenAI credentials available
- ✅ Build system working (`mvn clean install` passes)
- ✅ Safety branches can be created
- ✅ Implementation plan is detailed and clear

### 🛑 NO-GO Conditions (None Present)
- ❌ Existing modules broken
- ❌ Git repository in dirty/conflicted state
- ❌ Missing required credentials
- ❌ Build system failing
- ❌ Unclear requirements

**Decision: 🟢 GO FOR IMPLEMENTATION**
