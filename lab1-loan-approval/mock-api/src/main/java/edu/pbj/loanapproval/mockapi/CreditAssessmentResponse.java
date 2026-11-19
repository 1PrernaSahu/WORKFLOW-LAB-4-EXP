package edu.pbj.loanapproval.mockapi;

public class CreditAssessmentResponse {

    private String applicantId;
    private int creditScore;
    private String riskBand;
    private String assessmentTimestamp;
    private String status;

    public CreditAssessmentResponse() {}

    public CreditAssessmentResponse(String applicantId, int creditScore, String riskBand, String assessmentTimestamp, String status) {
        this.applicantId = applicantId;
        this.creditScore = creditScore;
        this.riskBand = riskBand;
        this.assessmentTimestamp = assessmentTimestamp;
        this.status = status;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public String getRiskBand() {
        return riskBand;
    }

    public void setRiskBand(String riskBand) {
        this.riskBand = riskBand;
    }

    public String getAssessmentTimestamp() {
        return assessmentTimestamp;
    }

    public void setAssessmentTimestamp(String assessmentTimestamp) {
        this.assessmentTimestamp = assessmentTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

