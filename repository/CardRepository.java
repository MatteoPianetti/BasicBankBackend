package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.card.Card;

public interface CardRepository extends JpaRepository<Card, Long>{
    Page<Card> findByAccountId(Long accountId, Pageable pageable);
}
