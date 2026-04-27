package org.t13.app.repository;

import org.springframework.stereotype.Repository;
import org.t13.app.entity.Transactions;

import java.util.List;

@Repository
public interface TransactionsRepository{

    public List<Transactions> findAll();
}
