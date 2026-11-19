# DMN Rules Explanation – Lab 3: `triage-classification`

> **DISCLAIMER**: For educational demonstration only.

## Decision Overview
- **Decision ID**: `triage-classification`
- **Hit Policy**: `FIRST`

## Inputs
1. `chestPain` (boolean)
2. `breathingDifficulty` (boolean)
3. `feverDays` (integer)
4. `painScore` (integer, 0-10)

## Outputs
1. `triageLevel` (string: `URGENT`, `PRIORITY`, `ROUTINE`)
2. `recommendedAction` (string)

---

## Decision Table Rules

| Rule # | Chest Pain | Breathing Difficulty | Fever Days | Pain Score | Triage Level | Recommended Action |
|---|---|---|---|---|---|---|
| 1 | `true` | `true` | `-` | `-` | `URGENT` | Immediate clinical emergency intake |
| 2 | `true` | `-` | `-` | `-` | `URGENT` | Urgent cardiac assessment protocol |
| 3 | `-` | `true` | `-` | `-` | `URGENT` | Acute respiratory assessment protocol |
| 4 | `-` | `-` | `-` | `>= 8` | `URGENT` | Severe acute pain protocol |
| 5 | `-` | `-` | `>= 3` | `>= 5` | `PRIORITY` | Prolonged fever with moderate pain - priority review |
| 6 | `-` | `-` | `-` | `>= 5` | `PRIORITY` | Moderate pain - priority consultation |
| 7 (Catch-all) | `-` | `-` | `-` | `-` | `ROUTINE` | Standard outpatient appointment |

