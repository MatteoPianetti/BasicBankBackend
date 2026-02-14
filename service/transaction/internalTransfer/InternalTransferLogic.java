package com.example.demo.service.transaction.internalTransfer;

import com.example.demo.model.transaction.Status;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.Type;
import com.example.demo.service.transaction.Logic;

import jakarta.transaction.Transactional;

public class InternalTransferLogic implements Logic{

    @Override
    @Transactional
    public Transaction execute(Transaction tx) {
       tx.getFromAccount().setBalance(tx.getFromAccount().getBalance().subtract(tx.getAmount()));
       tx.getToAccount().setBalance(tx.getToAccount().getBalance().add(tx.getAmount()));

       tx.setStatus(Status.APPROVED);
       return tx;
    }

    @Override
    public Type getType() {
       return Type.INTERNAL_TRANSFER;
    }

}
