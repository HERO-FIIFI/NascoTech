package com.nascotech.service;
import com.nascotech.models.PaymentTransaction;
import reactor.core.publisher.Mono;
public class TaskService {


    public interface PaymentService {
        Mono<PaymentTransaction> retrieveFinancialTransaction(String PaymentId);
    }

    public static class PaymentServiceImpl implements PaymentService {
        @Override
        public Mono<PaymentTransaction> retrieveFinancialTransaction(String PaymentId) {
            PaymentTransaction.Payment payment = new PaymentTransaction.Payment(PaymentId);
            PaymentTransaction transaction = new PaymentTransaction(payment);

            return Mono.just(transaction);

        }
}
}