package com.example.demo.service.transaction;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.demo.model.transaction.Type;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionFactory {
    private final Map<Type, Validator> validators;
    private final Map<Type, Logic> logics;

    public Validator getValidator(Type type) {
        return validators.get(type);
    }

    public Logic getLogic(Type type) {
        return logics.get(type);
    }
}
