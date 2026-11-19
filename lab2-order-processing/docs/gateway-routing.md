# Gateway Routing Documentation – Lab 2

## Gateway 1: `Gateway_StockCheck` (Exclusive Gateway)
- **Flow to DMN**: Condition `= inStock = true`
- **Flow to Stock Unavailable End (Default)**: default flow

---

## Gateway 2: `Gateway_ManualVerificationCheck` (Exclusive Gateway)
- **Flow to User Task (Manual Verification)**: Condition `= requiresManualVerification = true`
- **Flow to Payment Connector (Default)**: default flow

---

## Gateway 3: `Gateway_VerificationOutcome` (Exclusive Gateway)
- **Flow to Payment Connector**: Condition `= verificationApproved = true`
- **Flow to Order Cancelled End (Default)**: default flow

---

## Gateway 4: `Gateway_PaymentResult` (Exclusive Gateway)
- **Flow to Order Dispatched End**: Condition `= paymentStatus = "SUCCESS"`
- **Flow to Payment Failed End (Default)**: default flow

