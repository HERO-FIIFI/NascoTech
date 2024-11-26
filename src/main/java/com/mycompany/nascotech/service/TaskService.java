package com.mycompany.nascotech.service;
import com.mycompany.nascotech.models.PaymentTransaction;

public class TaskService {

    private static class Mono<T> {

        public Mono() {
        }
    }
    public interface PaymentService {
        Mono<PaymentTransaction> retrieveFinancialTransaction(String paymentId);
    }
}