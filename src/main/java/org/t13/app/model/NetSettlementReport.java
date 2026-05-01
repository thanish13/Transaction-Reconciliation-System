package org.t13.app.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetSettlementReport {

    private String transactionId;
    private BigDecimal netSettledAmount;
    private LocalDate lastSettlementDate;
}
