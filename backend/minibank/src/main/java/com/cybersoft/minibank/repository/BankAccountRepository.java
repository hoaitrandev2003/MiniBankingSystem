package com.cybersoft.minibank.repository;

import com.cybersoft.minibank.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity,Integer> {
    Optional<BankAccountEntity> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}
