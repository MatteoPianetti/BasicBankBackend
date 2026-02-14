package com.example.demo.dto.acount;

import java.math.BigDecimal;
import java.util.List;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionAccountResponse {
    private Type transactionType;
    private BigDecimal amount;
    private int numberOfTimes;

    public static TransactionAccountResponse from(List<Transaction> transactions) {
        TransactionAccountResponse res = new TransactionAccountResponse();

        res.setTransactionType(transactions.getFirst().getType());

        BigDecimal sum = BigDecimal.ZERO;
        for(Transaction t : transactions) {
            sum.add(t.getAmount());
        }
        res.setAmount(sum);

        res.setNumberOfTimes(transactions.size());

        return res;
    }
}
