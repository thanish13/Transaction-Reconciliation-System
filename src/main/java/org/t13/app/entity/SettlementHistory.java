package org.t13.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlement_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementHistory {

    @Id
    @Column(name = "settlement_id", length = 50)
    private String settlementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id")
    private Transactions transaction;

    @Column(name = "lifecycle_id", length = 50)
    private String lifecycleId;

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @Column(name = "settlement_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal settlementAmount;

    @Column(name = "settlement_type", length = 10, nullable = false)
    private String settlementType; // "DEBIT" or "CREDIT"

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
