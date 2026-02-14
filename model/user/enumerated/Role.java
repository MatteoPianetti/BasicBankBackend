package com.example.demo.model.user.enumerated;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static com.example.demo.model.user.enumerated.Permission.*;

@RequiredArgsConstructor
public enum Role {
    //cliente della banca
    USER(
        Set.of(
            CUSTOMER_READ,
            ACCOUNT_READ,
            TRANSACTION_READ,
            TRANSFER_INTERNAL,
            TRANSFER_EXTERNAL,
            LOAN_READ,
            CARD_READ,
            CARD_BLOCK,
            INVESTMENT_READ,
            INVESTMENT_MANAGE,
            INVESTMENT_CREATE,
            LIMIT_ADJUST
        )
    ), 

    //ti serve allo sportello
    TELLER(
        Set.of(
            CUSTOMER_READ,
            CUSTOMER_CREATE,
            CUSTOMER_UPDATE,
            CUSTOMER_DELETE,
            ACCOUNT_CREATE,
            ACCOUNT_CLOSE,
            ACCOUNT_UPDATE,
            TRANSACTION_APPROVE,
            TRANSACTION_CANCEL,
            TRANSACTION_REVERSE,
            LOAN_CREATE,
            LOAN_APPROVE,
            LOAN_MANAGE,
            CARD_REISSUE,
            CARD_CREATE,
            USER_MANAGE,
            OVERDRAFT_APPROVE
        )
    ),

    BANK_MANAGER(
        Set.of(
            CUSTOMER_READ,
            CUSTOMER_CREATE,
            CUSTOMER_UPDATE,
            CUSTOMER_DELETE,
            ACCOUNT_CREATE,
            ACCOUNT_CLOSE,
            ACCOUNT_UPDATE,
            TRANSACTION_APPROVE,
            TRANSACTION_CANCEL,
            TRANSACTION_REVERSE,
            LOAN_CREATE,
            LOAN_APPROVE,
            LOAN_MANAGE,
            CARD_REISSUE,
            CARD_CREATE,
            USER_MANAGE,
            OVERDRAFT_APPROVE,
            ROLE_MANAGE,
            PERMISSION_MANAGE,
            SYSTEM_CONFIG,
            BACKUP_MANAGE,
            LOG_VIEW
        )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
