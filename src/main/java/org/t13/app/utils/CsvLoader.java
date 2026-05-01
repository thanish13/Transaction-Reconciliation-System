package org.t13.app.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;
import org.t13.app.model.SettlementReport;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    public static List<SettlementReport> readCsv(MultipartFile file) throws Exception {
        List<SettlementReport> records = new ArrayList<>();

        try (CSVParser parser = new CSVParser(
                new InputStreamReader(file.getInputStream()),
                CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim())) {

            for (CSVRecord record : parser) {
                SettlementReport sh = new SettlementReport();
                sh.setSettlementId(record.get("settlement_id"));
                sh.setLifecycleId(record.get("lifecycle_id"));
                sh.setAccountId(record.get("account_id"));
                sh.setMerchantName(record.get("merchant_name"));
                sh.setTransactionDate(LocalDate.parse(record.get("transaction_date"),  DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                sh.setSettlementDate(LocalDate.parse(record.get("settlement_date"),  DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                sh.setSettlementAmount(new BigDecimal(record.get("settlement_amount")));
                sh.setSettlementType(record.get("settlement_type"));
                sh.setCurrency(record.get("currency"));
                records.add(sh);
            }
        }
        return records;
    }
}
