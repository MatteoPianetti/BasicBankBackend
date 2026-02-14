package com.example.demo.service.transaction.deposit;

import org.springframework.stereotype.Component;

import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.Type;
import com.example.demo.model.account.Account;
import com.example.demo.model.transaction.Status;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.transaction.Logic;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DepositLogic implements Logic{
    private final AccountRepository accountRepository;

    @Transactional
    public Transaction execute(Transaction tx) {
        Account to = tx.getToAccount();
        to.setBalance(to.getBalance().add(tx.getAmount()));
        
        accountRepository.save(to);

        tx.setStatus(Status.APPROVED);

        return tx;
    }

    public Type getType() {return Type.DEPOSIT;} 
}
