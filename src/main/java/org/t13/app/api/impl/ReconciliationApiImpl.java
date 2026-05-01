package org.t13.app.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.t13.app.api.ReconcilationApi;
import org.t13.app.entity.Transactions;
import org.t13.app.model.DashboardSummary;
import org.t13.app.model.TransactionReport;
import org.t13.app.service.ReconciliationService;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class ReconciliationApiImpl implements ReconcilationApi {

    private final ReconciliationService reconciliationService;

    public ReconciliationApiImpl(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @Override
    public ResponseEntity<HashMap<String,List<Transactions>>> transactions() {
        return ResponseEntity.ok(reconciliationService.getTransactions());
    }

    @Override
    public ResponseEntity<?> reconcile(MultipartFile file) {
        try {
            reconciliationService.reconcile(file);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<TransactionReport> getTransactionWithSettlements(String id) {
        return ResponseEntity.ok(reconciliationService.getTransactionsById(id));
    }

    @Override
    public ResponseEntity<DashboardSummary> getDashboardSummary() {
        return ResponseEntity.ok(reconciliationService.getDashboardSummary());
    }

}