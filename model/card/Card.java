package com.example.demo.model.card;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.demo.model.account.Account;
import com.example.demo.model.transaction.Transaction;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cards")
@Entity
@Data
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_carta")
    @Convert(converter = CardNumberEncryptor.class) //cifratura
    private String numeroCarta;

    @Transient //non lo salvo nel database
    private String cvv;

    @Column(name = "cvv_hash") // Solo hash per verifica
    private String cvvHash;

    @Column(name = "titolare")
    private String cardHolder;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @Column(name = "scadenza")
    private LocalDate expireDate;

    @Column(name = "limite_giornaliero")
    private BigDecimal dailyLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "account_id"
    )
    private Account account;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

     // Metodo per display
    public String getMaskedCardNumber() {
        if (numeroCarta == null || numeroCarta.length() < 4) {
            return "****";
        }
        String lastFour = numeroCarta.substring(numeroCarta.length() - 4);
        return "**** **** **** " + lastFour;
    }
}
