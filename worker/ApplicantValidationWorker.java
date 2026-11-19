package edu.pbj.loanapproval.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class ApplicantValidationWorker {

    private static final Logger log = LoggerFactory.getLogger(ApplicantValidationWorker.class);
    private final WorkerConfigService configService;

    public ApplicantValidationWorker(WorkerConfigService configService) {
        this.configService = configService;
    }

    @JobWorker(type = "validate-applicant", autoComplete = false)
    public void validateApplicant(
            final JobClient client,
            final ActivatedJob job,
            @Variable(name = "applicantId") String applicantId,
            @Variable(name = "applicantName") String applicantName,
            @Variable(name = "age") Integer age,
            @Variable(name = "employmentStatus") String employmentStatus,
            @Variable(name = "requestedAmount") Double requestedAmount,
            @Variable(name = "monthlyIncome") Double monthlyIncome,
            @Variable(name = "existingMonthlyDebt") Double existingMonthlyDebt,
            @Variable(name = "loanTermMonths") Integer loanTermMonths
    ) {
        log.info("Processing job {} (validate-applicant) for applicantId={}, requestedAmount={}",
                job.getKey(), applicantId, requestedAmount);

        // 1. Check for deliberate failure configuration flag
        if (configService.isDeliberateFailureEnabled()) {
            String errorMsg = "DELIBERATE_WORKER_FAILURE: Simulated worker configuration fault in Lab 1. " +
                    "To recover, set failure-mode to false via /worker-config/failure-mode and retry the job in Camunda Operate.";
            log.error("Job {}: {}", job.getKey(), errorMsg);
            client.newFailCommand(job.getKey())
                    .retries(0)
                    .errorMessage(errorMsg)
                    .send()
                    .join();
            return;
        }

        List<String> errors = new ArrayList<>();

        if (applicantId == null || applicantId.trim().isEmpty()) {
            errors.add("applicantId cannot be null or empty");
        }
        if (applicantName == null || applicantName.trim().isEmpty()) {
            errors.add("applicantName cannot be null or empty");
        }
        if (age == null || age < 18 || age > 75) {
            errors.add("applicant age must be between 18 and 75 (received: " + age + ")");
        }
        if (employmentStatus == null || employmentStatus.trim().isEmpty()) {
            errors.add("employmentStatus is required");
        }
        if (requestedAmount == null || requestedAmount <= 0) {
            errors.add("requestedAmount must be greater than zero");
        }
        if (monthlyIncome == null || monthlyIncome <= 0) {
            errors.add("monthlyIncome must be greater than zero");
        }
        if (existingMonthlyDebt == null || existingMonthlyDebt < 0) {
            errors.add("existingMonthlyDebt cannot be negative");
        }
        if (loanTermMonths == null || loanTermMonths < 6 || loanTermMonths > 360) {
            errors.add("loanTermMonths must be between 6 and 360 (received: " + loanTermMonths + ")");
        }

        boolean isValid = errors.isEmpty();
        double dti = 0.0;
        double incomeToLoanRatio = 0.0;

        if (isValid) {
            dti = BigDecimal.valueOf(existingMonthlyDebt / monthlyIncome)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            incomeToLoanRatio = BigDecimal.valueOf((monthlyIncome * loanTermMonths) / requestedAmount)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        Map<String, Object> outputVariables = new HashMap<>();
        outputVariables.put("isValid", isValid);
        outputVariables.put("validationErrors", errors);
        outputVariables.put("debtToIncomeRatio", dti);
        outputVariables.put("incomeToLoanRatio", incomeToLoanRatio);

        log.info("Applicant validation finished for job {}: isValid={}, errors={}, dti={}",
                job.getKey(), isValid, errors, dti);

        client.newCompleteCommand(job.getKey())
                .variables(outputVariables)
                .send()
                .join();
    }
}

