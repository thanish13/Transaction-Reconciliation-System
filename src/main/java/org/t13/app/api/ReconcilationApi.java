package org.t13.app.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.t13.app.entity.Transactions;
import org.t13.app.model.TransactionReport;

import java.util.HashMap;
import java.util.List;

@RestController
public interface ReconcilationApi {

    @GetMapping("/transactions")
    public ResponseEntity<HashMap<String,List<Transactions>>>transactions();

    @PostMapping("/reconcile")
    public ResponseEntity<List<String>> reconcile(@RequestParam("file") MultipartFile file) throws Exception;

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionReport> getTransactionWithSettlements(@PathVariable("id") String id);
}
