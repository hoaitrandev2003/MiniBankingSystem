package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.dto.TransactionDTO;
import com.cybersoft.minibank.entity.TransactionEntity;
import com.cybersoft.minibank.repository.TransactionRepository;
import com.cybersoft.minibank.service.TransactionService;
import com.cybersoft.minibank.specification.TransactionSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImp implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<TransactionDTO> findAll() {
        return transactionRepository.findAll().stream().map(item -> {
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setCreatedAt(item.getCreatedAt());
            transactionDTO.setDescription(item.getDescription());
            transactionDTO.setStatus(item.getStatus());
            transactionDTO.setAmount(item.getAmount());

            return transactionDTO;
        }).toList();
    }

    @Override
    public Page<TransactionDTO> filter(String status, Double fromAmount, Double toAmount, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        Specification<TransactionEntity> specification = Specification.where(TransactionSpecification.dateFilter(fromDate, toDate))
                .and(TransactionSpecification.hasStatus(status))
                .and(TransactionSpecification.amountFilter(fromAmount, toAmount));

        Page<TransactionEntity> transactionEntities = transactionRepository.findAll(specification, pageable);
        return transactionEntities.map(item -> {
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setCreatedAt(item.getCreatedAt());
            transactionDTO.setDescription(item.getDescription());
            transactionDTO.setStatus(item.getStatus());
            transactionDTO.setAmount(item.getAmount());

            return transactionDTO;
        });
    }
}
