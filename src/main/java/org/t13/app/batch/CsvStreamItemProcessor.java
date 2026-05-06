package org.t13.app.batch;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.t13.app.model.SettlementReport;
import org.t13.app.repository.SettlementHistoryRepository;
import org.t13.app.exception.DuplicateSettlementException;

@Component
@Slf4j
public class CsvStreamItemProcessor implements ItemProcessor<SettlementReport, SettlementReport> {

    private final SettlementHistoryRepository settlementHistoryRepository;

    public CsvStreamItemProcessor(SettlementHistoryRepository settlementHistoryRepository) {
        this.settlementHistoryRepository = settlementHistoryRepository;
    }

    @Override
    public @Nullable SettlementReport process(SettlementReport item) {

        log.info("Processing settlement report: {}", item);

        if(!settlementHistoryRepository.getSettlementBySettlementId(item.getSettlementId()).isEmpty()){
            throw new DuplicateSettlementException("Duplicate settlement id " + item.getSettlementId());
        }
        return item;
    }
}
