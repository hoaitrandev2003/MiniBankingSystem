package com.cybersoft.minibank.service;

import com.cybersoft.minibank.entity.TransactionEntity;

import java.util.List;

public interface TransactionService {
    List<TransactionEntity> findAll();
}
