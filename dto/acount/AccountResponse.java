package com.example.demo.dto.acount;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.demo.model.account.Account;
import com.example.demo.model.account.Status;
import com.example.demo.model.transaction.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long accountId;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private Status status;
    private Long userId;
    private LocalDate createdAt;
    private List<Transaction> outTransactions;
    private List<Transaction> inTransactions;

    public static AccountResponse from(Account a) {
        AccountResponse res = new AccountResponse();

        res.setAccountId(a.getId());
        res.setAccountNumber(a.getAccountNumber());
        res.setBalance(a.getBalance());
        res.setCurrency(a.getCurrency());
        res.setStatus(a.getStatus());
        res.setUserId(a.getUser().getId());
        res.setCreatedAt(a.getCreatedAt());
        res.setOutTransactions(a.getOutgoingTransactions());
        res.setInTransactions(a.getIncomingTransactions());

        return res;
    }
}
