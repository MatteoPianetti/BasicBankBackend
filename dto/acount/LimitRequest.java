package com.example.demo.dto.acount;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitRequest {
    private BigDecimal dailyLimit;
    private BigDecimal monthlyLimit;
}
