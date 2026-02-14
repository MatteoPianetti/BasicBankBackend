package com.example.demo.service.card;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.dto.card.*;
import com.example.demo.dto.transaction.TransactionPagination;
import com.example.demo.dto.transaction.TransactionResponse;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.NoEntityFoundException;
import com.example.demo.model.account.Account;
import com.example.demo.model.transaction.*;
import com.example.demo.model.user.User;
import com.example.demo.model.card.Card;
import com.example.demo.model.card.Type;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CardRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CvvHasher cvvHasher;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public CardResponse createCard(
        CardRequest request,
        Long account_id
    ) {
        Account a = accountRepository.findById(account_id)
                .orElseThrow(() -> new NoEntityFoundException("Nessun account trovato"));
    
        String cvvHash = cvvHasher.hash(request.getCvv());

        Card c = Card.builder()
            .numeroCarta(request.getNumeroCarta())
            .cvvHash(cvvHash)
            .cardHolder(request.getCardHolder())
            .type(request.getType())
            .expireDate(request.getExpirDate())
            .dailyLimit(request.getDailyLimit())
            .account(a)
            .build();

        cardRepository.save(c);

        return CardResponse.from(c);
    }

    public void activateCard(Long cardId) {
        Card c = cardRepository.findById(cardId)
                .orElseThrow(() -> new NoEntityFoundException("Nessuna carta trovata"));

        if(c.getType() != Type.BLOCKED) {
            throw new BusinessException("Puoi attivare solo carte bloccate");
        }

        c.setType(Type.ACTIVE);
        cardRepository.save(c);
    }

    public void blockCard(Long cardId) {
        Card c = cardRepository.findById(cardId)
                .orElseThrow(() -> new NoEntityFoundException("Nessuna carta trovata"));

        if(c.getType() != Type.ACTIVE) {
            throw new BusinessException("Puoi bloccare solo carte attive");
        }

        c.setType(Type.BLOCKED);
        cardRepository.save(c);
    }

    public CardResponse cardDetails(Long cardId) {
         Card c = cardRepository.findById(cardId)
                .orElseThrow(() -> new NoEntityFoundException("Nessuna carta trovata"));

        return CardResponse.from(c);
    }

    public void updateLimits(
        Long cardId,
        CardRequest request,
        Principal principal
    ) throws AccessDeniedException{
        User loggedUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NoEntityFoundException("Nessuno user trovato"));

        Account loggedAccount = accountRepository.findByUserId(loggedUser.getId())
                .orElseThrow(() -> new NoEntityFoundException("Nessun conto trovato"));
        
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NoEntityFoundException("Nessuna carta trovata"));

        if(!card.getAccount().getId().equals(loggedAccount.getId())) {
            throw new AccessDeniedException("Non puoi modificare i limiti di un altra carta");
        }
        
        if(request.getDailyLimit().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Stai impostando i limiti a un valore negativo");
        }
        
        card.setDailyLimit(request.getDailyLimit());
        cardRepository.save(card);
    }

    public CardPagination usersCard(Principal principal, int page, int size) {
        User loggedUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NoEntityFoundException("Nessun user trovato"));

        Account loggedAccount = accountRepository.findByUserId(loggedUser.getId())
                .orElseThrow(() -> new NoEntityFoundException("Nessun conto trovato"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cards = cardRepository.findByAccountId(loggedAccount.getId(), pageable);
        List<Card> listOfCards = cards.getContent();
        List<CardResponse> content = listOfCards.stream().map(CardResponse::from).toList();

        CardPagination pag = new CardPagination();
        pag.setContent(content);
        pag.setPages(cards.getNumber());
        pag.setPageSize(cards.getSize());
        pag.setTotalElements(cards.getTotalElements());
        pag.setTotalPages(cards.getTotalPages());
        pag.setLast(cards.isLast());

        return pag;
    }

    public TransactionPagination transactionsOfCard(
        Long cardId,
        int page, 
        int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findByCardId(cardId, pageable);
        List<Transaction> listOfTransactions = transactions.getContent();
        List<TransactionResponse> content = listOfTransactions.stream().map(TransactionResponse::from).toList(); 

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
