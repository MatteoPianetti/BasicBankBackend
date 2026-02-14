package com.example.demo.dto.transaction;

import lombok.*;
import java.math.BigDecimal;
import com.example.demo.model.transaction.Type;
import com.example.demo.model.transaction.Status;
import com.example.demo.model.transaction.Transaction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private String currency;
    private Type type;
    private Status status;
    private Long fromAccountId;
    private Long toAccountId;
    private String reason;
    private Long cardId;
    private String country;
    private String swift;

    public static TransactionResponse from(Transaction t) {
        TransactionResponse res = new TransactionResponse();

        res.setId(t.getId());
        res.setAmount(t.getAmount());
        res.setCurrency(t.getCurrency());
        res.setType(t.getType());
        res.setStatus(t.getStatus());
        res.setFromAccountId(t.getFromAccount().getId());
        res.setToAccountId(t.getToAccount().getId());
        res.setReason(t.getReason());
        res.setCardId(t.getCard().getId());
        res.setCountry(t.getCountry());
        res.setSwift(t.getS());

        return res;
    }
}
