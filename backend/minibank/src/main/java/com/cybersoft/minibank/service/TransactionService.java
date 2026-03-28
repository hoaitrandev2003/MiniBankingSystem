package com.cybersoft.minibank.service;

import com.cybersoft.minibank.dto.TransactionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    List<TransactionDTO> findAll();
    Page<TransactionDTO> filter(String status, Double fromAmount, Double toAmount, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}
