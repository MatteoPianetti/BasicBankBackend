package com.example.demo.service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.model.account.Account;
import com.example.demo.model.account.Status;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.transaction.Type;
import com.example.demo.model.user.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

import com.example.demo.dto.acount.*;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.NoEntityFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private void validateString(String s) {
        if(s == null || s.isBlank()) {
            throw new IllegalArgumentException("La stringa fornita non è valida");
        }
    }

    @Transactional
    public AccountResponse createAccount(
        AccountRequest request
    ) {
        validateString(request.getAccountNumber());
        validateString(request.getCurrency());

        if(request.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Un nuovo conto corrente non può avere un saldo negativo");
        }

        if(request.getStatus() != Status.ACTIVE) {
            throw new IllegalArgumentException("Un nuovo conto corrente, dev'essere attivo");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoEntityFoundException("Nessun utente trovato"));

        Account account = Account.builder()
            .accountNumber(request.getAccountNumber())
            .balance(request.getBalance())
            .currency(request.getCurrency())
            .status(request.getStatus())
            .user(user)
            .createdAt(LocalDate.now())
            .outgoingTransactions(new ArrayList<>())
            .incomingTransactions(new ArrayList<>())
            .build();
        
        accountRepository.save(account);

        return AccountResponse.from(account);
    }

    public AccountResponse findById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoEntityFoundException("Nessun account trovato con quell'id"));
        
        return AccountResponse.from(account);
    }

    public AccountResponse findByIban(String iban) {
        Account account = accountRepository.findByAccountNumber(iban)
                .orElseThrow(() -> new NoEntityFoundException("Nessun account trovato con quell'iban"));
        
        return AccountResponse.from(account);
    }

    public TransactionAccountPagination getTransactionsStats(int page, int size, Long accountId) {
        Pageable pageable = PageRequest.of(page, size);
        
        //Query unica
        Page<Transaction> pageTx = transactionRepository.findByAccountId(accountId, pageable);

        //Raggruppamento per tipo
        Map<Type, List<Transaction>> grouped = 
            pageTx.getContent()
            .stream()
            .collect(Collectors.groupingBy(Transaction::getType));
        
        List<TransactionAccountResponse> content = new ArrayList<>();

        for(Type type : Type.values()) {
            List<Transaction> list = grouped.getOrDefault(type, List.of());
            content.add(TransactionAccountResponse.from(list));
        }

        TransactionAccountPagination pag = new TransactionAccountPagination();
        pag.setContent(content);
        pag.setPages(pageTx.getNumber());
        pag.setPageSize(pageTx.getSize());
        pag.setTotalElements(pageTx.getTotalElements());
        pag.setTotalPages(pageTx.getTotalPages());
        pag.setLast(pageTx.isLast());

        return pag;
    }

    @Transactional
    public void blockAccount(
        BlockRequest request,
        Long accountId
    ) {
        if(request.getReason() == null || request.getReason().isBlank()) {
            throw new BusinessException("Una motivazione è richiesta per il blocco");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoEntityFoundException("Nessun account trovato"));

        if(account.getStatus() == Status.CLOSED) {
            throw new BusinessException("Stai bloccando un conto chiuso");
        }

        account.setStatus(Status.BLOCKED);
        account.setReason(request.getReason());
        account.setBlockedBy(request.getBlockedBy());
        account.setSeverity(request.getSeverity());
        
        accountRepository.save(account);
    }

    @Transactional
    public void activateAccount(
        Long accountId
    ) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoEntityFoundException("Nessun account trovato"));
        
        if(account.getStatus() != Status.BLOCKED) {
            throw new BusinessException("Puoi attivare solo account bloccati");
        }

        account.setStatus(Status.ACTIVE);
        accountRepository.save(account);
    }

    @Transactional
    public void closeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NoEntityFoundException("Nessun account trovato"));
        
        if(account.getStatus() == Status.CLOSED) {
            throw new BusinessException("Stai chiudendo un account già chiuso");
        }

        account.setStatus(Status.CLOSED);
        accountRepository.save(account);
    }

    public AccountResponse findByUserId(Long userId) {
        Account a = accountRepository.findByUserId(userId)
            .orElseThrow(() -> new NoEntityFoundException("Nessun account per lo user"));
        
        return AccountResponse.from(a);
    }

    @Transactional
    public AccountLimitResponse updateAccountLimits(
        Long accountId,
        LimitRequest request,
        Principal principal
    ) {

        User loggedUser= userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NoEntityFoundException("Nessun user trovato con la mail corrente"));

        Account account = accountRepository.findByIdAndUserId(accountId, loggedUser.getId())
                .orElseThrow(() -> new BusinessException("Stai modificando i limiti di un conto non tuo"));

        if(request.getDailyLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Non puoi mettere un daily limit minore di zero");
        }

        if(request.getMonthlyLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Non puoi mettere un monthly limit minore di zero");
        }

        account.setDailyLimit(request.getDailyLimit());
        account.setMonthlyLimit(request.getMonthlyLimit());

        accountRepository.save(account);

        return AccountLimitResponse.from(account);
    }

    public DailyTransactionPagination getDailyTransactions(int page, int size, LocalDate day) {
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end = day.plusDays(1).atStartOfDay(); 
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByDate(start, end, pageable);
        List<Transaction> listOfTransactions = transactions.getContent();
        List<DailyTransactionResponse> content = listOfTransactions
                .stream()
                .map(DailyTransactionResponse::from)
                .toList();

        DailyTransactionPagination pag = new DailyTransactionPagination();
        pag.setContent(content);
        pag.setPageSize(transactions.getSize());
        pag.setPages(transactions.getNumber());
        pag.setTotalElements(transactions.getTotalElements());
        pag.setTotalPages(transactions.getTotalPages());
        pag.setLast(transactions.isLast());

        return pag;
    }
}
