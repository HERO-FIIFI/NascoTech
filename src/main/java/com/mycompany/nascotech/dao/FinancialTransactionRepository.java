package com.mycompany.nascotech.dao;
import models.FilterParaRequest;
import java.awt.print.Pageable;


@Repository
public interface FinancialTransactionRepository {
    Page<FilterParaRequest> findByFilters(String dateFrom, String dateTo, String userId, String service, String status, String reference, Pageable pageable);
}