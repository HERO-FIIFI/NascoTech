package com.nascotech.controller;

import com.nascotech.Utils.Link;
import com.nascotech.dao.FinancialTransactionRepository;
import com.nascotech.models.DataListPaymentResponse;
import com.nascotech.models.FinancialTransaction;
import com.nascotech.models.Payment;
import com.nascotech.models.PaymentResponse;
import com.nascotech.service.TaskService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class FinancialTransactionController {

    private final FinancialTransactionRepository transactionRepository;
    private final TaskService taskService;

    public FinancialTransactionController(FinancialTransactionRepository transactionRepository,
                                          TaskService taskService) {
        this.transactionRepository = transactionRepository;
        this.taskService = taskService;
    }

    @GetMapping("/filter")
    public Mono<ResponseEntity<DataListPaymentResponse>> getFilteredTransactions(
            @RequestParam(value = "dateFrom", required = false) LocalDate dateFrom,
            @RequestParam(value = "dateTo", required = false) LocalDate dateTo,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "service", required = false) String service,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "reference", required = false) String reference,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return transactionRepository.findTransactions(dateFrom, dateTo, userId, service, status, reference, pageable)
                .flatMapMany(Page::getContent)
                .flatMap(transaction ->
                        taskService.retrieveFinancialTransaction(transaction.getPaymentId())
                                .map(payment -> mapToPaymentResponse(transaction, payment)))
                .sort(Comparator.comparing(PaymentResponse::getPaymentId).reversed())
                .collectList()
                .flatMap(sortedPayments -> {
                    DataListPaymentResponse response = new DataListPaymentResponse();
                    response.setPayments(sortedPayments);
                    response.setLinks(createHateoasLinks(page, size, sortedPayments.size()));
                    return Mono.just(ResponseEntity.ok(response));
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    // Consider using a logging framework instead of a hypothetical LoggingUtil
                    return Mono.just(ResponseEntity.status(ex.getStatusCode())
                            .body(new DataListPaymentResponse("Error: " + ex.getMessage())));
                });
    }

    private PaymentResponse mapToPaymentResponse(FinancialTransaction transaction, Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setTransactionId(transaction.getId());
        response.setPaymentId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setStatus(transaction.getStatus());
        return response;
    }

    private List<Link> createHateoasLinks(int page, int size, int totalItems) {
        List<Link> links = new ArrayList<>();

        links.add(new Link("self", "/api/transactions/filter?page=" + page + "&size=" + size));
        if (totalItems == size) {
            links.add(new Link("next", "/api/transactions/filter?page=" + (page + 1) + "&size=" + size));
        }
        if (page > 0) {
            links.add(new Link("prev", "/api/transactions/filter?page=" + (page - 1) + "&size=" + size));
        }

        return links;
    }
}