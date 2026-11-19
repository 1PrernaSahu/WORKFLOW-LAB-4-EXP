package edu.pbj.orderprocessing.mockapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payment Gateway", description = "Mock Payment Processing API with strict synthetic token enforcement")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    private static final Pattern REAL_CARD_PATTERN = Pattern.compile("^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|6(?:011|5[0-9]{2})[0-9]{12}|(?:2131|1800|35\\d{3})\\d{11}|\\d{13,19})$");

    private final PaymentSimulationService simulationService;

    public PaymentController(PaymentSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/charge")
    @Operation(summary = "Process credit token payment", description = "Charges the synthetic payment token for the specified order total.")
    public ResponseEntity<?> chargePayment(@Valid @RequestBody PaymentChargeRequest request) {
        log.info("Processing payment charge: customerRef={}, amount={}, token={}",
                request.getCustomerRef(), request.getOrderTotal(), request.getPaymentToken());

        // 1. Check artificial timeout simulation
        simulationService.applyDelayIfConfigured();

        // 2. Check failure mode simulation
        if (simulationService.isFailureModeEnabled()) {
            log.error("PAYMENT GATEWAY FAILURE SIMULATION: Returning HTTP 503 Service Unavailable");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                    "status", "FAILED",
                    "errorCode", "PAYMENT_GATEWAY_OUTAGE",
                    "message", "Payment processing gateway unavailable. Disable failure-mode via /admin/failure-mode to restore.",
                    "timestamp", Instant.now().toString()
            ));
        }

        // 3. Strict security check: reject real card credentials or non-synthetic tokens
        String token = request.getPaymentToken().trim();
        if (REAL_CARD_PATTERN.matcher(token.replaceAll("[\\s-]", "")).matches()) {
            log.error("SECURITY REJECTION: Potential real card number detected in paymentToken! Real credentials forbidden.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "REJECTED",
                    "errorCode", "REAL_PAYMENT_CREDENTIALS_FORBIDDEN",
                    "message", "Real card numbers are strictly prohibited. Use synthetic test tokens: tok_test_success or tok_test_declined.",
                    "timestamp", Instant.now().toString()
            ));
        }

        if (!token.startsWith("tok_test_")) {
            log.error("INVALID TOKEN: Token must begin with tok_test_ (received: {})", token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "REJECTED",
                    "errorCode", "INVALID_TEST_TOKEN",
                    "message", "Payment token must be a valid synthetic test token starting with tok_test_.",
                    "timestamp", Instant.now().toString()
            ));
        }

        // 4. Evaluate synthetic payment token outcome
        if ("tok_test_declined".equalsIgnoreCase(token)) {
            String txnId = "TXN-DECLINED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            log.warn("Payment DECLINED for customerRef={}, amount={}, txnId={}", request.getCustomerRef(), request.getOrderTotal(), txnId);
            PaymentChargeResponse response = new PaymentChargeResponse(
                    "DECLINED",
                    txnId,
                    request.getOrderTotal(),
                    Instant.now().toString(),
                    "Card declined by simulated issuing bank (insufficient test funds)"
            );
            return ResponseEntity.ok(response);
        }

        // Default or tok_test_success
        String txnId = "TXN-SYN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("Payment APPROVED for customerRef={}, amount={}, txnId={}", request.getCustomerRef(), request.getOrderTotal(), txnId);
        PaymentChargeResponse response = new PaymentChargeResponse(
                "SUCCESS",
                txnId,
                request.getOrderTotal(),
                Instant.now().toString(),
                "Payment authorized and charged successfully"
        );
        return ResponseEntity.ok(response);
    }
}

