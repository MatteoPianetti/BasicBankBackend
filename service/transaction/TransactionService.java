package com.example.demo.service.transaction;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.dto.transaction.DeleteRequest;
import com.example.demo.dto.transaction.TransactionPagination;
import com.example.demo.dto.transaction.TransactionRequest;
import com.example.demo.dto.transaction.TransactionResponse;
import com.example.demo.exception.NoEntityFoundException;
import com.example.demo.model.account.Account;
import com.example.demo.model.card.Card;
import com.example.demo.model.transaction.Status;
import com.example.demo.model.transaction.Transaction;
import com.example.demo.model.user.*;
import com.example.demo.model.user.enumerated.Role;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionFactory transactionFactory;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionResponse createTransaction(
        TransactionRequest request,
        Principal principal
    ) throws AccessDeniedException{
        Validator val = transactionFactory.getValidator(request.getType());
        val.validateTransaction(request);

        Card card = null;
        if(request.getCardId() != null) {
            card = cardRepository.findById(request.getCardId())
                .orElseThrow(() -> new NoEntityFoundException("Nessuna carta trovata"));
        }  
        
        User loggedUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NoEntityFoundException("Nessun user trovato"));

        Account fromAccount = accountRepository.findByIdAndUserId(request.getFromAccount(), loggedUser.getId()) 
                .orElseThrow(() -> new AccessDeniedException("Stai creando una transazione per un account non tuo"));

        Account toAccount = accountRepository.findById(request.getToAccount())
                .orElseThrow(() -> new NoEntityFoundException("Il destinatario non esiste"));
        Transaction transaction = Transaction.builder()
            .amount(request.getAmount())
            .currency(request.getCurrency())
            .type(request.getType())
            .status(Status.PENDING)
            .description(request.getDescription())
            .fromAccount(fromAccount)
            .toAccount(toAccount)
            .card(card)
            .timeStamp(LocalDateTime.now())
            .reason(request.getReason())
            .build();

        transactionRepository.save(transaction);

        return TransactionResponse.from(transaction);
    }

    @Transactional
    public void approveTransaction(
        Long transactionId,
        Principal principal
    ) throws AccessDeniedException {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NoEntityFoundException("Nessuna transazione trovata"));
        
        User a = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NoEntityFoundException("Nessun user trovato"));
        
        if(a.getRole() != Role.TELLER && a.getRole() != Role.BANK_MANAGER) {
            throw new AccessDeniedException("Lo user non è autorizzato ad approvare la transazione");
        }

        if(tx.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Solo le transazioni in stato pending possono essere approvate");
        }

        Logic logic = transactionFactory.getLogic(tx.getType());
        logic.execute(tx);
        transactionRepository.save(tx);
    }

    public void deleteTransaction(
        Long transactionId,
        DeleteRequest request 
    ) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NoEntityFoundException("Nessuna transaction trovata"));
            
        tx.setStatus(Status.CANCELLED);
        tx.setReason(request.getReason());
        
        transactionRepository.save(tx);
    }

    public void rejectTransaction(
        Long transactionId,
        String reason
    ) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NoEntityFoundException("Nessuna transaction trovata"));
            
        tx.setStatus(Status.REJECTED);
        tx.setReason(reason);
        
        transactionRepository.save(tx);
    }

    public TransactionResponse findById(Long transactionId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NoEntityFoundException("Nessuna transaction trovata"));

        return TransactionResponse.from(tx);
    }

    public TransactionPagination findByAccountWorkers(int page, int size, Long accountId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByAccount(accountId, pageable);
        List<Transaction> listOfTransactions = transactions.getContent();
        List<TransactionResponse> content = listOfTransactions
                .stream()
                .map(TransactionResponse::from)
                .toList();

        TransactionPagination pag = new TransactionPagination();
        pag.setContent(content);
        pag.setPages(transactions.getNumber());
        pag.setPageSize(transactions.getSize());
        pag.setTotalElements(transactions.getTotalElements());
        pag.setTotalPages(transactions.getTotalPages());
        pag.setLast(transactions.isLast());

        return pag;
    }

    public TransactionPagination findByAccountUsers(
        int page, 
        int size, 
        Long accountId, 
        Principal principal
    )throws AccessDeniedException {
        User loggedUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NoEntityFoundException("Nessun user trovato"));
        Account account = accountRepository.findByIdAndUserId(accountId, loggedUser.getId())
                .orElseThrow(() -> new AccessDeniedException("Non sei il proprietario di questo account"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByAccount(account.getId(), pageable);
        List<Transaction> listOfTransactions = transactions.getContent();
        List<TransactionResponse> content = listOfTransactions
                .stream()
                .map(TransactionResponse::from)
                .toList();

        TransactionPagination pag = new TransactionPagination();
        pag.setContent(content);
        pag.setPages(transactions.getNumber());
        pag.setPageSize(transactions.getSize());
        pag.setTotalElements(transactions.getTotalElements());
        pag.setTotalPages(transactions.getTotalPages());
        pag.setLast(transactions.isLast());

        return pag;
    }



    public TransactionPagination findPendingTransactions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByStatus(Status.PENDING, pageable);
        List<Transaction> listOfTransactions = transactions.getContent();
        List<TransactionResponse> content = listOfTransactions
                .stream()
                .map(TransactionResponse::from)
                .toList();

        TransactionPagination pag = new TransactionPagination();
        pag.setContent(content);
        pag.setPages(transactions.getNumber());
        pag.setPageSize(transactions.getSize());
        pag.setTotalElements(transactions.getTotalElements());
        pag.setTotalPages(transactions.getTotalPages());
        pag.setLast(transactions.isLast());

        return pag;
    }

    public TransactionPagination findByPeriod(int page, int size, LocalDateTime from, LocalDateTime to) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByPeriod(from, to, pageable);
        List<Transaction> listOfTransactions = transactions.getContent();
        List<TransactionResponse> content = listOfTransactions
                .stream()
                .map(TransactionResponse::from)
                .toList();

        TransactionPagination pag = new TransactionPagination();
        pag.setContent(content);
        pag.setPages(transactions.getNumber());
        pag.setPageSize(transactions.getSize());
        pag.setTotalElements(transactions.getTotalElements());
        pag.setTotalPages(transactions.getTotalPages());
        pag.setLast(transactions.isLast());

        return pag;
    }
}
