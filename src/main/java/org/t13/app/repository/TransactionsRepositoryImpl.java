package org.t13.app.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.t13.app.entity.Transactions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class TransactionsRepositoryImpl implements TransactionsRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Transactions> findAll() {
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
//                        .lastSettlementDate(rs.getString("last_settlement_date"). "null" ? LocalDate.now() : LocalDate.parse(rs.getString("last_settlement_date")))
                        .createdAt(LocalDateTime.parse(rs.getString("created_at"),  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")))
                        .build());
    }
}
