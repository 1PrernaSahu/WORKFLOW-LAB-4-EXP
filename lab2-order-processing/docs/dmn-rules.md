# DMN Rules Explanation – Lab 2: `order-priority`

## Decision Overview
- **Decision ID**: `order-priority`
- **Hit Policy**: `FIRST`

## Inputs
1. `customerCategory` (string: `PLATINUM`, `GOLD`, `STANDARD`)
2. `deliveryPreference` (string: `NEXT_DAY`, `EXPRESS`, `STANDARD`)
3. `orderTotal` (number)

## Outputs
1. `priority` (string: `HIGH`, `MEDIUM`, `LOW`)
2. `requiresManualVerification` (boolean)

---

## Decision Table Rules

| Rule # | Customer Category | Delivery Preference | Order Total | Priority | Requires Manual Verification |
|---|---|---|---|---|---|
| 1 | `"PLATINUM"` | `-` | `-` | `HIGH` | `false` |
| 2 | `"GOLD"` | `"NEXT_DAY"` | `-` | `HIGH` | `false` |
| 3 | `"GOLD"` | `"EXPRESS"` | `-` | `HIGH` | `false` |
| 4 | `-` | `-` | `> 1000` | `HIGH` | `true` |
| 5 | `"STANDARD"` | `"NEXT_DAY"` | `-` | `MEDIUM` | `true` |
| 6 | `"STANDARD"` | `"EXPRESS"` | `-` | `MEDIUM` | `false` |
| 7 | `"GOLD"` | `"STANDARD"` | `-` | `MEDIUM` | `false` |
| 8 (Catch-all) | `-` | `-` | `-` | `LOW` | `false` |

