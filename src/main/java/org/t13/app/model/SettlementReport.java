package org.t13.app.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementReport {

    private String settlementId;
    private String lifecycleId;
    private String accountId;
    private String merchantName;
    private LocalDate transactionDate;
    private LocalDate settlementDate;
    private BigDecimal settlementAmount;
    private String settlementType;
    private String currency;
    private LocalDateTime processedAt;
}

