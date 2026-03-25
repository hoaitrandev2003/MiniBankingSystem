package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.entity.TransactionEntity;
import com.cybersoft.minibank.repository.TransactionRepository;
import com.cybersoft.minibank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImp implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<TransactionEntity> findAll() {
        return transactionRepository.findAll();
    }
}
