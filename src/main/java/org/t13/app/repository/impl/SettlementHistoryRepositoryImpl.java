package org.t13.app.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.t13.app.entity.SettlementHistory;
import org.t13.app.entity.Transactions;
import org.t13.app.model.NetSettlementReport;
import org.t13.app.model.SettlementReport;
import org.t13.app.repository.SettlementHistoryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
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

    private static final String SETTLEMENT_SUMMARY_QUERY = """
            SELECT s.transaction_id,
            sum(
                CASE
                    WHEN s.settlement_type = 'DEBIT'
                        THEN s.settlement_amount
                        ELSE -s.settlement_amount
                END) AS net_settled_amount,
            max(s.settlement_date) AS last_settlement_date
            FROM settlement_history s
            GROUP BY s.transaction_id
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

    @Override
    public List<NetSettlementReport> netSettlement() {

        return namedParameterJdbcTemplate.query(SETTLEMENT_SUMMARY_QUERY, (rs, rowNum) ->
                NetSettlementReport.builder()
                        .transactionId(rs.getString("TRANSACTION_ID"))
                        .netSettledAmount(rs.getBigDecimal("NET_SETTLED_AMOUNT"))
                        .lastSettlementDate(rs.getDate("LAST_SETTLEMENT_DATE").toLocalDate())
                        .build());
    }

    public List<SettlementHistory> findById(String id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("transaction_id", id);
        return namedParameterJdbcTemplate.query("select * from settlement_history where transaction_id = :transaction_id",
                params,
                settlementRowMapper());
    }

    private RowMapper<SettlementHistory> settlementRowMapper() {
        return (rs, intNum) -> SettlementHistory.builder()
                .settlementId(rs.getString("settlement_id"))
                .lifecycleId(rs.getString("lifecycle_id"))
                .settlementDate(LocalDate.parse(rs.getString("settlement_date")))
                .settlementAmount(rs.getBigDecimal("settlement_amount"))
                .settlementType(rs.getString("settlement_type"))
                .currency(rs.getString("currency"))
                .processedAt(LocalDateTime.parse(rs.getString("processed_at"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                .build();
    }
}
