# Lab 1 – Loan Approval: cURL API Examples

All commands use **synthetic data only** – no real personal or financial information.

---

## 1. Worker Health & Topology

```bash
# Check worker health
curl -s http://localhost:8092/actuator/health

# Confirm Zeebe connection
curl -s http://localhost:8092/workflow/topology
```

---

## 2. Deploy BPMN and DMN

```bash
curl -s -X POST "http://localhost:8092/workflow/deploy"
```

Expected response:
```json
{
  "status": "SUCCESS",
  "deploymentKey": 2251799813685249,
  "bpmnProcesses": [{"bpmnProcessId": "loan-approval-process", "version": 1}],
  "decisions": [{"decisionId": "loan-decision", "version": 1}]
}
```

---

## 3. Start a Process Instance

### TC-L1-01: Eligible Applicant → APPROVED

```bash
curl -s -X POST http://localhost:8092/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "applicantId": "SYN-APP-001-PASS",
    "applicantName": "Alice Johnson",
    "age": 35,
    "employmentStatus": "EMPLOYED",
    "requestedAmount": 10000,
    "monthlyIncome": 5000,
    "existingMonthlyDebt": 800,
    "loanTermMonths": 60
  }'
```

### TC-L1-02: Ineligible Applicant → REJECTED

```bash
curl -s -X POST http://localhost:8092/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "applicantId": "SYN-APP-002-FAIL",
    "applicantName": "Bob Smith",
    "age": 25,
    "employmentStatus": "UNEMPLOYED",
    "requestedAmount": 25000,
    "monthlyIncome": 1200,
    "existingMonthlyDebt": 700,
    "loanTermMonths": 60
  }'
```

### TC-L1-03: Borderline Applicant → MANUAL_REVIEW

```bash
curl -s -X POST http://localhost:8092/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "applicantId": "SYN-APP-003-MANUAL",
    "applicantName": "Carol Davis",
    "age": 42,
    "employmentStatus": "SELF_EMPLOYED",
    "requestedAmount": 30000,
    "monthlyIncome": 4000,
    "existingMonthlyDebt": 1200,
    "loanTermMonths": 48
  }'
```

### TC-L1-04: Invalid Applicant → Validation Rejection

```bash
curl -s -X POST http://localhost:8092/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "applicantId": "SYN-APP-004-INVALID",
    "applicantName": "Dave Young",
    "age": 15,
    "employmentStatus": "STUDENT",
    "requestedAmount": 5000,
    "monthlyIncome": 500,
    "existingMonthlyDebt": 0,
    "loanTermMonths": 24
  }'
```

---

## 4. Credit Assessment Mock API (port 8088)

### Direct call to credit assessment endpoint

```bash
curl -s -X POST http://localhost:8088/credit/assess \
  -H "Content-Type: application/json" \
  -d '{
    "applicantId": "SYN-APP-001-PASS",
    "requestedAmount": 10000,
    "monthlyIncome": 5000,
    "employmentStatus": "EMPLOYED"
  }'
```

### Check API health

```bash
curl -s http://localhost:8088/health
curl -s http://localhost:8088/actuator/health
```

---

## 5. Failure Injection (TC-L1-04 & TC-L1-05)

### Enable API failure mode

```bash
curl -s -X POST http://localhost:8088/admin/failure-mode \
  -H "Content-Type: application/json" \
  -d '{"enabled": true}'
```

### Start a process (will fail at credit assessment step → creates incident in Operate)

```bash
curl -s -X POST http://localhost:8092/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "applicantId": "SYN-APP-005-INCIDENT",
    "applicantName": "Eve Green",
    "age": 30,
    "employmentStatus": "EMPLOYED",
    "requestedAmount": 15000,
    "monthlyIncome": 4500,
    "existingMonthlyDebt": 600,
    "loanTermMonths": 36
  }'
```

### Disable API failure mode (then retry in Operate)

```bash
curl -s -X POST http://localhost:8088/admin/failure-mode \
  -H "Content-Type: application/json" \
  -d '{"enabled": false}'
```

### Enable timeout simulation

```bash
curl -s -X POST http://localhost:8088/admin/timeout-mode \
  -H "Content-Type: application/json" \
  -d '{"enabled": true, "delaySeconds": 10}'

# Disable timeout
curl -s -X POST http://localhost:8088/admin/timeout-mode \
  -H "Content-Type: application/json" \
  -d '{"enabled": false}'
```

---

## 6. Worker Deliberate Failure (TC-L1-06)

```bash
# Enable deliberate worker failure
curl -s -X POST http://localhost:8092/worker-config/failure-mode \
  -H "Content-Type: application/json" \
  -d '{"enabled": true}'

# Start a process (validate-applicant worker will throw exception → incident)
curl -s -X POST http://localhost:8092/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "applicantId": "SYN-APP-006-WKRFAIL",
    "applicantName": "Frank Miller",
    "age": 40,
    "employmentStatus": "EMPLOYED",
    "requestedAmount": 20000,
    "monthlyIncome": 6000,
    "existingMonthlyDebt": 400,
    "loanTermMonths": 48
  }'

# Check incident in Operate: http://localhost:8080/operate

# Disable worker failure mode
curl -s -X POST http://localhost:8092/worker-config/failure-mode \
  -H "Content-Type: application/json" \
  -d '{"enabled": false}'

# Now retry the incident in Operate UI → process continues
```

---

## 7. Admin Status Check

```bash
curl -s http://localhost:8088/admin/status
curl -s http://localhost:8092/worker-config/status
```

---

## 8. Swagger UI

Open in browser: http://localhost:8088/swagger-ui.html

