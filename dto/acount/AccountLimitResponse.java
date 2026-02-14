package com.example.demo.dto.acount;

import java.math.BigDecimal;

import com.example.demo.model.account.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountLimitResponse {
    private String accountNumber;
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;

    public static AccountLimitResponse from(Account a) {
        AccountLimitResponse res = new AccountLimitResponse();

        res.setAccountNumber(a.getAccountNumber());
        res.setDailyLimit(a.getDailyLimit());
        res.setMonthlyLimit(a.getMonthlyLimit());

        return res;
    }
}
