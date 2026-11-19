# DMN Rules Explanation – Lab 1: `loan-decision`

## Decision Overview
- **Decision ID**: `loan-decision`
- **Hit Policy**: `FIRST` (First rule matching all inputs determines the output)

## Inputs
1. `creditScore` (integer)
2. `debtToIncomeRatio` (number)
3. `employmentStatus` (string)
4. `requestedAmount` (number)

## Outputs
1. `decision` (string: `APPROVED`, `REJECTED`, `MANUAL_REVIEW`)
2. `reason` (string)

---

## Decision Table Rules

| Rule # | Credit Score | DTI Ratio | Employment Status | Amount | Decision | Reason |
|---|---|---|---|---|---|---|
| 1 | `>= 750` | `<= 0.35` | `"EMPLOYED"` | `<= 50000` | `APPROVED` | High credit score, low DTI, stable employment |
| 2 | `>= 700` | `<= 0.30` | `"EMPLOYED"` | `<= 30000` | `APPROVED` | Good credit score, low DTI |
| 3 | `< 580` | `-` | `-` | `-` | `REJECTED` | Credit score below minimum threshold |
| 4 | `-` | `> 0.50` | `-` | `-` | `REJECTED` | Debt-to-income ratio exceeds 50% limit |
| 5 | `-` | `-` | `"UNEMPLOYED"` | `-` | `REJECTED` | Unemployed applicant ineligible |
| 6 | `>= 650` | `<= 0.45` | `"SELF_EMPLOYED"` | `<= 40000` | `MANUAL_REVIEW` | Self-employed applicant requires income verification |
| 7 | `>= 620` | `<= 0.40` | `"EMPLOYED"` | `<= 25000` | `MANUAL_REVIEW` | Moderate credit profile requires secondary assessment |
| 8 (Catch-all) | `-` | `-` | `-` | `-` | `REJECTED` | Profile did not meet automatic approval or review criteria |

