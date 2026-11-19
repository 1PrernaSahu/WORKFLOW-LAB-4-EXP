package edu.pbj.hospitaltriage.worker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/worker-config")
public class WorkerConfigController {

    private final WorkerConfigService configService;

    public WorkerConfigController(WorkerConfigService configService) {
        this.configService = configService;
    }

    @GetMapping("/failure-mode")
    public ResponseEntity<Map<String, Object>> getFailureMode() {
        return ResponseEntity.ok(Map.of(
                "deliberateFailureEnabled", configService.isDeliberateFailureEnabled()
        ));
    }

    @PostMapping("/failure-mode")
    public ResponseEntity<Map<String, Object>> setFailureMode(@RequestBody Map<String, Boolean> body) {
        boolean enabled = body.getOrDefault("enabled", false);
        configService.setDeliberateFailureEnabled(enabled);
        return ResponseEntity.ok(Map.of(
                "deliberateFailureEnabled", configService.isDeliberateFailureEnabled(),
                "message", enabled
                        ? "Deliberate registration worker failure ENABLED."
                        : "Deliberate registration worker failure DISABLED."
        ));
    }
}

