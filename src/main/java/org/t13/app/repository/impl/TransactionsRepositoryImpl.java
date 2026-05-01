package org.t13.app.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.t13.app.entity.Transactions;
import org.t13.app.model.DashboardSummary;
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

    private static final String DASHBOARD_QUERY = """
            SELECT
                COUNT(*) AS totalTransactions,
                SUM(CASE WHEN settlement_status = 'PENDING' THEN 1 ELSE 0 END) AS pendingTransactions,
                SUM(CASE WHEN settlement_status = 'PARTIAL' THEN 1 ELSE 0 END) AS partialSettledTransactions,
                SUM(CASE WHEN settlement_status = 'FULLY_SETTLED' THEN 1 ELSE 0 END) AS fullySettledTransactions,
                SUM(CASE WHEN settlement_status = 'OVER_SETTLED' THEN 1 ELSE 0 END) AS overSettledTransactions,
                SUM(CASE WHEN settlement_status = 'REFUNDED' THEN 1 ELSE 0 END) AS refundedTransactions,
                SUM(CASE WHEN settlement_status = 'NOT_APPLICABLE' THEN 1 ELSE 0 END) AS notApplicableTransactions,
                SUM(COALESCE(total_settled_amount,0)) AS totalSettlementAmount
            FROM transactions
    """;

    public List<Transactions> find() {
        return namedParameterJdbcTemplate.query("select * from transactions",
                transactionsRowMapper());
    }

    public List<Transactions> findById(String id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("transaction_id", id);
        return namedParameterJdbcTemplate.query("select * from transactions where transaction_id = :transaction_id",
                params,
                transactionsRowMapper());
    }

    @Override
    public void updateTransactions(NetSettlementReport  netSettlementReport) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("net_settled_amount",netSettlementReport.getNetSettledAmount());
        params.put("last_settlement_date",netSettlementReport.getLastSettlementDate());
        params.put("transaction_id", netSettlementReport.getTransactionId());

        namedParameterJdbcTemplate.update(UPDATE_TRANSACTION_QUERY, params);
    }

    public List<DashboardSummary> dashboardReport() {
        return namedParameterJdbcTemplate.query(DASHBOARD_QUERY, (rs, rowNum) ->
                DashboardSummary.builder()
                        .totalTransactions(rs.getInt("totalTransactions"))
                        .pendingTransactions(rs.getInt("pendingTransactions"))
                        .partialSettledTransactions(rs.getInt("partialSettledTransactions"))
                        .fullySettledTransactions(rs.getInt("fullySettledTransactions"))
                        .overSettledTransactions(rs.getInt("overSettledTransactions"))
                        .refundedTransactions(rs.getInt("refundedTransactions"))
                        .notApplicableTransactions(rs.getInt("notApplicableTransactions"))
                        .totalSettlementAmount(rs.getBigDecimal("totalSettlementAmount"))
                        .lastUpdated(LocalDateTime.now())
                        .build()
        );
    }

    private RowMapper<Transactions> transactionsRowMapper(){
        return (rs, rowNum) -> Transactions.builder()
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
                .lastSettlementDate(rs.getDate("last_settlement_date") == null ? null : rs.getDate("last_settlement_date").toLocalDate())
                .createdAt(LocalDateTime.parse(rs.getString("created_at"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                .build();
    }
}
