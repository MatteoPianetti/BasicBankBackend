package com.example.demo.service.transaction.cardPayment;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.demo.model.account.Account;
import com.example.demo.model.card.Card;
import com.example.demo.model.transaction.Status;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.Type;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.transaction.Logic;
import com.example.demo.service.transaction.MultiTypeLogic;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CardPaymentLogic implements Logic, MultiTypeLogic{
    private final AccountRepository accountRepository;

    @Override
    public Set<Type> getSupportedTypes() {
       return Set.of(
        Type.CARD_PAYMENT,
        Type.ONLINE_PAYMENT,
        Type.POS_PAYMENT,
        Type.SUBSCRIPTION_PAYMENT
       );
    }

    @Override
    @Transactional
    public Transaction execute(Transaction tx) {
        Card card = tx.getCard();
        Account account = tx.getFromAccount();
        BigDecimal amount = tx.getAmount();

        if(card == null) {throw new IllegalStateException("Pagamento con carta senza carta");}
        if(account.getBalance().compareTo(amount) < 0) {throw new IllegalStateException("Non hai i soldi");}
        if(card.getDailyLimit() != null && amount.compareTo(card.getDailyLimit()) > 0) {
            tx.setStatus(Status.REJECTED);
            throw new IllegalStateException("Limite giornaliero carta superato");
        }

        account.setBalance(account.getBalance().subtract(amount));
        tx.setStatus(Status.COMPLETED);
        accountRepository.save(account);

        return tx;
    }

    @Override
    public Type getType() {
        return null;
    }
    
}
