package com.example.demo.model.transaction;

public enum Type {

    DEPOSIT(Direction.IN, Nature.EXTERNAL, EconomicType.CASH_FLOW),
    WITHDRAWAL(Direction.OUT, Nature.EXTERNAL, EconomicType.CASH_FLOW),
    REFUND(Direction.IN, Nature.EXTERNAL, EconomicType.CASH_FLOW),
    CHARGEBACK(Direction.IN, Nature.EXTERNAL, EconomicType.CASH_FLOW),

    INTERNAL_TRANSFER(Direction.INTERNAL, Nature.INTERNAL, EconomicType.MOVEMENT),

    BILL_PAYMENT(Direction.OUT, Nature.EXTERNAL, EconomicType.PAYMENT),
    SUBSCRIPTION_PAYMENT(Direction.OUT, Nature.EXTERNAL, EconomicType.PAYMENT),

    SEPA_TRANSFER(Direction.OUT, Nature.EXTERNAL, EconomicType.BANKING),
    WIRE_TRANSFER(Direction.OUT, Nature.EXTERNAL, EconomicType.BANKING),
    INSTANT_TRANSFER(Direction.OUT, Nature.EXTERNAL, EconomicType.BANKING),
    CARD_PAYMENT(Direction.OUT, Nature.EXTERNAL, EconomicType.BANKING),
    POS_PAYMENT(Direction.OUT, Nature.EXTERNAL, EconomicType.BANKING),
    ONLINE_PAYMENT(Direction.OUT, Nature.EXTERNAL, EconomicType.BANKING);

    public final Direction direction;
    public final Nature nature;
    public final EconomicType economicType;

    Type(Direction direction, Nature nature, EconomicType economicType) {
        this.direction = direction;
        this.nature = nature;
        this.economicType = economicType;
    }
}
