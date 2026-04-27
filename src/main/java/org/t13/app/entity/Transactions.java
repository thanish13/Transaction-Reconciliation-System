package org.t13.app.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transactions {

    @Id
    @Column(name = "transaction_id", length = 50)
    private String transactionId;

    @Column(name = "lifecycle_id", length = 50)
    private String lifecycleId;

    @Column(name = "account_id", length = 50, nullable = false)
    private String accountId;

    @Column(name = "merchant_name", length = 100, nullable = false)
    private String merchantName;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "transaction_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal transactionAmount;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "settlement_status", length = 20)
    private String settlementStatus = "PENDING";

    @Column(name = "total_settled_amount", precision = 10, scale = 2)
    private BigDecimal totalSettledAmount = BigDecimal.ZERO;

    @Column(name = "last_settlement_date")
    private LocalDate lastSettlementDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Relationship with SettlementHistory
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SettlementHistory> settlementHistories;

}
