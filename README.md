# PBJ-2 Practical Assignment: Camunda 8 End-to-End Workflow Automation

> **Academic Level**: 2nd Year B.Tech Computer Science & Engineering  
> **Course**: Process-Based Java & Enterprise Workflow Automation (PBJ-2)  
> **Technologies**: Camunda 8.6 (Zeebe, Operate, Tasklist), Spring Boot 3.3.5, Java 21/23, DMN, BPMN 2.0, Maven, REST Connectors  
> **Data Privacy Notice**: 100% synthetic data used throughout. No real personal, financial, card, or medical information is included or processed.

---

## 1. Project Overview

This repository provides a complete, runnable, and verified submission for Labs 1 through 3:
- **Lab 1 – Loan Approval**: Automated credit scoring, DTI calculation, DMN approval matrix, manual review task, and credit API error/timeout simulation.
- **Lab 2 – E-Commerce Order Processing**: Inventory check worker, order prioritization DMN, high-value fraud review, mock payment token gateway, and failure injection.
- **Lab 3 – Hospital Emergency Triage**: Patient symptom validation, clinical acuity DMN classification, staff slot scheduling review, and appointment confirmation. *(Educational demonstration only; not clinical advice).*

---

## 2. Service Architecture & Port Map

| Component | Port | Description | UI / Endpoint |
|---|---|---|---|
| **Camunda Operate** | `8080` | Process instance monitoring & incident debugging | `http://localhost:8080/operate` |
| **Camunda Tasklist** | `8080` | Human workflow user tasks & forms | `http://localhost:8080/tasklist` |
| **Zeebe Gateway** | `26500` | gRPC Workflow engine broker (plaintext) | `127.0.0.1:26500` |
| **Connectors Runtime** | `8085` | Inbound & Outbound HTTP REST Connectors | `http://localhost:8085` |
| **Elasticsearch** | `9200` | Distributed persistence & audit log index | `http://localhost:9200` |
| **Lab 1 Mock Credit API** | `8088` | Synthetic credit scoring & failure injection | `http://localhost:8088/swagger-ui.html` |
| **Lab 1 Worker Application** | `8092` | `@JobWorker(type="validate-applicant")` & Deployer | `http://localhost:8092/workflow/topology` |
| **Lab 2 Mock Payment API** | `8089` | Synthetic payment charge gateway | `http://localhost:8089/swagger-ui.html` |
| **Lab 2 Worker Application** | `8094` | `@JobWorker(type="validate-order-and-check-stock")` | `http://localhost:8094/workflow/topology` |
| **Lab 3 Mock Scheduling API** | `8090` | Synthetic slot query & availability simulation | `http://localhost:8090/swagger-ui.html` |
| **Lab 3 Worker Application** | `8096` | `@JobWorker(type="validate-registration", "book-appointment")` | `http://localhost:8096/workflow/topology` |

---

## 3. Quick Start (Windows & Linux / Mac)

### Step 1: Start Camunda 8 Infrastructure
If using **Camunda 8 Run (`c8run`)**:
```powershell
# In PowerShell:
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"
$env:JAVA_VERSION = "23"
cd d:\workflow\c8run
.\c8run.exe start
```
Or if using **Docker Desktop**:
```bash
docker compose up -d
```

### Step 2: Build All Spring Boot Modules
```bash
cd camunda8-assignment
mvn clean package -DskipTests
```

### Step 3: Start All Workers & Mock APIs
- Windows: run `start.bat`
- Linux / Mac: run `./start.sh`

---

## 4. 10-Minute Practical Demonstration Script

Follow these steps during your viva or laboratory demonstration:

### Minute 1–2: Infrastructure & Health Verification
1. Open Chrome and navigate to `http://localhost:8080/operate`.
   - Explain: *"This is Camunda Operate running against a local Zeebe 8.6 broker with Elasticsearch persistence."*
2. Check worker connection topology:
   ```bash
   curl -s http://localhost:8092/workflow/topology
   ```
   - Shows: `{"status":"CONNECTED","gatewayVersion":"8.6.8","clusterSize":1}`.

### Minute 3–4: Lab 1 (Loan Approval)
1. Run automated test suite:
   ```powershell
   powershell.exe -ExecutionPolicy Bypass -File lab1-loan-approval\test\run-tests.ps1
   ```
   - Demonstrates 22/22 tests passing automatically.
2. Show Operate dashboard:
   - Click into `loan-approval-process`.
   - Highlight completed instance for `Alice Johnson` (Approved).
   - Highlight completed instance for `Bob Smith` (Rejected via low credit score).
3. Demonstrate User Task in Tasklist:
   - Go to `http://localhost:8080/tasklist`.
   - Open the "Manual Review" task for `Carol Davis`.
   - Show the embedded form with synthetic income details.
   - Click "Approve" and show the process complete in Operate.

### Minute 5–6: Incident Generation & Live Recovery
1. Inject synthetic credit service failure:
   ```bash
   curl -X POST http://localhost:8088/admin/failure-mode -H "Content-Type: application/json" -d "{\"enabled\":true}"
   ```
2. Trigger loan request:
   ```bash
   curl -X POST http://localhost:8092/workflow/start -H "Content-Type: application/json" -d "{\"applicantId\":\"DEMO-INCIDENT\",\"applicantName\":\"Incident Test\",\"age\":30,\"employmentStatus\":\"EMPLOYED\",\"requestedAmount\":10000,\"monthlyIncome\":5000,\"existingMonthlyDebt\":500,\"loanTermMonths\":36}"
   ```
3. Show Operate:
   - A bright red badge appears on the "Assess Credit Profile" REST connector task.
   - Click into the incident: shows `HTTP 500: Synthetic Credit Assessment Service Failure Mode is currently ACTIVE`.
4. Recover from Incident:
   - Turn off failure mode:
     ```bash
     curl -X POST http://localhost:8088/admin/failure-mode -H "Content-Type: application/json" -d "{\"enabled\":false}"
     ```
   - In Operate UI, click **"Retry"** on the incident.
   - The task executes successfully, and the process completes!

### Minute 7–8: Lab 2 (Order Processing)
1. Run Lab 2 test suite:
   ```powershell
   powershell.exe -ExecutionPolicy Bypass -File lab2-order-processing\test\run-tests.ps1
   ```
   - Demonstrates 20/20 tests passing.
2. Show stock check worker logic:
   - Stock available (`PROD-001`) proceeds to DMN & payment.
   - Out-of-stock (`PROD-OUT-OF-STOCK`) branches to "Stock Unavailable End" event.

### Minute 9–10: Lab 3 (Hospital Emergency Triage)
1. Run Lab 3 test suite:
   ```powershell
   powershell.exe -ExecutionPolicy Bypass -File lab3-hospital-triage\test\run-tests.ps1
   ```
   - Demonstrates 23/23 tests passing.
2. Highlight DMN `triage-classification` decision routing:
   - Severe chest pain & dyspnea routes to Urgent Clinical Assessment.
   - Routine headache routes directly to available morning slot booking worker.

---

## 5. Directory Structure

```
camunda8-assignment/
├── pom.xml                               # Maven parent POM (Spring Boot 3.3.5, Camunda 8.6.8)
├── docker-compose.yml                    # Optional Docker configuration
├── start.bat / start.sh                  # Service bootstrap scripts
├── stop.bat / stop.sh                    # Service shutdown scripts
│
├── lab1-loan-approval/                   # Lab 1 Module
│   ├── bpmn/loan-approval.bpmn
│   ├── bpmn/manual-review.form
│   ├── dmn/loan-decision.dmn
│   ├── worker/                           # Spring Boot worker (port 8092)
│   ├── mock-api/                         # Mock credit API (port 8088)
│   ├── api-examples/                     # cURL, HTTP, and Postman examples
│   ├── test/                             # run-tests.ps1 & run-tests.sh
│   └── docs/                             # Report, DMN rules, Variable dictionary
│
├── lab2-order-processing/                # Lab 2 Module
│   ├── bpmn/order-processing.bpmn
│   ├── bpmn/manual-verification.form
│   ├── dmn/order-priority.dmn
│   ├── worker/                           # Spring Boot worker (port 8094)
│   ├── mock-api/                         # Mock payment API (port 8089)
│   ├── api-examples/
│   ├── test/
│   └── docs/
│
├── lab3-hospital-triage/                 # Lab 3 Module
│   ├── bpmn/hospital-triage.bpmn
│   ├── bpmn/*.form                       # Camunda form definitions
│   ├── dmn/triage-classification.dmn
│   ├── worker/                           # Spring Boot worker (port 8096)
│   ├── mock-api/                         # Mock scheduling API (port 8090)
│   ├── api-examples/
│   ├── test/
│   └── docs/
│
└── docs/                                 # Central Documentation
    ├── screenshots/SCREENSHOT_CHECKLIST.md
    ├── FINAL_SELF_AUDIT.md
    └── SUBMISSION_CHECKLIST.md
```

---

## 6. Troubleshooting

- **Worker cannot connect to Zeebe (`UNAVAILABLE: io exception`)**:
  Confirm Zeebe broker is listening on port `26500` via `netstat -ano | findstr 26500`. Verify `zeebe.client.security.plaintext=true`.
- **Port already in use**:
  Use `stop.bat` to terminate all background Java workers and mock APIs.
- **Operate UI not loading**:
  Confirm Elasticsearch is green at `http://localhost:9200/_cluster/health`.

