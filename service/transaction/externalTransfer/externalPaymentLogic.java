package com.example.demo.service.transaction.externalTransfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.demo.exception.BusinessException;
import com.example.demo.model.account.Account;
import com.example.demo.model.transaction.Status;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.Type;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.transaction.Logic;
import com.example.demo.service.transaction.MultiTypeLogic;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class externalPaymentLogic implements Logic, MultiTypeLogic{
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    @Override
    public Set<Type> getSupportedTypes() {
        return Set.of(
            Type.SEPA_TRANSFER,
            Type.WIRE_TRANSFER,
            Type.INSTANT_TRANSFER
        );
    }

    @Override
    @Transactional
    public Transaction execute(Transaction tx) {
        if (tx == null) {
        throw new IllegalArgumentException("Transazione nulla");
        }

        if (tx.getAmount() == null || tx.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Importo transazione non valido");
        }

        Account senderAccount = tx.getFromAccount();
        Account receiverAccount = tx.getToAccount(); // può essere null per esterni (SEPA/WIRE)

        if (senderAccount == null) {
            throw new BusinessException("Conto mittente mancante");
        }

        // 🔐 Lock logico (coerenza concorrenza)
        accountRepository.lockById(senderAccount.getId());

        // 🔎 Reload entity (consistenza DB)
        senderAccount = accountRepository.findById(senderAccount.getId())
                .orElseThrow(() -> new BusinessException("Conto mittente non trovato"));

        // 💰 Controllo saldo
        if (senderAccount.getBalance().compareTo(tx.getAmount()) < 0) {
            throw new BusinessException("Saldo insufficiente");
        }

        // ➖ Addebito
        senderAccount.setBalance(
            senderAccount.getBalance().subtract(tx.getAmount())
        );

        accountRepository.save(senderAccount);

        // ➕ Accredito (solo se conto interno)
        if (receiverAccount != null) {

            accountRepository.lockById(receiverAccount.getId());

            receiverAccount = accountRepository.findById(receiverAccount.getId())
                    .orElseThrow(() -> new BusinessException("Conto destinatario non trovato"));

            receiverAccount.setBalance(
                receiverAccount.getBalance().add(tx.getAmount())
            );

            accountRepository.save(receiverAccount);
        }

        // 🧾 Stato transazione
        tx.setStatus(Status.COMPLETED);
        tx.setTimeStamp(LocalDateTime.now());

        return transactionRepository.save(tx);
    }

    @Override
    public Type getType() {
       return null;
    }

}
