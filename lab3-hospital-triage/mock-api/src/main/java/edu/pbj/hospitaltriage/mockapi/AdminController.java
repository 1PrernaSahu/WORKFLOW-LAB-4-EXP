package edu.pbj.hospitaltriage.mockapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "Administration", description = "Mock Clinic Scheduling administration and incident testing controls")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private final SchedulingSimulationService simulationService;

    public AdminController(SchedulingSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/failure-mode")
    @Operation(summary = "Toggle scheduling API failure mode", description = "Returns HTTP 500 downstream error to test incident generation.")
    public ResponseEntity<Map<String, Object>> toggleFailureMode(@RequestBody Map<String, Boolean> body) {
        boolean enabled = body.getOrDefault("enabled", false);
        simulationService.setFailureModeEnabled(enabled);
        log.warn("ADMIN ACTION: Scheduling failure mode set to {}", enabled);
        return ResponseEntity.ok(Map.of(
                "failureModeEnabled", simulationService.isFailureModeEnabled(),
                "message", enabled
                        ? "Failure mode ACTIVATED. Scheduling API will return HTTP 500 error."
                        : "Failure mode DEACTIVATED. Scheduling API operating normally."
        ));
    }

    @PostMapping("/timeout-mode")
    @Operation(summary = "Toggle scheduling timeout simulation", description = "Introduce artificial processing delays.")
    public ResponseEntity<Map<String, Object>> toggleTimeoutMode(@RequestBody Map<String, Object> body) {
        boolean enabled = Boolean.TRUE.equals(body.get("enabled"));
        int delay = enabled ? Integer.parseInt(body.getOrDefault("delaySeconds", 10).toString()) : 0;
        simulationService.setTimeoutSeconds(delay);
        log.warn("ADMIN ACTION: Scheduling timeout set to {} seconds", delay);
        return ResponseEntity.ok(Map.of(
                "timeoutDelaySeconds", delay,
                "message", delay > 0 ? "Timeout simulation ACTIVATED (" + delay + "s)." : "Timeout simulation DEACTIVATED."
        ));
    }

    @PostMapping("/slots-availability")
    @Operation(summary = "Toggle slot availability", description = "Toggle whether appointment slots are available or fully booked.")
    public ResponseEntity<Map<String, Object>> toggleSlots(@RequestBody Map<String, Boolean> body) {
        boolean available = body.getOrDefault("available", true);
        simulationService.setSlotsAvailable(available);
        log.info("ADMIN ACTION: Slots availability set to {}", available);
        return ResponseEntity.ok(Map.of(
                "slotsAvailable", simulationService.areSlotsAvailable(),
                "message", available
                        ? "Slots are now AVAILABLE for routine scheduling."
                        : "All slots FULL. Routine requests will route to Staff Slot Review user task."
        ));
    }

    @GetMapping("/status")
    @Operation(summary = "Get simulation status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "failureModeEnabled", simulationService.isFailureModeEnabled(),
                "timeoutDelaySeconds", simulationService.getTimeoutSeconds(),
                "slotsAvailable", simulationService.areSlotsAvailable()
        ));
    }
}

