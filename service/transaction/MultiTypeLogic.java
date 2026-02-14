package com.example.demo.service.transaction;

import java.util.Set;

import com.example.demo.model.transaction.Type;

public interface MultiTypeLogic {
    Set<Type> getSupportedTypes();
}
