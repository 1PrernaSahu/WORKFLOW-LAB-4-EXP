package edu.pbj.orderprocessing.mockapi;

public class PaymentChargeResponse {

    private String paymentStatus;
    private String transactionId;
    private Double amountCharged;
    private String timestamp;
    private String responseMessage;

    public PaymentChargeResponse() {}

    public PaymentChargeResponse(String paymentStatus, String transactionId, Double amountCharged, String timestamp, String responseMessage) {
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.amountCharged = amountCharged;
        this.timestamp = timestamp;
        this.responseMessage = responseMessage;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Double getAmountCharged() {
        return amountCharged;
    }

    public void setAmountCharged(Double amountCharged) {
        this.amountCharged = amountCharged;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}

