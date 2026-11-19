# Gateway Routing Documentation – Lab 1

## Gateway 1: `Gateway_Validation` (Exclusive Gateway)
Evaluates whether applicant inputs passed initial syntactic and business boundary checks.

- **Sequence Flow to Credit API**:
  - Condition Expression: `= isValid = true`
- **Sequence Flow to Rejected End (Default)**:
  - Default sequence flow: marked with `isDefault="true"`

---

## Gateway 2: `Gateway_LoanDecision` (Exclusive Gateway)
Evaluates output variable `decision` from DMN `loan-decision`.

- **Sequence Flow to Approved End**:
  - Condition Expression: `= decision = "APPROVED"`
- **Sequence Flow to Manual Review User Task**:
  - Condition Expression: `= decision = "MANUAL_REVIEW"`
- **Sequence Flow to Rejected End (Default)**:
  - Default sequence flow: catches `REJECTED` and any unhandled conditions.

---

## Gateway 3: `Gateway_OfficerReview` (Exclusive Gateway)
Evaluates user task output variable `officerDecision`.

- **Sequence Flow to Approved End**:
  - Condition Expression: `= officerDecision = "APPROVE"`
- **Sequence Flow to Rejected End (Default)**:
  - Default sequence flow.

