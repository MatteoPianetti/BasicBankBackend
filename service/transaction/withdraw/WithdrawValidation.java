package com.example.demo.service.transaction.withdraw;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.demo.dto.transaction.TransactionRequest;
import com.example.demo.exception.NoEntityFoundException;
import com.example.demo.model.account.Account;
import com.example.demo.model.transaction.Type;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.transaction.Validator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WithdrawValidation implements Validator{
    private final AccountRepository accountRepository;
    @Override
    public void validateTransaction(TransactionRequest request) {
        if(request.getFromAccount() == null){
            throw new IllegalArgumentException("Deve esistere un from account");    
        }
        
        Account account = accountRepository.findById(request.getFromAccount())
                .orElseThrow(() -> new NoEntityFoundException("Nessun account trovato"));

        if(request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero");
        }

        if(request.getAmount().compareTo(account.getBalance()) <= 0) {
            throw new IllegalArgumentException("Non ci sono abbastanza soldi sul conto");
        }

        if(request.getAmount().compareTo(account.getDailyLimit()) > 0){
            throw new IllegalArgumentException("Superati i limiti giornalieri");
        }
    }

    @Override
    public Type getType() {
        return Type.WITHDRAWAL;
    }
    
}
