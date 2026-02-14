package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.acount.AccountRequest;
import com.example.demo.dto.acount.AccountResponse;
import com.example.demo.dto.acount.BlockRequest;
import com.example.demo.dto.acount.DailyTransactionPagination;
import com.example.demo.dto.acount.LimitRequest;
import com.example.demo.dto.acount.TransactionAccountPagination;
import com.example.demo.service.AccountService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<AccountResponse> createAccount(
        @RequestBody AccountRequest request
    ) {
        AccountResponse response = accountService.createAccount(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<AccountResponse> getById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @GetMapping("/iban/{iban}")
    @PreAuthorize("hasAnyRole('USER', 'TELLER', 'BANK_MANAGER')")
    public ResponseEntity<AccountResponse> getByIabn(
        @PathVariable String iban
    ) {
        return ResponseEntity.ok(accountService.findByIban(iban));
    }

    @GetMapping("/{accountId}/transactionsStats")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<TransactionAccountPagination> getTransactionsStats(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @PathVariable Long accountId
    ) {
        TransactionAccountPagination pagination = accountService.getTransactionsStats(page, size, accountId);
        return ResponseEntity.ok(pagination);
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<Void> blockAccount(
        @PathVariable Long id,
        @RequestBody BlockRequest request
    ) {
        accountService.blockAccount(request, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/acitvate")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<Void> activateAccount(
        @PathVariable Long id
    ) {
        accountService.activateAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<Void> closeAccount(
        @PathVariable Long id
    ) {
        accountService.closeAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/userId/{userId}")
    @PreAuthorize("hasAnyRole('TELLER', 'BANK_MANAGER')")
    public ResponseEntity<AccountResponse> getByUserId(
        @PathVariable Long userId
    ) {
        return ResponseEntity.ok(accountService.findByUserId(userId));
    }

    @PutMapping("/{id}/update/limits")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> updateAccountLimits(
        @PathVariable Long id,
        @RequestBody LimitRequest request,
        Principal principal
    ) {
        accountService.updateAccountLimits(id, request, principal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dailyTransactions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<DailyTransactionPagination> getDailyTransactions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam LocalDate day
    ) {
        DailyTransactionPagination pagination = accountService.getDailyTransactions(page, size, day);
        return ResponseEntity.ok(pagination);
    }
}
