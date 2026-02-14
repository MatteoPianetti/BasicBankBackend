package com.example.demo.repository;

import com.example.demo.model.transaction.*;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    Page<Transaction> findAll(Pageable pageable);

     @Query("""
        SELECT t
        FROM Transaction t
        WHERE(
            t.timeStamp BETWEEN :start AND :end
        ) 
    """)
    Page<Transaction> findByDate(
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end, 
        Pageable pageable
    );

    @Query("""
            Select t
            From Transaction t
            where t.fromAccount.id = :accountId
    """)
    Page<Transaction> findByAccount(
        @Param("accountId") Long accountId,
        Pageable pageable
    );

    @Query("""
            SELECT t
            FROM Transaction t
            WHERE t.status = :status     
    """)
    Page<Transaction> findByStatus(
        @Param("status") Status status, 
        Pageable pageable
    );

    @Query("""
        SELECT t
        FROM Transaction t
        WHERE(
            t.timeStamp BETWEEN :from AND :to
        ) 
    """)
    Page<Transaction> findByPeriod(
        @Param("from") LocalDateTime from, 
        @Param("to") LocalDateTime to, 
        Pageable pageable
    );

    @Query("""
    SELECT t 
    FROM Card c
    JOIN c.transactions t
    WHERE c.id = :cardId
    """)
    Page<Transaction> findByCardId(
        @Param("cardId") Long cardId,
        Pageable pageable
    );

    @Query("""
       SELECT t
       FROM Transaction t
       WHERE(
        t.fromAccount = :accountId
       )     
    """)
    Page<Transaction> findByAccountId(
        @Param("accountId") Long accountId, 
        Pageable pageable
    );
}
