package org.t13.app.service;

import org.springframework.web.multipart.MultipartFile;
import org.t13.app.entity.Transactions;

import java.util.List;

public interface ReconciliationService {

    List<Transactions> getTransactions();

    void reconcile(MultipartFile file) throws Exception;
}
