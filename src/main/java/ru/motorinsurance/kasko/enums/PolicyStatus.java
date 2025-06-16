package ru.motorinsurance.kasko.enums;

public enum PolicyStatus {
    PRE_CALCULATION, // Предрасчет: Новое
    QUOTATION_NEW,      // Котировка: Новое
    QUOTATION_PENDING_APPROVAL, // Котировка: Отправлена на согласование
    QUOTATION_ISSUED,   // Котировка: Выпущена
    QUOTATION_REVISION, // Котировка: Доработка
    POLICY_NEW,          // Полис: Новый
    POLICY_AWAIT_PAYMENT, // Полис: Ожидает оплаты
    POLICY_ISSUED // Полис: выпущен
}
