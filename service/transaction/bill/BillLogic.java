package com.example.demo.service.transaction.bill;

import com.example.demo.model.account.Account;
import com.example.demo.model.transaction.Status;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.Type;
import com.example.demo.service.transaction.Logic;

public class BillLogic implements Logic{

    @Override
    public Transaction execute(Transaction transaction) {
        // cast sicuro se vuoi accedere a campi specifici
        // BillRequest billRequest = (BillRequest) ???  // in genere non serve, abbiamo solo la transaction

        // Aggiorna saldo dell'account di provenienza
        Account from = transaction.getFromAccount();
        from.setBalance(from.getBalance().subtract(transaction.getAmount()));

        // Imposta stato APPROVED
        transaction.setStatus(Status.APPROVED);

        // opzionale: salva la transazione se non è già managed
        // transactionRepository.save(transaction);

        return transaction;
    }

    @Override
    public Type getType() {
        return Type.BILL_PAYMENT;
    }

}
