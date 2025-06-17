package ru.motorinsurance.kasko.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    YOO_KASSA("ЮKassa"),
    SBP("СБП"),
    BANK_TRANSFER("Банковский перевод"),
    CASH("Наличные");

    private final String russianName;

    PaymentMethod(String russianName) {
        this.russianName = russianName;
    }

    // Пример: сериализация для API
    public String toApiValue() {
        return this.russianName;
    }

    @Override
    public String toString() {
        return this.russianName;
    }
}