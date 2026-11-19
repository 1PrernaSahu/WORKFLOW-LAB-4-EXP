# Process Variable Dictionary – Lab 1 (Loan Approval)

| Variable Name | Type | Scope / Origin | Description | Example (Synthetic) |
|---|---|---|---|---|
| `applicantId` | String | Process Start | Unique synthetic applicant identifier | `"SYN-APP-001-PASS"` |
| `applicantName` | String | Process Start | Synthetic applicant full name | `"Alice Johnson"` |
| `age` | Integer | Process Start | Applicant age in years | `35` |
| `employmentStatus` | String | Process Start | `EMPLOYED`, `SELF_EMPLOYED`, `UNEMPLOYED`, `STUDENT` | `"EMPLOYED"` |
| `requestedAmount` | Double | Process Start | Loan amount requested | `10000.0` |
| `monthlyIncome` | Double | Process Start | Monthly gross income | `5000.0` |
| `existingMonthlyDebt` | Double | Process Start | Existing monthly debt obligations | `800.0` |
| `loanTermMonths` | Integer | Process Start | Loan tenure in months | `60` |
| `isValid` | Boolean | Worker: `validate-applicant` | Whether input data meets initial validation rules | `true` |
| `validationErrors` | List<String> | Worker: `validate-applicant` | List of error messages if validation fails | `[]` |
| `debtToIncomeRatio` | Double | Worker: `validate-applicant` | Calculated as existingMonthlyDebt / monthlyIncome | `0.16` |
| `creditScore` | Integer | REST Connector / Mock API | Synthetic credit score (300 - 850) | `750` |
| `riskBand` | String | REST Connector / Mock API | `LOW`, `MEDIUM`, `HIGH` | `"LOW"` |
| `decision` | String | DMN: `loan-decision` | `APPROVED`, `REJECTED`, `MANUAL_REVIEW` | `"APPROVED"` |
| `reason` | String | DMN: `loan-decision` | Human-readable explanation of DMN rule matched | `"High credit score and low DTI"` |
| `officerDecision` | String | User Task: Manual Review | `APPROVE` or `REJECT` | `"APPROVE"` |
| `officerComments` | String | User Task: Manual Review | Reviewer audit notes | `"Approved after income check"` |

