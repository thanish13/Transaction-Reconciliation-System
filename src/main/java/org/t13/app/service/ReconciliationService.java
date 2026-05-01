package org.t13.app.service;

import org.springframework.web.multipart.MultipartFile;
import org.t13.app.entity.Transactions;
import org.t13.app.model.DashboardSummary;
import org.t13.app.model.TransactionReport;

import java.util.HashMap;
import java.util.List;

public interface ReconciliationService {

    HashMap<String,List<Transactions>> getTransactions();

    void reconcile(MultipartFile file) throws Exception;

    TransactionReport getTransactionsById(String id);

    DashboardSummary getDashboardSummary();
}
