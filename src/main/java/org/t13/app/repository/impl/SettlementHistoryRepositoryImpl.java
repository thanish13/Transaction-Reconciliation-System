package org.t13.app.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.t13.app.model.SettlementReport;
import org.t13.app.repository.SettlementHistoryRepository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SettlementHistoryRepositoryImpl implements SettlementHistoryRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String INSERT_SETTLEMENT_RECORDS = """
            INSERT INTO settlement_history (
                 settlement_id,
                 lifecycle_id,
                 transaction_id,
                 settlement_date,
                 settlement_amount,
                 settlement_type,
                 currency,
                 processed_at
             )
             SELECT\s
                 :settlement_id,
                 :lifecycle_id,
                 t.transaction_id,
                 :settlement_date,
                 :settlement_amount,
                 :settlement_type,
                 :currency,
                 NOW()
             FROM transactions t
             WHERE (
                 (:lifecycle_id IS NOT NULL AND t.lifecycle_id = :lifecycle_id)
                 OR (
                     :lifecycle_id IS NULL
                     AND t.account_id = :account_id
                     AND t.merchant_name = :merchant_name
                     AND t.transaction_date = :transaction_date
                 )
             );
           """;

    public void updateSettlementHistory(SettlementReport settlementReport) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("settlement_id", settlementReport.getSettlementId());
        paramMap.put("lifecycle_id", settlementReport.getLifecycleId());
        paramMap.put("account_id", settlementReport.getAccountId());
        paramMap.put("merchant_name", settlementReport.getMerchantName());
        paramMap.put("transaction_date", settlementReport.getTransactionDate());
        paramMap.put("settlement_date", settlementReport.getSettlementDate());
        paramMap.put("settlement_amount", settlementReport.getSettlementAmount());
        paramMap.put("settlement_type", settlementReport.getSettlementType());
        paramMap.put("currency", settlementReport.getCurrency());

        namedParameterJdbcTemplate.update(INSERT_SETTLEMENT_RECORDS, paramMap);
    }
}
