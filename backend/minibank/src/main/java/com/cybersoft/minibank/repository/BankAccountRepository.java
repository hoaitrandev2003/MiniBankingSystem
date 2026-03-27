package com.cybersoft.minibank.repository;

import com.cybersoft.minibank.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<com.cybersoft.minibank.entity.BankAccountEntity,Integer> {
    Optional<com.cybersoft.minibank.entity.BankAccountEntity> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}
