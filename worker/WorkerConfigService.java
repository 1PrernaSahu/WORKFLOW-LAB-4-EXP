package edu.pbj.hospitaltriage.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class WorkerConfigService {

    private static final Logger log = LoggerFactory.getLogger(WorkerConfigService.class);
    private final AtomicBoolean deliberateFailureEnabled;

    public WorkerConfigService(@Value("${worker.deliberate-failure-enabled:false}") boolean initialFailure) {
        this.deliberateFailureEnabled = new AtomicBoolean(initialFailure);
        log.info("Initialized Hospital WorkerConfigService with deliberateFailureEnabled={}", initialFailure);
    }

    public boolean isDeliberateFailureEnabled() {
        return deliberateFailureEnabled.get();
    }

    public void setDeliberateFailureEnabled(boolean enabled) {
        deliberateFailureEnabled.set(enabled);
        log.warn("HOSPITAL WORKER CONFIG CHANGED: deliberateFailureEnabled={}", enabled);
    }
}

