package org.t13.app.batch;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.NullInputStream;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.t13.app.model.SettlementReport;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

@Component
@Slf4j
public class CsvStreamItemReader implements ItemReader<SettlementReport> {

    private static Iterator<CSVRecord> records;

    @PostConstruct
    public void init() throws IOException {

        File file = new File("src/main/resources/settlement.csv");

        FileInputStream fis = new FileInputStream(file);

        InputStreamReader inputStream = new InputStreamReader(fis);

        log.info("Stream: {} ", inputStream);

        CSVFormat format = CSVFormat.Builder.create()
                .setHeader()                 // treat first row as header
                .setSkipHeaderRecord(true)    // skip header row in data
                .setTrim(true)                // trim whitespace
                .setIgnoreEmptyLines(true)    // skip empty lines
                .build();

        CSVParser parser = new CSVParser(inputStream, format);
        records = parser.iterator();

        log.info("Reading items from CSV file");

    }

    @Override
    @StepScope
    public @Nullable SettlementReport read()  {

        if (records.hasNext()) {
            CSVRecord record = records.next();
            SettlementReport settlementReport = new SettlementReport();
            settlementReport.setSettlementId(record.get("settlement_id"));
            settlementReport.setLifecycleId(record.get("lifecycle_id"));
            settlementReport.setAccountId(record.get("account_id"));
            settlementReport.setMerchantName(record.get("merchant_name"));
            settlementReport.setTransactionDate(LocalDate.parse(record.get("transaction_date"), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            settlementReport.setSettlementDate(LocalDate.parse(record.get("settlement_date"), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            settlementReport.setSettlementAmount(new BigDecimal(record.get("settlement_amount")));
            settlementReport.setSettlementType(record.get("settlement_type"));
            settlementReport.setCurrency(record.get("currency"));
            log.info("settlementReport {}", settlementReport.getSettlementId());

            return settlementReport;
        }

        return null;

    }
}
