package com.example.demo.service.transaction;

import com.example.demo.model.transaction.*;

public interface Logic {
    Transaction execute(Transaction tx);
    Type getType();
}
