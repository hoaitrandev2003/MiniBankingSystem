package com.cybersoft.minibank.specification;

import com.cybersoft.minibank.entity.TransactionEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecification {
    public static Specification<TransactionEntity> hasStatus(String status) {
        return (root, query, criteriaBuilder) -> status == null ? null :
                criteriaBuilder.like(root.get("transactionType"), "%" + status + "%");
    }

    public static Specification<TransactionEntity> dateFilter(LocalDateTime fromDate, LocalDateTime toDate){
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null && toDate == null) {
                return null;
            }

            if (fromDate != null && toDate != null) {
                return criteriaBuilder.between(root.get("createdAt"), fromDate, toDate);
            }

            if (fromDate != null) {
                return criteriaBuilder.greaterThan(root.get("createdAt"), fromDate);
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate);
        };
    }

    public static Specification<TransactionEntity> amountFilter(Double fromAmount, Double toAmount){
        return (root, query, criteriaBuilder) -> {
            if (fromAmount == null && toAmount == null) {
                return null;
            }

            if (fromAmount != null && toAmount != null) {
                return criteriaBuilder.between(root.get("amount"), fromAmount, toAmount);
            }

            if (fromAmount != null) {
                return criteriaBuilder.greaterThan(root.get("amount"), fromAmount);
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("amount"), toAmount);
        };
    }
}
