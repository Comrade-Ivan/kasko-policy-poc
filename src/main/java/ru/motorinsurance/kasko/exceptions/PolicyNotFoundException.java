package ru.motorinsurance.kasko.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое когда запрашиваемый полис не найден в системе.
 * Автоматически возвращает HTTP 404 статус при использовании в Spring MVC.
 */
@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Policy not found")
public class PolicyNotFoundException extends RuntimeException {

    private final String policyId;

    public PolicyNotFoundException(String policyId) {
        super(String.format("Policy with ID '%s' not found", policyId));
        this.policyId = policyId;
    }

    public PolicyNotFoundException(String policyId, Throwable cause) {
        super(String.format("Policy with ID '%s' not found", policyId), cause);
        this.policyId = policyId;
    }

    // Дополнительные конструкторы при необходимости
    public PolicyNotFoundException(String message, String policyId) {
        super(message);
        this.policyId = policyId;
    }

    /**
     * Фабричный метод для удобного создания исключения
     */
    public static PolicyNotFoundException byId(String policyId) {
        return new PolicyNotFoundException(policyId);
    }
}