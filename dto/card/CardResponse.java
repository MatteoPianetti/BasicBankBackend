package com.example.demo.dto.card;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.demo.model.card.Card;
import com.example.demo.model.card.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    private Long id;
    private String numeroCartaMascherato;
    private String cardHolder;
    private LocalDate expireDate;
    private Type type;
    private BigDecimal dailyLimit;

    public static CardResponse from(Card c) {
        CardResponse res = new CardResponse();

        res.setId(c.getId());
        res.setNumeroCartaMascherato(c.getNumeroCarta());
        res.setExpireDate(c.getExpireDate());
        res.setCardHolder(c.getCardHolder());
        res.setType(c.getType());
        res.setDailyLimit(c.getDailyLimit());

        return res;
    }
}
