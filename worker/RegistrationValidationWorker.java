package edu.pbj.hospitaltriage.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RegistrationValidationWorker {

    private static final Logger log = LoggerFactory.getLogger(RegistrationValidationWorker.class);
    private final WorkerConfigService configService;

    public RegistrationValidationWorker(WorkerConfigService configService) {
        this.configService = configService;
    }

    @JobWorker(type = "validate-registration", autoComplete = false)
    public void validateRegistration(
            final JobClient client,
            final ActivatedJob job,
            @Variable(name = "patientRef") String patientRef,
            @Variable(name = "appointmentPreference") String appointmentPreference,
            @Variable(name = "symptomsSummary") String symptomsSummary,
            @Variable(name = "chestPain") Boolean chestPain,
            @Variable(name = "breathingDifficulty") Boolean breathingDifficulty,
            @Variable(name = "feverDays") Integer feverDays,
            @Variable(name = "painScore") Integer painScore
    ) {
        log.info("Processing job {} (validate-registration) for patientRef={}, preference={}",
                job.getKey(), patientRef, appointmentPreference);

        // 1. Deliberate failure flag
        if (configService.isDeliberateFailureEnabled()) {
            String errorMsg = "DELIBERATE_HOSPITAL_WORKER_FAILURE: Simulated registration database outage for Lab 3 incident demonstration.";
            log.error("Job {}: {}", job.getKey(), errorMsg);
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage(errorMsg)
                    .send()
                    .join();
            return;
        }

        // 2. Validate inputs
        boolean isValid = true;
        StringBuilder notes = new StringBuilder();

        if (patientRef == null || !patientRef.startsWith("SYN-PAT-")) {
            isValid = false;
            notes.append("patientRef must be synthetic starting with 'SYN-PAT-'. ");
        }
        if (symptomsSummary == null || symptomsSummary.trim().isEmpty()) {
            isValid = false;
            notes.append("symptomsSummary cannot be blank. ");
        }

        // Normalize urgency indicators
        boolean normChestPain = Boolean.TRUE.equals(chestPain);
        boolean normBreathingDifficulty = Boolean.TRUE.equals(breathingDifficulty);
        int normFeverDays = (feverDays != null && feverDays >= 0) ? feverDays : 0;
        int normPainScore = (painScore != null && painScore >= 1 && painScore <= 10) ? painScore : 1;

        Map<String, Object> output = new HashMap<>();
        output.put("isRegistrationValid", isValid);
        output.put("validationNotes", notes.length() > 0 ? notes.toString().trim() : "Registration validated successfully.");
        output.put("chestPain", normChestPain);
        output.put("breathingDifficulty", normBreathingDifficulty);
        output.put("feverDays", normFeverDays);
        output.put("painScore", normPainScore);

        log.info("Registration validation completed for job {}: isValid={}, notes={}",
                job.getKey(), isValid, output.get("validationNotes"));

        client.newCompleteCommand(job.getKey())
                .variables(output)
                .send()
                .join();
    }
}

