# Lab 3 – Hospital Triage: cURL API Examples

> **DISCLAIMER**: This is an educational demonstration only. It does not constitute real medical advice.

All commands use **synthetic data only** – no real patient information.

---

## 1. Worker Health & Topology

```bash
# Check worker health
curl -s http://localhost:8096/actuator/health

# Confirm Zeebe connection
curl -s http://localhost:8096/workflow/topology
```

---

## 2. Deploy BPMN and DMN

```bash
curl -s -X POST "http://localhost:8096/workflow/deploy"
```

Expected response:
```json
{
  "status": "SUCCESS",
  "deploymentKey": 2251799813692644,
  "bpmnProcesses": [{"bpmnProcessId": "hospital-triage-process", "version": 1}],
  "decisions": [{"decisionId": "triage-classification", "version": 1}]
}
```

---

## 3. Start a Process Instance

### TC-L3-01: Routine Patient → LOW Priority → Appointment Booked

```bash
curl -s -X POST http://localhost:8096/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "patientRef": "SYN-PAT-001",
    "appointmentPreference": "MORNING",
    "symptomsSummary": "Mild headache for 2 days, no fever",
    "chestPain": false,
    "breathingDifficulty": false,
    "feverDays": 0,
    "painScore": 2
  }'
```

### TC-L3-03: Urgent Patient → URGENT Priority → Clinical Assessment User Task

```bash
curl -s -X POST http://localhost:8096/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "patientRef": "SYN-PAT-003",
    "appointmentPreference": "MORNING",
    "symptomsSummary": "Chest pain and shortness of breath",
    "chestPain": true,
    "breathingDifficulty": true,
    "feverDays": 0,
    "painScore": 8
  }'
```

---

## 4. Scheduling Mock API (port 8090)

```bash
# Direct slot query
curl -s "http://localhost:8090/scheduling/slots?preference=MORNING"

# Check API health
curl -s http://localhost:8090/health
```

---

## 5. Slot Availability & Failure Injection

```bash
# Disable slot availability (forces Staff Slot Review task)
curl -s -X POST http://localhost:8090/admin/slots-availability \
  -H "Content-Type: application/json" \
  -d '{"available": false}'

# Re-enable slot availability
curl -s -X POST http://localhost:8090/admin/slots-availability \
  -H "Content-Type: application/json" \
  -d '{"available": true}'
```

