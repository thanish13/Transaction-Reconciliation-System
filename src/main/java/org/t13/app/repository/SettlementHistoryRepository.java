package org.t13.app.repository;

import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.t13.app.entity.SettlementHistory;
import org.t13.app.model.SettlementReport;

@Registered
public interface SettlementHistoryRepository {

    void updateSettlementHistory(SettlementReport settlementReport);
}
