package com.nascotech.models;

import java.math.BigDecimal;

public class Payment {
    private String PaymentId;
    private BigDecimal amount;

    public String getPaymentId() {
        return PaymentId;
    }

    public void setPaymentId(String paymentId) {
        PaymentId = paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Payment(String paymentId, BigDecimal amount) {
        PaymentId = paymentId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "PaymentId='" + PaymentId + '\'' +
                ", amount=" + amount +
                '}';
    }
}
