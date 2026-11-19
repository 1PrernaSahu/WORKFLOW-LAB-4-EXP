# Test Case Table – Lab 3 (Hospital Triage)

> **DISCLAIMER**: For educational demonstration only.

| Test ID | Description | Input Variables | Expected Path / Outcome | Actual Outcome | Status |
|---|---|---|---|---|---|
| `TC-L3-01` | Routine patient (painScore=2) | `painScore: 2`, `chestPain: false`, `breathingDifficulty: false` | DMN sets `ROUTINE` -> Check slots -> Book appointment | Appointment Confirmed End | **PASS** |
| `TC-L3-02` | Moderate patient (feverDays=4) | `feverDays: 4`, `painScore: 6` | DMN sets `PRIORITY` -> Priority review -> Check slots -> Book | Appointment Confirmed End | **PASS** |
| `TC-L3-03` | Urgent patient (chestPain=true) | `chestPain: true`, `breathingDifficulty: true`, `painScore: 8` | DMN sets `URGENT` -> Urgent Clinical Assessment User Task | User Task in Tasklist (`triage-staff`) | **PASS** |
| `TC-L3-04` | No slots available | Disabled slot availability via Admin API | Slots returned false -> Staff Slot Review User Task | Staff Slot Review task in Tasklist | **PASS** |
| `TC-L3-05` | Scheduling API Failure Mode | Enabled failure flag on port 8090 | Connector receives 500 -> Incident raised in Operate | Incident raised, retryable | **PASS** |
| `TC-L3-06` | Worker Deliberate Failure Mode | Enabled worker failure flag via REST | Worker throws deliberate RuntimeException -> Incident | Incident raised, recovered after toggle | **PASS** |

