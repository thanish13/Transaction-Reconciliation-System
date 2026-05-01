package org.t13.app.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.t13.app.api.ReconcilationApi;
import org.t13.app.model.SettlementReport;
import org.t13.app.entity.Transactions;
import org.t13.app.service.ReconciliationService;
import org.t13.app.utils.CsvLoader;

import java.util.List;

@Component
@Slf4j
public class ReconciliationApiImpl implements ReconcilationApi {

    private final ReconciliationService reconciliationService;

    public ReconciliationApiImpl(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @Override
    public ResponseEntity<List<Transactions>> transactions() {
        return ResponseEntity.ok(reconciliationService.getTransactions());
    }

    @Override
    public ResponseEntity<List<String>> reconcile(MultipartFile file) {
        try {
            reconciliationService.reconcile(file);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
