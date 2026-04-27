package org.t13.app.api;

import jakarta.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.t13.app.entity.SettlementReport;
import org.t13.app.entity.Transactions;
import org.t13.app.service.TRSServiceImpl;
import org.t13.app.utils.CsvLoader;
import java.util.List;

@Component
public class TRSApiImpl implements TRSApi {

    @Autowired
    TRSServiceImpl trsServiceImpl;

    CsvLoader csvLoader = new CsvLoader();

    @Override
    public ResponseEntity<List<Transactions>> transactions() {
        return trsServiceImpl.getTransactions();
    }

    @Override
    public ResponseEntity<List<String>> uploadCsv(MultipartFile file) throws Exception {
        List<SettlementReport> records = CsvLoader.readCsv(file);
        return ResponseEntity.ok().body(records.stream().map(r -> r.getLifecycleId()).toList());
    }
}
