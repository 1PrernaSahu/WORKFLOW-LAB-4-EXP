package edu.pbj.loanapproval.mockapi;

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

@RestController
@RequestMapping("/credit")
@Tag(name = "Credit Assessment", description = "Mock API for synthetic credit risk evaluation")
public class CreditAssessmentController {

    private static final Logger log = LoggerFactory.getLogger(CreditAssessmentController.class);
    private final SimulationStateService simulationStateService;

    public CreditAssessmentController(SimulationStateService simulationStateService) {
        this.simulationStateService = simulationStateService;
    }

    @PostMapping("/assess")
    @Operation(summary = "Assess applicant credit risk", description = "Generates synthetic credit score and risk band based on applicant financial profile.")
    public ResponseEntity<?> assessCredit(@Valid @RequestBody CreditAssessmentRequest request) {
        log.info("Received credit assessment request for applicantId={}, requestedAmount={}, monthlyIncome={}, employment={}",
                request.getApplicantId(), request.getRequestedAmount(), request.getMonthlyIncome(), request.getEmploymentStatus());

        // 1. Check artificial delay simulation
        simulationStateService.applyDelayIfConfigured();

        // 2. Check failure mode simulation
        if (simulationStateService.isFailureModeEnabled()) {
            log.error("FAILURE MODE TRIGGERED: Simulating HTTP 500 downstream failure for applicantId={}", request.getApplicantId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "ERROR",
                    "errorCode", "CREDIT_BUREAU_DOWNSTREAM_UNAVAILABLE",
                    "message", "Simulated credit bureau server outage. Disable failure-mode via /admin/failure-mode to restore service.",
                    "timestamp", Instant.now().toString()
            ));
        }

        // 3. Compute deterministic synthetic credit score
        int score = calculateSyntheticScore(request);
        String riskBand;
        if (score >= 740) {
            riskBand = "LOW";
        } else if (score >= 640) {
            riskBand = "MEDIUM";
        } else {
            riskBand = "HIGH";
        }

        CreditAssessmentResponse response = new CreditAssessmentResponse(
                request.getApplicantId(),
                score,
                riskBand,
                Instant.now().toString(),
                "COMPLETED"
        );

        log.info("Credit assessment completed: applicantId={}, score={}, riskBand={}",
                request.getApplicantId(), score, riskBand);

        return ResponseEntity.ok(response);
    }

    private int calculateSyntheticScore(CreditAssessmentRequest request) {
        // Deterministic synthetic calculation based on applicant profile
        String id = request.getApplicantId();
        if (id != null) {
            if (id.contains("PASS") || id.contains("001") || id.contains("HIGH")) {
                return 760;
            }
            if (id.contains("FAIL") || id.contains("002") || id.contains("LOW")) {
                return 540;
            }
            if (id.contains("MANUAL") || id.contains("003") || id.contains("BORDER")) {
                return 650;
            }
        }

        // General calculation based on income and employment
        int base = 600;
        if ("EMPLOYED".equalsIgnoreCase(request.getEmploymentStatus())) {
            base += 60;
        } else if ("SELF_EMPLOYED".equalsIgnoreCase(request.getEmploymentStatus())) {
            base += 30;
        } else if ("UNEMPLOYED".equalsIgnoreCase(request.getEmploymentStatus())) {
            base -= 70;
        }

        if (request.getMonthlyIncome() > 6000) {
            base += 80;
        } else if (request.getMonthlyIncome() > 3000) {
            base += 40;
        } else {
            base -= 30;
        }

        return Math.min(850, Math.max(300, base));
    }
}

