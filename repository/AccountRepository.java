package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.account.Account;

import jakarta.persistence.LockModeType;

public interface AccountRepository extends JpaRepository<Account, Long>{
    Optional<Account> findByAccountNumber(String iban);

    Optional<Account> findByUserId(Long userId);

    Optional<Account> findByIdAndUserId(Long accountId, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a FROM Account a WHERE a.id = :id     
    """)
    void lockById(@Param("id") Long id);
}
