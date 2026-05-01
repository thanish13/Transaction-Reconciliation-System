package org.t13.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.t13.app.entity.SettlementHistory;
import org.t13.app.entity.Transactions;
import org.t13.app.model.SettlementReport;
import org.t13.app.repository.SettlementHistoryRepository;
import org.t13.app.repository.TransactionsRepository;
import org.t13.app.repository.impl.SettlementHistoryRepositoryImpl;
import org.t13.app.service.ReconciliationService;
import org.t13.app.utils.CsvLoader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReconciliationServiceImpl implements ReconciliationService {

    private final TransactionsRepository transactionsRepository;
    private final SettlementHistoryRepository settlementHistoryRepository;

    public ReconciliationServiceImpl(TransactionsRepository transactionsRepository, SettlementHistoryRepository settlementHistoryRepository) {
        this.transactionsRepository = transactionsRepository;
        this.settlementHistoryRepository = settlementHistoryRepository;
    }

    public List<Transactions> getTransactions() {
        return transactionsRepository.findAll();
    }

    @Override
    public void reconcile(MultipartFile file) throws Exception {
        List<SettlementReport> reportList = CsvLoader.readCsv(file);
        reportList.forEach(settlementHistoryRepository::updateSettlementHistory);
    }
}
