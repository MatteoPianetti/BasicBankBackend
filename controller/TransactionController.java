package com.example.demo.controller;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.transaction.DeleteRequest;
import com.example.demo.dto.transaction.TransactionPagination;
import com.example.demo.dto.transaction.TransactionRequest;
import com.example.demo.dto.transaction.TransactionResponse;
import com.example.demo.service.transaction.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionResponse> createTransaction(
        @RequestBody TransactionRequest request,
        Principal principal
    ) throws AccessDeniedException {
        return ResponseEntity.ok(transactionService.createTransaction(request, principal));
    } 

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('TELLER')")
    public ResponseEntity<Void> approveTransaction(
        @PathVariable Long id,
        Principal principal
    ) throws AccessDeniedException{
        transactionService.approveTransaction(id, principal);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<Void> deleteTransaciton(
        @PathVariable Long id,
        @RequestBody DeleteRequest request
    ) {
        transactionService.deleteTransaction(id, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<Void> rejectTransaction(
        @PathVariable Long id,
        @RequestParam String reason
    ) {
        transactionService.rejectTransaction(id, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/transaction")
    @PreAuthorize("hasAnyRole('USER', 'TELLER', 'BANK_MANAGER')")
    public ResponseEntity<TransactionResponse> getById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @GetMapping("/transactions/workers/{accountId}") 
    @PreAuthorize("hasRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<TransactionPagination> getByAccountWorkers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @PathVariable Long accountId
    ) {
        TransactionPagination pagination = transactionService.findByAccountWorkers(page, size, accountId);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/transactions/users/{accountId}") 
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionPagination> getByAccountForUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @PathVariable Long accountId,
        Principal principal
    ) throws AccessDeniedException{
        TransactionPagination pagination = transactionService.findByAccountUsers(page, size, accountId, principal);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<TransactionPagination> getPendingTransactions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        TransactionPagination pagination = transactionService.findPendingTransactions(page, size);
        return ResponseEntity.ok(pagination);
    }

    @GetMapping("/period")
    @PreAuthorize("hasRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<TransactionPagination> getPeriodTransactions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam LocalDateTime from,
        @RequestParam LocalDateTime to
    ) {
        TransactionPagination pagination = transactionService.findByPeriod(page, size, from, to);
        return ResponseEntity.ok(pagination);
    }
}
