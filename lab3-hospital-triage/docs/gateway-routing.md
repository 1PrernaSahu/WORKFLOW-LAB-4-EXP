# Gateway Routing Documentation – Lab 3

## Gateway 1: `Gateway_TriageRouting` (Exclusive Gateway)
Routes based on output variable `triageLevel` from DMN `triage-classification`:

- **Flow to Urgent Assessment User Task**:
  - Condition Expression: `= triageLevel = "URGENT"`
- **Flow to Priority Review User Task**:
  - Condition Expression: `= triageLevel = "PRIORITY"`
- **Flow to Slot Query REST Connector (Default)**:
  - Default sequence flow: catches `ROUTINE` and unclassified profiles.

---

## Gateway 2: `Gateway_SlotAvailability` (Exclusive Gateway)
Routes based on output variable `slotsAvailable` from the REST Connector:

- **Flow to Book Appointment Worker**:
  - Condition Expression: `= slotsAvailable = true`
- **Flow to Staff Slot Review User Task (Default)**:
  - Default sequence flow: catches unavailable slots (`false`).

