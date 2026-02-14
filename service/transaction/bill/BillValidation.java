package com.example.demo.service.transaction.bill;

import com.example.demo.dto.transaction.BillRequest;
import com.example.demo.dto.transaction.TransactionRequest;
import com.example.demo.exception.NoEntityFoundException;
import com.example.demo.model.account.Account;
import com.example.demo.model.transaction.Type;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.BillRepository;
import com.example.demo.service.transaction.Validator;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillValidation implements Validator{
    private final BillRepository billRepository;
    private final AccountRepository accountRepository;

    @Override
    public void validateTransaction(TransactionRequest request) {
        BillRequest req = (BillRequest) request;
        if(req.getFromAccount() == null) {
            throw new IllegalArgumentException("Il from account deve esistere");
        }

        if(req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("L'amount dev'essere superiore a zero");
        }

        Account account = accountRepository.findById(request.getFromAccount())
                .orElseThrow(() -> new NoEntityFoundException("Nessun account trovato"));
        if(req.getAmount().compareTo(account.getBalance()) > 0) {
            throw new IllegalArgumentException("Fondi insufficienti");
        }

        if(billRepository.findById(req.getBillId()) == null) {
            throw new IllegalArgumentException("Nessun id per il fornitore trovato");
        } 
    }   

    @Override
    public Type getType() {
        return Type.BILL_PAYMENT;
    }
        
}
