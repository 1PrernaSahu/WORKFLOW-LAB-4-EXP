# Process Variable Dictionary – Lab 3 (Hospital Triage)

> **DISCLAIMER**: For educational demonstration only.

| Variable Name | Type | Scope / Origin | Description | Example (Synthetic) |
|---|---|---|---|---|
| `patientRef` | String | Process Start | Synthetic patient identifier | `"SYN-PAT-001"` |
| `appointmentPreference` | String | Process Start | `MORNING`, `AFTERNOON`, `EVENING` | `"MORNING"` |
| `symptomsSummary` | String | Process Start | Synthetic symptoms description | `"Mild headache"` |
| `chestPain` | Boolean | Process Start | Indicator for chest discomfort | `false` |
| `breathingDifficulty` | Boolean | Process Start | Indicator for shortness of breath | `false` |
| `feverDays` | Integer | Process Start | Duration of elevated temperature | `0` |
| `painScore` | Integer | Process Start | Numeric subjective pain scale (0 - 10) | `2` |
| `isRegistrationValid` | Boolean | Worker: `validate-registration` | Registration validation flag | `true` |
| `triageLevel` | String | DMN: `triage-classification` | `URGENT`, `PRIORITY`, `ROUTINE` | `"ROUTINE"` |
| `recommendedAction` | String | DMN: `triage-classification` | Recommended clinical action path | `"Standard outpatient consultation"` |
| `slotsAvailable` | Boolean | REST Connector / Scheduling API | Availability of booking slots | `true` |
| `availableSlotId` | String | REST Connector / Scheduling API | Synthetic slot ID | `"SLOT-AM-01"` |
| `appointmentId` | String | Worker: `book-appointment` | Confirmed synthetic booking reference | `"APT-76543"` |
| `confirmationStatus` | String | Worker: `book-appointment` | `CONFIRMED` | `"CONFIRMED"` |

