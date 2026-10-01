package com.cybersoft.minibank.repository;

import com.cybersoft.minibank.entity.BankAccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity,Integer> {
    Optional<BankAccountEntity> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(" SELECT a FROM BankAccountEntity a WHERE a.accountNumber = :accountNumber")
    Optional<BankAccountEntity> findForUpdate(@Param("accountNumber") String accountNumber);
}
