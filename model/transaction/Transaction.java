package com.example.demo.model.transaction;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.demo.model.account.Account;
import com.example.demo.model.card.Card;

import jakarta.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "transactions")  
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "somma")
    private BigDecimal amount;

    @Column(name = "valuta")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato")
    private Status status;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    // Conto destinazione (chi riceve / dove arrivano i soldi)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;

    private LocalDateTime timeStamp;

    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "card_id"
    )
    private Card card;

    @Column(name = "nazione")
    private String country;
    
    @Column(name = "swift")
    private String s;

    @PrePersist
    public void createdAt() {this.timeStamp = LocalDateTime.now();}
}
