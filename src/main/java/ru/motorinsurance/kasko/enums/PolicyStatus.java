package ru.motorinsurance.kasko.enums;

import lombok.Getter;

@Getter
public enum PolicyStatus {
    PRE_CALCULATION("Предрасчет"),
    QUOTE_NEW("Котировка_Новое"),
    QUOTE_SENT_FOR_APPROVAL("Котировка_ОтправленаНаСогласование"),
    QUOTE_REWORK("Котировка_Доработка"),
    QUOTE_ISSUED("Котировка_Выпущена"),
    POLICY_NEW("Полис_Новый"),
    POLICY_AWAITING_PAYMENT("Полис_Ожидает_Оплаты"),
    POLICY_ISSUED("Полис_Выпущен"),
    CANCELLED("Отменен");

    private final String russianName;

    PolicyStatus(String russianName) {
        this.russianName = russianName;
    }

    public static PolicyStatus fromRussianName(String russianName) {
        for (PolicyStatus status : values()) {
            if (status.russianName.equals(russianName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Неизвестный статус: " + russianName);
    }

    @Override
    public String toString() {
        return this.russianName;
    }

}
