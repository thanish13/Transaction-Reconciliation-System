package org.t13.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.t13.app.entity.Transactions;
import org.t13.app.repository.TransactionsRepository;

import java.util.List;

@Configuration
public class TRSServiceImpl {

    @Autowired
    private TransactionsRepository transactionsRepository;

    public ResponseEntity<List<Transactions>> getTransactions() {
        return ResponseEntity.ok(transactionsRepository.findAll());

    }
}
