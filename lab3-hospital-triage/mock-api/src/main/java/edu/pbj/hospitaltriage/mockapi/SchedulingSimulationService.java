package edu.pbj.hospitaltriage.mockapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SchedulingSimulationService {

    private static final Logger log = LoggerFactory.getLogger(SchedulingSimulationService.class);

    private final AtomicBoolean failureModeEnabled = new AtomicBoolean(false);
    private final AtomicInteger timeoutSeconds = new AtomicInteger(0);
    private final AtomicBoolean slotsAvailable = new AtomicBoolean(true);

    public boolean isFailureModeEnabled() {
        return failureModeEnabled.get();
    }

    public void setFailureModeEnabled(boolean enabled) {
        failureModeEnabled.set(enabled);
        log.warn("Scheduling Mock API Failure Mode set to {}", enabled);
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds.get();
    }

    public void setTimeoutSeconds(int seconds) {
        timeoutSeconds.set(seconds);
        log.warn("Scheduling Mock API Timeout delay set to {} seconds", seconds);
    }

    public boolean areSlotsAvailable() {
        return slotsAvailable.get();
    }

    public void setSlotsAvailable(boolean available) {
        slotsAvailable.set(available);
        log.info("Scheduling Mock API Slots Availability set to {}", available);
    }

    public void applyDelayIfConfigured() {
        int delay = timeoutSeconds.get();
        if (delay > 0) {
            log.info("Simulating artificial scheduling API delay of {} seconds...", delay);
            try {
                Thread.sleep(delay * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Simulation sleep interrupted", e);
            }
        }
    }
}

