package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.service.ExportService;
import com.cybersoft.minibank.service.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ExportService exportService;

    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestParam(required = false) String status,
                                    @RequestParam(required = false) Double fromAmount,
                                    @RequestParam(required = false) Double toAmount,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
                                    Pageable pageable) {
        return ResponseEntity.ok(transactionService.filter(status, fromAmount, toAmount, fromDate, toDate, pageable));
    }

    @GetMapping("/export/{format}")
    public void export(@PathVariable String format, HttpServletResponse response) throws IOException {
        exportService.executeExport(format, response);
    }
}
