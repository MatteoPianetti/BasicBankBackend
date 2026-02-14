package com.example.demo.service.transaction.withdraw;

import com.example.demo.model.transaction.Status;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.Type;
import com.example.demo.service.transaction.Logic;

import jakarta.transaction.Transactional;

public class WithdrawlLogic implements Logic{

    @Override
    @Transactional  
    public Transaction execute(Transaction tx) {
        tx.getFromAccount().setBalance(tx.getFromAccount().getBalance().subtract(tx.getAmount()));
        tx.setStatus(Status.APPROVED);

        return tx;
    }

    @Override
    public Type getType() {
        return Type.WITHDRAWAL;
    }
    
}
