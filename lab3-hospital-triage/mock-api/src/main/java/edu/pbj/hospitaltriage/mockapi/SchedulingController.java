package edu.pbj.hospitaltriage.mockapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/scheduling")
@Tag(name = "Clinic Scheduling", description = "Mock Clinic Appointment Scheduling Service (Educational Only - Not Real Medical Advice)")
public class SchedulingController {

    private static final Logger log = LoggerFactory.getLogger(SchedulingController.class);
    private final SchedulingSimulationService simulationService;

    public SchedulingController(SchedulingSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping("/slots")
    @Operation(summary = "Check available appointment slots", description = "Returns available booking slots based on preference (MORNING, AFTERNOON, EVENING).")
    public ResponseEntity<?> getSlots(@RequestParam(name = "preference", defaultValue = "MORNING") String preference) {
        log.info("Querying available appointment slots for preference={}", preference);

        // 1. Timeout simulation
        simulationService.applyDelayIfConfigured();

        // 2. Failure mode simulation
        if (simulationService.isFailureModeEnabled()) {
            log.error("SCHEDULING API FAILURE SIMULATION: Returning HTTP 500");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "ERROR",
                    "errorCode", "SCHEDULING_SYSTEM_DATABASE_OFFLINE",
                    "message", "Clinic appointment reservation database connection timed out. Disable failure-mode via /admin/failure-mode to restore.",
                    "timestamp", Instant.now().toString()
            ));
        }

        // 3. Slot availability check
        if (!simulationService.areSlotsAvailable()) {
            log.info("No slots available currently for preference={}", preference);
            SlotAvailabilityResponse response = new SlotAvailabilityResponse(
                    false,
                    null,
                    null,
                    "General Outpatient Clinic",
                    Collections.emptyList(),
                    Instant.now().toString()
            );
            return ResponseEntity.ok(response);
        }

        // Slots available
        String slotId = "SLOT-SYN-" + preference.toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String timeStr = switch (preference.toUpperCase()) {
            case "AFTERNOON" -> "14:30 PM";
            case "EVENING" -> "18:00 PM";
            default -> "09:30 AM";
        };

        List<String> available = List.of(timeStr, "11:00 AM", "15:45 PM");

        SlotAvailabilityResponse response = new SlotAvailabilityResponse(
                true,
                slotId,
                timeStr,
                "General Outpatient Clinic",
                available,
                Instant.now().toString()
        );

        log.info("Slots available: recommendedSlotId={}, time={}", slotId, timeStr);
        return ResponseEntity.ok(response);
    }
}

