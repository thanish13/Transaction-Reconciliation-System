package org.t13.app.repository;

import org.springframework.stereotype.Repository;
import org.t13.app.entity.Transactions;
import org.t13.app.model.DashboardSummary;
import org.t13.app.model.NetSettlementReport;

import java.util.List;

@Repository
public interface TransactionsRepository{

    List<Transactions> find();
    void updateTransactions(NetSettlementReport  netSettlementReport);
    List<Transactions> findById(String id);
    List<DashboardSummary> dashboardReport();

}
