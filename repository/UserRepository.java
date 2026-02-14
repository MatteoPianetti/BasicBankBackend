package com.example.demo.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.user.User;

import java.util.Optional;
import java.time.LocalDateTime;
import com.example.demo.model.user.enumerated.Role;


public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);

    Page<User> findByRole(Role role, Pageable pageable);

    boolean existsByEmail(String email);

    Page<User> findByLastLogin(LocalDateTime lastLogin, Pageable pageable);

    @Query("""
        SELECT u 
        FROM User u
        WHERE u.createdAt BETWEEN :from AND :to
    """)
    Page<User> findNewUsers(
        @Param("from") LocalDateTime from, 
        @Param("to") LocalDateTime to,
        Pageable pageable
    );

    @Query("""
        SELECT u
        FROM User u
        WHERE u.enabled = true        
    """
    )
    Page<User> findByEnabled(boolean enable, Pageable pageable);
}
