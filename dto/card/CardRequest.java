package com.example.demo.dto.card;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.demo.model.card.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRequest {
    private String numeroCarta;
    private String cardHolder;
    private String cvv;
    private Type type;
    private LocalDate expirDate;
    private BigDecimal dailyLimit;
    private Long accountId;
}
