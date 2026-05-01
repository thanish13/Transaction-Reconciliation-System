package org.t13.app.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {

    private int totalTransactions;
    private int pendingTransactions;
    private int partialSettledTransactions;
    private int fullySettledTransactions;
    private int overSettledTransactions;
    private int refundedTransactions;
    private int noSettlementAfter7Days;
    private int notApplicableTransactions;
    private BigDecimal totalSettlementAmount;
    private LocalDateTime lastUpdated;

}
