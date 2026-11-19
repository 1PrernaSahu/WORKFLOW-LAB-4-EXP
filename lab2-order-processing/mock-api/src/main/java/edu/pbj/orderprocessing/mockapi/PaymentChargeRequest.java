package edu.pbj.orderprocessing.mockapi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentChargeRequest {

    @NotBlank(message = "customerRef is required")
    private String customerRef;

    @NotNull(message = "orderTotal is required")
    @Positive(message = "orderTotal must be positive")
    private Double orderTotal;

    @NotBlank(message = "paymentToken is required")
    private String paymentToken;

    public PaymentChargeRequest() {}

    public PaymentChargeRequest(String customerRef, Double orderTotal, String paymentToken) {
        this.customerRef = customerRef;
        this.orderTotal = orderTotal;
        this.paymentToken = paymentToken;
    }

    public String getCustomerRef() {
        return customerRef;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getPaymentToken() {
        return paymentToken;
    }

    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }
}

