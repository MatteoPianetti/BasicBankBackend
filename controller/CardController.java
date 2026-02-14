package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.card.CardPagination;
import com.example.demo.dto.card.CardRequest;
import com.example.demo.dto.card.CardResponse;
import com.example.demo.dto.transaction.TransactionPagination;
import com.example.demo.service.card.CardService;

import lombok.RequiredArgsConstructor;

import java.nio.file.AccessDeniedException;
import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class CardController {
    private final CardService cardService;

    @PostMapping("/create/for/{accountId}")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<CardResponse> createCard(
        @RequestBody CardRequest request,
        @PathVariable Long accountId
    ) {
        CardResponse response = cardService.createCard(request, accountId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/activate/{cardId}")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<Void> activateCard(
        @PathVariable Long cardId
    ) {
        cardService.activateCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/block/{cardId}")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<Void> blockCard(
        @PathVariable Long cardId
    ) {
        cardService.blockCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/details/{cardId}")
    @PreAuthorize("hasRole('TELLER')")
    public ResponseEntity<CardResponse> cardDetails(
        @PathVariable Long cardId
    ) {
        return ResponseEntity.ok(cardService.cardDetails(cardId));
    }

    @PutMapping("/updateLimits/{cardId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> updateCardLimits(
        @PathVariable Long cardId,
        @RequestBody CardRequest request,
        Principal principal
    ) throws AccessDeniedException{
        cardService.updateLimits(cardId, request, principal);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/usersCards")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardPagination> usersCard(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Principal principal
    ) {
        CardPagination pagination = cardService.usersCard(principal, page, size);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/cardTransactions/{cardId}")
    @PreAuthorize("hasAnyRole('USER', 'TELLER', 'BANK_MANAGER')")
    public ResponseEntity<TransactionPagination> transactionsOfCard(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @PathVariable Long cardId
    ) {
        TransactionPagination pagination = cardService.transactionsOfCard(cardId, page, size);
        return ResponseEntity.ok(pagination);
    }
}
