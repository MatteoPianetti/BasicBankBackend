package com.example.demo.dto.acount;

import java.math.BigDecimal;
import java.time.LocalTime;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyTransactionResponse {
    private BigDecimal amount;
    private Type transactionType;
    private LocalTime hour;

    public static DailyTransactionResponse from(Transaction t) {
        DailyTransactionResponse res = new DailyTransactionResponse();

        res.setAmount(t.getAmount());
        res.setTransactionType(t.getType());
        res.setHour(t.getTimeStamp().toLocalTime());

        return res;
    }
}
