package com.mycompany.nascotech.controller;

import dao.FinancialTransactionRepository;
import com.mycompany.nascotech.models.DataListPaymentResponse;
import models.FilterParaRequest;
import com.mycompany.nascotech.service.TaskService;
import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/task")
public class FinancialTransactionController {

    private final TaskService tService;
    private final FinancialTransactionRepository financialTransactionRepository;


    public FinancialTransactionController(TaskService tService, TaskService.PaymentService paymentService, FinancialTransactionRepository financialTransactionRepository) {
        this.tService = tService;
        this.financialTransactionRepository = financialTransactionRepository;
    }

    @GetMapping("api/filter")
    public ResponseEntity<Page<Payment>> fetchFilteredData(
            @ModelAttribute FilterParaRequest request,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String sortBy
    ) {
        Page<YourEntity> result = tService.fetchFilteredData(
                request,
                offset,
                limit,
                sortBy
        );

        return ResponseEntity.ok(result);
    }


    @GetMapping
    public Mono<ResponseEntity<DataListPaymentResponse>> getFinancialTransactions(
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String service,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String reference) {

        PageRequest pageRequest = PageRequest.of(offset, limit);

        return Mono.fromCallable(() -> financialTransactionRepository.findByFilters(dateFrom, dateTo, userId, service, status, reference, pageRequest))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(page -> {
                    List<FinancialTransaction> transactions = page.getContent();

                    // Convert to Flux for processing
                    return Flux.fromIterable(transactions)
                            .flatMap(transaction -> paymentService.retrieveFinancialTransaction(transaction.getPaymentId())
                                    .map(payment -> new PaymentWithTransaction(transaction, payment)))
                            .collectList();
                })
                .map(payments -> {
                    //Sort payments by paymentId
                    payments.sort((p1, p2) -> p2.getPayment().getPaymentId().compareTo(p1.getPayment().getPaymentId()));                   
                    DataListPaymentResponse response = new DataListPaymentResponse(payments);

                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    // Handle errors and return appropriate response
                    return Mono.just(ResponseEntity.status(500).body(new DataListPaymentResponse("Error processing request")));
                });
    }
}



}
