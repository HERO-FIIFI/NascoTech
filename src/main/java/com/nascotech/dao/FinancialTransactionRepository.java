package com.nascotech.dao;
import com.nascotech.models.FinancialTransaction;
import org.springframework.stereotype.Repository;
import java.awt.print.Pageable;
import java.time.LocalDate;
import reactor.core.publisher.Flux;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;



@Repository
public interface FinancialTransactionRepository extends ReactiveCrudRepository<FinancialTransaction, String> {
    Flux<FinancialTransaction> findTransactions(LocalDate dateFrom, LocalDate dateTo, String userId,
                                                String service, String status, String reference, Pageable pageable);
}
