package org.t13.app.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.t13.app.model.SettlementReport;
import org.t13.app.repository.SettlementHistoryRepository;
import org.t13.app.repository.TransactionsRepository;

@Slf4j
@Component
public class CsvStreamItemWriter implements ItemWriter<SettlementReport> {

    private final TransactionsRepository transactionsRepository;

    private final SettlementHistoryRepository settlementHistoryRepository;


    public CsvStreamItemWriter(TransactionsRepository transactionsRepository, SettlementHistoryRepository settlementHistoryRepository) {
        this.transactionsRepository = transactionsRepository;
        this.settlementHistoryRepository = settlementHistoryRepository;
    }

    @Override
    @StepScope
    public void write(Chunk<? extends SettlementReport> chunk) throws Exception {


        for(SettlementReport item : chunk.getItems()){
            log.info("Writing settlement report: {}", item.getSettlementId());

            settlementHistoryRepository.updateSettlementHistory(item);
        }

    }
}
