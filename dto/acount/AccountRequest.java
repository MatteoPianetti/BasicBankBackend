package com.example.demo.dto.acount;

import java.math.BigDecimal;

import com.example.demo.model.account.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private Status status;
    private Long userId;
}
