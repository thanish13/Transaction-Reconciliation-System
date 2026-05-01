package org.t13.app.transformer;

import org.t13.app.entity.SettlementHistory;
import org.t13.app.entity.Transactions;
import org.t13.app.model.Settlement;
import org.t13.app.model.TransactionReport;

import java.util.List;

public class TransactionTransformer {

    public static TransactionReport transform(Transactions transactions, List<SettlementHistory> settlementHistory) {
        return TransactionReport.builder()
                .lifecycleId(transactions.getLifecycleId())
                .transactionId(transactions.getTransactionId())
                .accountId(transactions.getAccountId())
                .createdAt(transactions.getCreatedAt())
                .merchantName(transactions.getMerchantName())
                .currency(transactions.getCurrency())
                .lastSettlementDate(transactions.getLastSettlementDate())
                .settlementStatus(transactions.getSettlementStatus())
                .totalSettledAmount(transactions.getTotalSettledAmount())
                .transactionDate(transactions.getTransactionDate())
                .status(transactions.getStatus())
                .transactionAmount(transactions.getTransactionAmount())
                .settlements(toSettlement(settlementHistory))
                .build();
    }

    private static List<Settlement> toSettlement(List<SettlementHistory> settlementHistory) {
        return settlementHistory.stream().map(s ->
            Settlement.builder()
                    .settlementId(s.getSettlementId())
                    .currency(s.getCurrency())
                    .settlementAmount(s.getSettlementAmount())
                    .settlementDate(s.getSettlementDate())
                    .settlementType(s.getSettlementType())
                    .lifecycleId(s.getLifecycleId())
                    .processedAt(s.getProcessedAt())
                    .build()
        ).toList();
    }


}
