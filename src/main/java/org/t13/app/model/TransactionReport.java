package org.t13.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.t13.app.entity.SettlementHistory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionReport {

    private String transactionId;
    private String lifecycleId;
    private String accountId;
    private String merchantName;
    private LocalDate transactionDate;
    private BigDecimal transactionAmount;
    private String currency;
    private String status;
    private String settlementStatus = "PENDING";
    private BigDecimal totalSettledAmount = BigDecimal.ZERO;
    private LocalDate lastSettlementDate;
    private LocalDateTime createdAt;
    private List<Settlement> settlements;
}
