# Lab 2 – Order Processing: cURL API Examples

All commands use **synthetic data only** – no real customer or payment details.

---

## 1. Worker Health & Topology

```bash
# Check worker health
curl -s http://localhost:8094/actuator/health

# Confirm Zeebe connection
curl -s http://localhost:8094/workflow/topology
```

---

## 2. Deploy BPMN and DMN

```bash
curl -s -X POST "http://localhost:8094/workflow/deploy"
```

Expected response:
```json
{
  "status": "SUCCESS",
  "deploymentKey": 2251799813692639,
  "bpmnProcesses": [{"bpmnProcessId": "order-processing-process", "version": 1}],
  "decisions": [{"decisionId": "order-priority", "version": 1}]
}
```

---

## 3. Start a Process Instance

### TC-L2-01: Standard Order → Success

```bash
curl -s -X POST http://localhost:8094/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "customerRef": "SYN-CUST-001",
    "customerCategory": "STANDARD",
    "productId": "PROD-001",
    "quantity": 1,
    "deliveryPreference": "STANDARD",
    "paymentToken": "tok_test_success"
  }'
```

### TC-L2-02: Gold Customer EXPRESS Order

```bash
curl -s -X POST http://localhost:8094/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "customerRef": "SYN-CUST-002",
    "customerCategory": "GOLD",
    "productId": "PROD-002",
    "quantity": 2,
    "deliveryPreference": "EXPRESS",
    "paymentToken": "tok_test_success"
  }'
```

### TC-L2-03: Out-of-Stock → Stock Unavailable End Event

```bash
curl -s -X POST http://localhost:8094/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "customerRef": "SYN-CUST-003",
    "customerCategory": "STANDARD",
    "productId": "PROD-OUT-OF-STOCK",
    "quantity": 1,
    "deliveryPreference": "STANDARD",
    "paymentToken": "tok_test_success"
  }'
```

### TC-L2-04: Declined Payment → Payment Failed End Event

```bash
curl -s -X POST http://localhost:8094/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "customerRef": "SYN-CUST-004",
    "customerCategory": "STANDARD",
    "productId": "PROD-003",
    "quantity": 2,
    "deliveryPreference": "STANDARD",
    "paymentToken": "tok_test_declined"
  }'
```

---

## 4. Payment Mock API (port 8089)

### Direct call to payment charge endpoint

```bash
curl -s -X POST http://localhost:8089/payments/charge \
  -H "Content-Type: application/json" \
  -d '{
    "customerRef": "SYN-CUST-DIRECT",
    "paymentToken": "tok_test_success",
    "orderTotal": 199.99
  }'
```

---

## 5. Failure Injection & Recovery

```bash
# Enable API failure mode
curl -s -X POST http://localhost:8089/admin/failure-mode \
  -H "Content-Type: application/json" \
  -d '{"enabled": true}'

# Start process (causes incident in Operate)
curl -s -X POST http://localhost:8094/workflow/start \
  -H "Content-Type: application/json" \
  -d '{
    "customerRef": "SYN-CUST-006",
    "customerCategory": "GOLD",
    "productId": "PROD-001",
    "quantity": 1,
    "deliveryPreference": "STANDARD",
    "paymentToken": "tok_test_success"
  }'

# Disable failure mode and retry incident in Operate
curl -s -X POST http://localhost:8089/admin/failure-mode \
  -H "Content-Type: application/json" \
  -d '{"enabled": false}'
```

