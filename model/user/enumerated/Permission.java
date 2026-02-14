package com.example.demo.model.user.enumerated;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    //CLIENTI
    CUSTOMER_READ("customer:read"),
    CUSTOMER_CREATE("customer:create"),
    CUSTOMER_UPDATE("customer:update"),
    CUSTOMER_DELETE("customer:delete"),

    //CONTI CORRENTI
    ACCOUNT_READ("account:read"),
    ACCOUNT_CREATE("account:create"),
    ACCOUNT_UPDATE("account:update"),
    ACCOUNT_CLOSE("account:close"),

    //TRANSAZIONI
    TRANSACTION_READ("transaction:read"),
    TRANSACTION_CREATE("transaction:create"),
    TRANSACTION_APPROVE("transaction:update"),
    TRANSACTION_CANCEL("transaction:cancel"),
    TRANSACTION_REVERSE("transaction:reverse"),

    //BONIFICI
    TRANSFER_INTERNAL("transfer:internal"),
    TRANSFER_EXTERNAL("transfer:external"),

    //PRESTITI
    LOAN_READ("load:read"),
    LOAN_CREATE("load:create"),
    LOAN_APPROVE("load:update"),
    LOAN_MANAGE("load:delete"),

    //CARDS
    CARD_READ("card:read"),
    CARD_CREATE("card:create"),
    CARD_BLOCK("card:block"),
    CARD_REISSUE("card:reissue"),
    
    //INVESTIMENTI 
    INVESTMENT_READ("investment:read"),
    INVESTMENT_CREATE("investment:create"),
    INVESTMENT_MANAGE("investment:manage"),
    
    //SICUREZZA 
    USER_MANAGE("user:manage"),
    ROLE_MANAGE("role:manage"),
    PERMISSION_MANAGE("permission:manage"),
    
    //OPERAZIONI SPECIALI
    OVERDRAFT_APPROVE("overdraft:approve"),
    LIMIT_ADJUST("limit:adjust"),
    FRAUD_MANAGE("fraud:manage"),
    
    //AMMINISTRAZIONE 
    SYSTEM_CONFIG("system:config"),
    BACKUP_MANAGE("backup:manage"),
    LOG_VIEW("log:view");

    @Getter
    private final String permission;
}
