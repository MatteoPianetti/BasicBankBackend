package com.example.demo.service.transaction.externalTransfer;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.demo.dto.transaction.TransactionRequest;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.NoEntityFoundException;
import com.example.demo.model.transaction.Type;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.transaction.MultiTypeValidator;
import com.example.demo.service.transaction.Validator;
import com.example.demo.model.account.*;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class externalPaymentValidation implements Validator, MultiTypeValidator{
    private final AccountRepository accountRepository;
    @Override
    public Set<Type> getSupportedTypes() {
        return Set.of(
            Type.SEPA_TRANSFER,
            Type.WIRE_TRANSFER,
            Type.INSTANT_TRANSFER
        );
    }

    @Override
    public void validateTransaction(TransactionRequest request) {
        if (request == null) {
        throw new IllegalArgumentException("Richiesta transazione nulla");
        }

        if (request.getType() == null) {
            throw new BusinessException("Tipo di transazione mancante");
        }

        if (request.getAmount() == null) {
            throw new BusinessException("Importo mancante");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("L'importo deve essere maggiore di zero");
        }

        // Tipologie supportate
        Set<Type> allowedTypes = Set.of(
            Type.SEPA_TRANSFER,
            Type.WIRE_TRANSFER,
            Type.INSTANT_TRANSFER
        );

        if (!allowedTypes.contains(request.getType())) {
            throw new BusinessException("Tipo di transazione non supportato: " + request.getType());
        }

        Account account = accountRepository.findById(request.getToAccount())
                .orElseThrow(() -> new NoEntityFoundException("Nessun account trovato"));

        if (request.getType() == Type.SEPA_TRANSFER) {
            if (account.getAccountNumber() == null || account.getAccountNumber().isBlank()) {
                throw new BusinessException("IBAN obbligatorio per bonifico SEPA");
            }

            if (!account.getAccountNumber().startsWith("IT")) {
                throw new BusinessException("IBAN non valido per bonifico SEPA");
            }
        }

        // WIRE
        if (request.getType() == Type.WIRE_TRANSFER) {
            if (request.getSwift() == null || request.getSwift().isBlank()) {
                throw new BusinessException("SWIFT obbligatorio per bonifico internazionale");
            }

            if (request.getCountry() == null || request.getCountry().isBlank()) {
                throw new BusinessException("Paese destinatario obbligatorio per bonifico internazionale");
            }
        }

        // INSTANT
        if (request.getType() == Type.INSTANT_TRANSFER) {
            if (account.getAccountNumber() == null || account.getAccountNumber().isBlank()) {
                throw new BusinessException("IBAN obbligatorio per bonifico istantaneo");
            }

            BigDecimal instantLimit = new BigDecimal("15000");
            if (request.getAmount().compareTo(instantLimit) > 0) {
                throw new BusinessException("Importo troppo alto per bonifico istantaneo (max 15000€)");
            }
        }
    }

    @Override
    public Type getType() {
        return null;
    }

}
