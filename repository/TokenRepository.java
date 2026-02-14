package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.token.Token;

import java.util.*;


public interface TokenRepository extends JpaRepository<Token, Long>{
    @Query(value = """
            SELECT  t from Token t join User u\s
            on t.user.id = u.id\s
            where u.id = :id and(t.expired = false or t.revoked = false)\s
        """)
    List<Token> findAllValidTokenByUser(Long id);
    Optional<Token> findByToken(String token);
}
