package org.t13.app.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.t13.app.entity.Transactions;
import org.t13.app.model.NetSettlementReport;
import org.t13.app.repository.TransactionsRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Repository
public class TransactionsRepositoryImpl implements TransactionsRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String UPDATE_TRANSACTION_QUERY = """
            UPDATE transactions t
            SET\s
                total_settled_amount = :net_settled_amount,
                last_settlement_date = :last_settlement_date,
                settlement_status = CASE
                    WHEN :net_settled_amount IS NULL OR :net_settled_amount = 0 THEN 'PENDING'
                    WHEN :net_settled_amount < t.transaction_amount AND :net_settled_amount > 0 THEN 'PARTIAL'
                    WHEN :net_settled_amount = t.transaction_amount THEN 'FULLY_SETTLED'
                    WHEN :net_settled_amount > t.transaction_amount THEN 'OVER_SETTLED'
                    WHEN :net_settled_amount < 0 THEN 'REFUNDED'
                    ELSE 'PARTIAL'
                END
            WHERE t.transaction_id = :transaction_id;
            """;

    public List<Transactions> find() {
        return namedParameterJdbcTemplate.query("select * from transactions",
                (rs, rowNum) -> Transactions.builder()
                        .transactionId(rs.getString("transaction_id"))
                        .lifecycleId(rs.getString("lifecycle_id"))
                        .accountId(rs.getString("account_id"))
                        .merchantName(rs.getString("merchant_name"))
                        .transactionDate(LocalDate.parse(rs.getString("transaction_date")))
                        .transactionAmount(BigDecimal.valueOf(rs.getLong("transaction_amount")))
                        .currency(rs.getString("currency"))
                        .status(rs.getString("status"))
                        .settlementStatus(rs.getString("settlement_status"))
                        .totalSettledAmount(BigDecimal.valueOf(rs.getLong("total_settled_amount")))
                        .lastSettlementDate(rs.getDate("last_settlement_date").toLocalDate())
                        .createdAt(LocalDateTime.parse(rs.getString("created_at"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                        .build());
    }

    @Override
    public void updateTransactions(NetSettlementReport  netSettlementReport) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("net_settled_amount",netSettlementReport.getNetSettledAmount());
        params.put("last_settlement_date",netSettlementReport.getLastSettlementDate());
        params.put("transaction_id", netSettlementReport.getTransactionId());

        namedParameterJdbcTemplate.update(UPDATE_TRANSACTION_QUERY, params);
    }
}
