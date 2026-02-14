package com.example.demo.service.transaction;

import com.example.demo.dto.transaction.TransactionRequest;
import com.example.demo.model.transaction.Type;

public interface Validator {
    void validateTransaction(TransactionRequest request);
    Type getType();
}
