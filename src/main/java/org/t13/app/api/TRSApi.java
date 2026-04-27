package org.t13.app.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.t13.app.entity.Transactions;

import java.util.List;

@RestController
public interface TRSApi {

    @GetMapping("/transactions")
    public ResponseEntity<List<Transactions>>transactions();

    @PostMapping("/reconcile")
    public ResponseEntity<List<String>> uploadCsv(@RequestParam("file") MultipartFile file) throws Exception;

}
