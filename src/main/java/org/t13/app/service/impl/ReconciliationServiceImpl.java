package org.t13.app.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.t13.app.entity.Transactions;
import org.t13.app.model.NetSettlementReport;
import org.t13.app.model.SettlementReport;
import org.t13.app.model.TransactionReport;
import org.t13.app.repository.SettlementHistoryRepository;
import org.t13.app.repository.TransactionsRepository;
import org.t13.app.service.ReconciliationService;
import org.t13.app.transformer.TransactionTransformer;
import org.t13.app.utils.CsvLoader;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

@Component
public class ReconciliationServiceImpl implements ReconciliationService {

    private final TransactionsRepository transactionsRepository;
    private final SettlementHistoryRepository settlementHistoryRepository;

    public ReconciliationServiceImpl(TransactionsRepository transactionsRepository, SettlementHistoryRepository settlementHistoryRepository) {
        this.transactionsRepository = transactionsRepository;
        this.settlementHistoryRepository = settlementHistoryRepository;
    }

    public HashMap<String,List<Transactions>> getTransactions() {
        List<Transactions> redIssues = transactionsRepository.find().stream()
                .filter(t ->
                        t.getTotalSettledAmount().compareTo(t.getTransactionAmount()) > 0 ||
                        ChronoUnit.DAYS.between(t.getTransactionDate(),
                                t.getLastSettlementDate() == null ? LocalDate.now() : t.getLastSettlementDate() ) > 7)
                .toList();

        List<Transactions> yellowIssues = transactionsRepository.find().stream()
                .filter(t -> t.getTotalSettledAmount().compareTo(t.getTransactionAmount()) < 0 ).toList();

        HashMap<String,List<Transactions>> map = new HashMap<>();
        map.put("redIssues", redIssues);
        map.put("yellowIssues", yellowIssues);

        return map;
    }

    @Override
    public void reconcile(MultipartFile file) throws Exception {
        List<SettlementReport> reportList = CsvLoader.readCsv(file);
        reportList.forEach(settlementHistoryRepository::updateSettlementHistory);
        List<NetSettlementReport> netSettlementReports = settlementHistoryRepository.netSettlement();
        netSettlementReports.forEach(transactionsRepository::updateTransactions);
    }

    @Override
    public TransactionReport getTransactionsById(String id) {
        return TransactionTransformer.transform(transactionsRepository.findById(id).getFirst(), settlementHistoryRepository.findById(id));
    }
}
