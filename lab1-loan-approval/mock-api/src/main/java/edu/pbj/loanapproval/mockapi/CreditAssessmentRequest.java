package edu.pbj.loanapproval.mockapi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreditAssessmentRequest {

    @NotBlank(message = "applicantId is required")
    private String applicantId;

    @NotNull(message = "monthlyIncome is required")
    @Positive(message = "monthlyIncome must be greater than 0")
    private Double monthlyIncome;

    @NotNull(message = "requestedAmount is required")
    @Positive(message = "requestedAmount must be greater than 0")
    private Double requestedAmount;

    @NotBlank(message = "employmentStatus is required")
    private String employmentStatus;

    public CreditAssessmentRequest() {}

    public CreditAssessmentRequest(String applicantId, Double monthlyIncome, Double requestedAmount, String employmentStatus) {
        this.applicantId = applicantId;
        this.monthlyIncome = monthlyIncome;
        this.requestedAmount = requestedAmount;
        this.employmentStatus = employmentStatus;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public Double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public Double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(Double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
}

