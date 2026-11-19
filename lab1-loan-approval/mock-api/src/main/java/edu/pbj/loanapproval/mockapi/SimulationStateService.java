package edu.pbj.loanapproval.mockapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service to manage mock API simulation states (failure mode and timeout delay)
 * without requiring application restart.
 */
@Service
public class SimulationStateService {

    private static final Logger log = LoggerFactory.getLogger(SimulationStateService.class);

    private final AtomicBoolean failureModeEnabled = new AtomicBoolean(false);
    private final AtomicInteger timeoutSeconds = new AtomicInteger(0);

    public boolean isFailureModeEnabled() {
        return failureModeEnabled.get();
    }

    public void setFailureModeEnabled(boolean enabled) {
        failureModeEnabled.set(enabled);
        log.warn("Mock API Failure Mode state changed: enabled={}", enabled);
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds.get();
    }

    public void setTimeoutSeconds(int seconds) {
        timeoutSeconds.set(seconds);
        log.warn("Mock API Timeout Simulation delay set to {} seconds", seconds);
    }

    public void applyDelayIfConfigured() {
        int delay = timeoutSeconds.get();
        if (delay > 0) {
            log.info("Simulating artificial API delay of {} seconds...", delay);
            try {
                Thread.sleep(delay * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Simulation sleep was interrupted", e);
            }
        }
    }
}

