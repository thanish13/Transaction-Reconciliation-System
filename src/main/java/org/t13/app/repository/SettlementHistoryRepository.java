package org.t13.app.repository;

import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.t13.app.entity.SettlementHistory;
import org.t13.app.entity.Transactions;
import org.t13.app.model.NetSettlementReport;
import org.t13.app.model.SettlementReport;

import java.util.List;

@Registered
public interface SettlementHistoryRepository {

    void updateSettlementHistory(SettlementReport settlementReport);

    List<NetSettlementReport> netSettlement();

    List<SettlementHistory> getSettlementByLifecyle(String lifecycleId);

    List<SettlementHistory> getSettlementBySettlementId(String settlementId);
}
