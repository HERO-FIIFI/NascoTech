package com.nascotech.models;


public class PaymentTransaction {
    private final FinancialTransaction transaction;
    private final Payment payment;


    public FinancialTransaction getTransaction() {
        return transaction;
    }

    public Payment getPayment() {
        return payment;
    }

    public PaymentTransaction(FinancialTransaction transaction, Payment payment) {
        this.transaction = transaction;
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "PaymentTransaction{" +
                "transaction=" + transaction +
                ", payment=" + payment +
                '}';
    }
}