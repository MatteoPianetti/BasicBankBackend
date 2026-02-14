package com.example.demo.service.transaction.cardPayment;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.demo.dto.transaction.TransactionRequest;
import com.example.demo.exception.NoEntityFoundException;
import com.example.demo.model.account.Account;
import com.example.demo.model.transaction.Type;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.transaction.MultiTypeValidator;
import com.example.demo.service.transaction.Validator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CardPaymentValidator implements Validator, MultiTypeValidator{
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
    public void validateTransaction(TransactionRequest request) {
        if (request.getCardId() == null) {
            throw new IllegalArgumentException("Card obbligatoria per pagamenti con carta");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Importo non valido");
        }
        Account account = accountRepository.findById(request.getFromAccount())
                .orElseThrow(() -> new NoEntityFoundException("Nessun account trovato"));
        if (account.getId() == null) {
            throw new IllegalArgumentException("Account sorgente obbligatorio");
        }
    }

    @Override
    public Type getType() {
       return null;
    }
    
}
