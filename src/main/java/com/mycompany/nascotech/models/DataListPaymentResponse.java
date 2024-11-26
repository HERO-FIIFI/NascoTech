package com.mycompany.nascotech.models;


import java.util.List;

public class DataListPaymentResponse {
    private List<PaymentTransaction> payments;
    private String error;

    public DataListPaymentResponse(List<PaymentTransaction> payments) {
        this.payments = payments;
    }

    public DataListPaymentResponse(String error) {
        this.error = error;
    }

    public List<PaymentTransaction> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentTransaction> payments) {
        this.payments = payments;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}