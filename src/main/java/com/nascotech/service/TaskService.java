package com.nascotech.service;
import com.nascotech.models.Payment;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Configuration
@Service
public class TaskService {

        private final PaymentService paymentService;

        public TaskService(PaymentService paymentService) {
            this.paymentService = paymentService;
        }

        public Mono<Payment> retrieveFinancialTransaction(String paymentId) {
            return paymentService.retrieveFinancialTransaction(paymentId);
        }

        public interface PaymentService {
            Mono<Payment> retrieveFinancialTransaction(String paymentId);
        }

        public static class PaymentServiceImpl implements PaymentService {
            @Override
            public Mono<Payment> retrieveFinancialTransaction(String paymentId) {

                Payment payment = new Payment();
                payment.setPaymentId(paymentId);

                return Mono.just(payment);
            }
        }
    }