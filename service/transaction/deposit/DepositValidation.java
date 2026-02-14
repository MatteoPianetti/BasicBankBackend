package com.example.demo.service.transaction.deposit;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.demo.dto.transaction.TransactionRequest;
import com.example.demo.model.transaction.Type;
import com.example.demo.service.transaction.Validator;

@Component
public class DepositValidation implements Validator{
    @Override
    public void validateTransaction(TransactionRequest request) {
        if(request.getToAccount() == null) {
            throw new IllegalArgumentException("E' necessario un account a cui fare il deposito");
        }
        if(request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero");
        }

    }

    @Override
    public Type getType() {
        return Type.DEPOSIT;
    }
}
