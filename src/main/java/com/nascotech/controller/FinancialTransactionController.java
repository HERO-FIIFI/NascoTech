package com.nascotech.controller;

import com.nascotech.Utils.Link;
import com.nascotech.dao.FinancialTransactionRepository;
import com.nascotech.models.DataListPaymentResponse;
import com.nascotech.models.FinancialTransaction;
import com.nascotech.models.Payment;
import com.nascotech.models.PaymentResponse;
import com.nascotech.service.TaskService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static java.util.Collections.sort;

@RestController
@RequestMapping("/api/transactions")
public class FinancialTransactionController {
    private static final Logger logger = LoggerFactory.getLogger(FinancialTransactionController.class);

    private final FinancialTransactionRepository transactionRepository;
    private final TaskService taskService;

    // Remove @Autowired and use constructor injection
    public FinancialTransactionController(
            FinancialTransactionRepository transactionRepository,
            TaskService taskService
    ) {
        this.transactionRepository = transactionRepository;
        this.taskService = taskService;
        logger.info("FinancialTransactionController initialized");
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

        logger.info("Fetching filtered transactions: dateFrom={}, dateTo={}, userId={}, service={}, status={}, reference={}, page={}, size={}",
                dateFrom, dateTo, userId, service, status, reference, page, size);

        Pageable pageable = PageRequest.of(page, size);

        return transactionRepository.findTransactions(dateFrom, dateTo, userId, service, status, reference, pageable)
                .flatMapMany(Page::getContent)
                .flatMap(transaction -> {
                    logger.debug("Processing transaction with paymentId: {}", transaction.getPaymentId());

                    taskService.retrieveFinancialTransaction(transaction.getPaymentId())
                            .map(payment -> {
                                logger.debug("Mapping transaction {} with payment {}", transaction.getId(), payment.getPaymentId());

                                PaymentResponse response = new PaymentResponse();
                                response.setTransactionId(transaction.getId());
                                response.setPaymentId(payment.getPaymentId());
                                response.setAmount(payment.getAmount());
                                response.setStatus(transaction.getStatus());
                                return response;
                            })
                            .onErrorResume(ex -> {
                                logger.error("Error retrieving financial transaction for paymentId: {}",
                                        transaction.getPaymentId(), ex);
                                return Mono.empty();
                            })

                .sort(Comparator.comparing(PaymentResponse::getPaymentId).reversed())
                .collectList()
                .flatMap(sortedPayments -> {
                    logger.info("Processed {} payments", sortedPayments.size());

                    DataListPaymentResponse response = new DataListPaymentResponse();
                    response.payments = sortedPayments;
                    response.setLinks(createHateoasLinks(page, size, sortedPayments.size()));

                    return Mono.just(ResponseEntity.ok(response));
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    logger.error("WebClient error while fetching transactions");
                    return Mono.just(ResponseEntity.status(ex.getStatusCode())
                            .body(new DataListPaymentResponse("Error: " + ex.getMessage())));
                })
                            .onErrorResume(ex -> {
                                logger.error("Error in transaction filtering process");
                                return Mono.just(
                                        ResponseEntity.internalServerError()
                                                .body(new DataListPaymentResponse("Error processing transactions"))
                                );
                            });
    }

    private PaymentResponse mapToPaymentResponse(FinancialTransaction transaction, Payment payment) {
        logger.debug("Mapping transaction {} with payment {}", transaction.getId(), payment.getPaymentId());
        PaymentResponse response = new PaymentResponse();
        response.setTransactionId(transaction.getId());
        response.setPaymentId(payment.getPaymentId());
        response.setAmount(payment.getAmount());
        response.setStatus(transaction.getStatus());
        return response;
    }

    private List<Link> createHateoasLinks(int page, int size, int totalItems) {
        logger.debug("Creating HATEOAS links for page {}, size {}, total items {}", page, size, totalItems);
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