# Process Variable Dictionary – Lab 2 (Order Processing)

| Variable Name | Type | Scope / Origin | Description | Example (Synthetic) |
|---|---|---|---|---|
| `customerRef` | String | Process Start | Synthetic customer ID | `"SYN-CUST-001"` |
| `customerCategory` | String | Process Start | `STANDARD`, `GOLD`, `PLATINUM` | `"GOLD"` |
| `productId` | String | Process Start | Synthetic product SKU | `"PROD-001"` |
| `quantity` | Integer | Process Start | Item count ordered | `2` |
| `deliveryPreference` | String | Process Start | `STANDARD`, `EXPRESS`, `NEXT_DAY` | `"EXPRESS"` |
| `paymentToken` | String | Process Start | Synthetic test token | `"tok_test_success"` |
| `inStock` | Boolean | Worker: Stock Check | True if inventory available | `true` |
| `unitPrice` | Double | Worker: Stock Check | Unit price per item | `149.99` |
| `orderTotal` | Double | Worker: Stock Check | Computed total price (unitPrice * quantity) | `299.98` |
| `priority` | String | DMN: `order-priority` | `HIGH`, `MEDIUM`, `LOW` | `"HIGH"` |
| `requiresManualVerification` | Boolean | DMN: `order-priority` | Flag indicating high-value or suspect order review | `false` |
| `verificationApproved` | Boolean | User Task: Verification | Approval flag submitted by order reviewer | `true` |
| `paymentStatus` | String | REST Connector / Payment API | `SUCCESS`, `DECLINED`, `FAILED` | `"SUCCESS"` |
| `transactionId` | String | REST Connector / Payment API | Synthetic payment transaction ID | `"TXN-98432"` |

