package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        return ResponseEntity.ok(transactionService.findAll());
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestParam String status,
                                    @RequestParam Double fromAmount,
                                    @RequestParam Double toAmount,
                                    @RequestParam LocalDateTime fromDate,
                                    @RequestParam LocalDateTime toDate,
                                    Pageable pageable) {
        return ResponseEntity.ok(transactionService.filter(status, fromAmount, toAmount, fromDate, toDate, pageable));
    }
}
