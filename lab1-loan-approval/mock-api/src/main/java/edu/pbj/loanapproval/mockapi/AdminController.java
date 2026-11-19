package edu.pbj.loanapproval.mockapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "Administration", description = "Endpoints to toggle deliberate failure modes and timeout simulations for incident testing")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final SimulationStateService simulationStateService;

    public AdminController(SimulationStateService simulationStateService) {
        this.simulationStateService = simulationStateService;
    }

    @PostMapping("/failure-mode")
    @Operation(summary = "Toggle failure mode", description = "When enabled, credit assessment returns HTTP 500 error to generate genuine incident in Zeebe/Operate.")
    public ResponseEntity<Map<String, Object>> toggleFailureMode(@RequestBody FailureModeRequest request) {
        simulationStateService.setFailureModeEnabled(request.isEnabled());
        log.warn("ADMIN ACTION: Failure mode set to {}", request.isEnabled());
        return ResponseEntity.ok(Map.of(
                "failureModeEnabled", simulationStateService.isFailureModeEnabled(),
                "message", request.isEnabled()
                        ? "Failure mode ACTIVATED. Subsequent credit assessment requests will fail with HTTP 500."
                        : "Failure mode DEACTIVATED. Credit assessment requests will succeed."
        ));
    }

    @PostMapping("/timeout-mode")
    @Operation(summary = "Toggle timeout simulation", description = "Introduce artificial delays in requests to test connector timeouts.")
    public ResponseEntity<Map<String, Object>> toggleTimeoutMode(@RequestBody TimeoutModeRequest request) {
        int delay = request.isEnabled() ? (request.getDelaySeconds() > 0 ? request.getDelaySeconds() : 10) : 0;
        simulationStateService.setTimeoutSeconds(delay);
        log.warn("ADMIN ACTION: Timeout delay set to {} seconds", delay);
        return ResponseEntity.ok(Map.of(
                "timeoutDelaySeconds", delay,
                "message", delay > 0
                        ? "Timeout simulation ACTIVATED with delay of " + delay + "s."
                        : "Timeout simulation DEACTIVATED."
        ));
    }

    @GetMapping("/status")
    @Operation(summary = "Get simulation status", description = "Check whether failure mode or timeout simulation are currently active.")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "failureModeEnabled", simulationStateService.isFailureModeEnabled(),
                "timeoutDelaySeconds", simulationStateService.getTimeoutSeconds()
        ));
    }
}

