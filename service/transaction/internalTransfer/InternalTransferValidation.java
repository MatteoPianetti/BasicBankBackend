package com.example.demo.service.transaction.internalTransfer;

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
public class InternalTransferValidation implements Validator{
    private final AccountRepository accountRepository;

    @Override
    public void validateTransaction(TransactionRequest request) {
        if(request.getFromAccount() == null || request.getToAccount() == null) {
            throw new IllegalArgumentException("Uno dei due conti non è specificato");
        }
        Account account = accountRepository.findById(request.getFromAccount())
                .orElseThrow(() -> new NoEntityFoundException("Nessun account trovato"));
        if(account.getBalance().compareTo(request.getAmount()) <= 0) {
            throw new IllegalArgumentException("Non hai abbastanza soldi per il bonifico");
        }

        if(account.getCurrency().equals(account.getCurrency())) {
            throw new IllegalArgumentException("Guarda che fai un bonifico interno con valute diverse");
        }
    }

    @Override
    public Type getType() {
        return Type.INTERNAL_TRANSFER;
    }
    
}
