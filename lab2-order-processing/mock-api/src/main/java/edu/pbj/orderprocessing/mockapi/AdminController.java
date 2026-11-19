package edu.pbj.orderprocessing.mockapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "Administration", description = "Mock Payment Gateway administration and incident testing controls")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final PaymentSimulationService simulationService;

    public AdminController(PaymentSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/failure-mode")
    @Operation(summary = "Toggle payment gateway failure mode", description = "When enabled, payment charge requests return HTTP 503 Service Unavailable to trigger incidents.")
    public ResponseEntity<Map<String, Object>> toggleFailureMode(@RequestBody Map<String, Boolean> body) {
        boolean enabled = body.getOrDefault("enabled", false);
        simulationService.setFailureModeEnabled(enabled);
        log.warn("ADMIN ACTION: Payment failure mode set to {}", enabled);
        return ResponseEntity.ok(Map.of(
                "failureModeEnabled", simulationService.isFailureModeEnabled(),
                "message", enabled
                        ? "Failure mode ACTIVATED. Payment gateway will return HTTP 503."
                        : "Failure mode DEACTIVATED. Payment gateway operating normally."
        ));
    }

    @PostMapping("/timeout-mode")
    @Operation(summary = "Toggle payment timeout simulation", description = "Introduce artificial processing delays.")
    public ResponseEntity<Map<String, Object>> toggleTimeoutMode(@RequestBody Map<String, Object> body) {
        boolean enabled = Boolean.TRUE.equals(body.get("enabled"));
        int delay = enabled ? Integer.parseInt(body.getOrDefault("delaySeconds", 10).toString()) : 0;
        simulationService.setTimeoutSeconds(delay);
        log.warn("ADMIN ACTION: Payment timeout set to {} seconds", delay);
        return ResponseEntity.ok(Map.of(
                "timeoutDelaySeconds", delay,
                "message", delay > 0 ? "Timeout simulation ACTIVATED with delay of " + delay + "s." : "Timeout simulation DEACTIVATED."
        ));
    }

    @GetMapping("/status")
    @Operation(summary = "Get simulation status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "failureModeEnabled", simulationService.isFailureModeEnabled(),
                "timeoutDelaySeconds", simulationService.getTimeoutSeconds()
        ));
    }
}

