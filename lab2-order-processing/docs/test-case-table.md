# Test Case Table – Lab 2 (Order Processing)

| Test ID | Description | Input Variables | Expected Path / Outcome | Actual Outcome | Status |
|---|---|---|---|---|---|
| `TC-L2-01` | Standard Customer Regular Order | `customerCategory: "STANDARD"`, `productId: "PROD-001"`, `paymentToken: "tok_test_success"` | Stock OK -> DMN Low -> Payment OK -> Dispatched End | Dispatched End Event | **PASS** |
| `TC-L2-02` | Gold Customer Express Order | `customerCategory: "GOLD"`, `deliveryPreference: "EXPRESS"`, `paymentToken: "tok_test_success"` | Stock OK -> DMN High -> Payment OK -> Dispatched End | Dispatched End Event | **PASS** |
| `TC-L2-03` | Out of Stock Product | `productId: "PROD-OUT-OF-STOCK"` | Worker flags `inStock=false` -> Stock Unavailable End | Stock Unavailable End Event | **PASS** |
| `TC-L2-04` | Declined Payment Token | `paymentToken: "tok_test_declined"` | Stock OK -> Payment API returns DECLINED -> Payment Failed End | Payment Failed End Event | **PASS** |
| `TC-L2-05` | High Value / Next Day Delivery | `customerCategory: "STANDARD"`, `quantity: 5`, `deliveryPreference: "NEXT_DAY"` | DMN sets `requiresManualVerification=true` -> User Task created | User Task in Tasklist | **PASS** |
| `TC-L2-06` | Payment API Failure Mode | Enabled failure flag on port 8089 | Connector throws 500 -> Incident raised in Operate | Incident raised & retryable | **PASS** |

