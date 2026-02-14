package com.example.demo.dto.transaction;

import java.math.BigDecimal;

import com.example.demo.model.transaction.Status;
import com.example.demo.model.transaction.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    private BigDecimal amount;
    private String currency;
    private Type type;
    private Status status;
    private String description;
    private Long fromAccount;
    private Long toAccount;
    private String reason;
    private Long cardId;
    private String country;
    private String swift;
}
