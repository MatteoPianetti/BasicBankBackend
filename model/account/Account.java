package com.example.demo.model.account;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.User;

import jakarta.persistence.*;

@Builder
@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "iban", nullable =  false)
    private String accountNumber;

    @Column(name = "bilancio")
    private BigDecimal balance;

    @Column(name = "valuta")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato")
    private Status status;

    private LocalDate createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "motivazione")
    private String reason;

    @Column(name = "blockedBy") 
    private String blockedBy;

    @Column(name = "severità")
    private Severity severity;

    @Column(name = "limite_giornaliero")
    private BigDecimal dailyLimit;

    @Column(name = "limite_mensile")
    private BigDecimal monthlyLimit;

    @OneToMany(mappedBy = "fromAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> outgoingTransactions;

    @OneToMany(mappedBy = "toAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> incomingTransactions;

    @PrePersist
    private void createdAt() {this.createdAt = LocalDate.now();}
}
