# Test Case Table – Lab 1 (Loan Approval)

| Test ID | Description | Input Variables | Expected Path / Outcome | Actual Outcome | Status |
|---|---|---|---|---|---|
| `TC-L1-01` | High-credit eligible applicant | `applicantId: "SYN-APP-001-PASS"`, amount: 10000, income: 5000, debt: 800 | Full flow -> Credit API -> DMN Approved -> Approved End | Reached Approved End Event | **PASS** |
| `TC-L1-02` | High-debt / unemployed applicant | `applicantId: "SYN-APP-002-FAIL"`, amount: 25000, income: 1200, unemployed | Full flow -> Credit API -> DMN Rejected -> Rejected End | Reached Rejected End Event | **PASS** |
| `TC-L1-03` | Borderline self-employed applicant | `applicantId: "SYN-APP-003-MANUAL"`, amount: 30000, income: 4000, self-employed | DMN Manual Review -> User Task in Tasklist | User Task assigned to `loan-officers` | **PASS** |
| `TC-L1-04` | Credit API Failure Mode | Failure mode enabled -> Start process instance | Connector receives HTTP 500 -> Incident raised in Operate | Incident created in Operate UI | **PASS** |
| `TC-L1-05` | Credit API Timeout Simulation | Timeout enabled -> Start process instance | Connector triggers timeout policy -> Incident or retry | Handled gracefully | **PASS** |
| `TC-L1-06` | Worker Deliberate Failure Mode | Worker failure flag enabled via REST | Worker throws deliberate RuntimeException -> Incident in Operate | Incident raised, recovered after toggle | **PASS** |

