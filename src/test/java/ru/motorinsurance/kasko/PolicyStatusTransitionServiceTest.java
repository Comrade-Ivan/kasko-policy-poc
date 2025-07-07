package ru.motorinsurance.kasko;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.motorinsurance.common.core.enums.PolicyStatus;
import ru.motorinsurance.kasko.model.Policy;
import ru.motorinsurance.kasko.model.Vehicle;
import ru.motorinsurance.kasko.service.status.PolicyStatusTransitionService;
import org.junit.jupiter.api.Assertions.*;
import ru.motorinsurance.kasko.service.status.rule.TransitionRule;
import ru.motorinsurance.kasko.service.status.rule.TransitionRuleBuilder;
import ru.motorinsurance.kasko.service.status.rule.TransitionRulesProvider;

import java.math.BigDecimal;
import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.motorinsurance.kasko.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PolicyStatusTransitionServiceTest {
    @Mock
    private TransitionRulesProvider rulesProvider;

    @InjectMocks
    private PolicyStatusTransitionService service;
    private Map<PolicyStatus, TransitionRule> mockRules;

    @BeforeEach
    void setUp() {
        // Создаем тестовую версию правил вместо использования реальной конфигурации
        mockRules = Map.ofEntries(
                entry(PolicyStatus.PRE_CALCULATION,
                        new TransitionRuleBuilder()
                                .to(PolicyStatus.QUOTE_NEW)
                                .withGuard(p -> p.getVehicle().getMileage() != null, "Mileage required")
                                .build()),
                entry(PolicyStatus.QUOTE_NEW,
                        new TransitionRuleBuilder()
                                .to(PolicyStatus.QUOTE_SENT_FOR_APPROVAL)
                                .build())
        );

        when(rulesProvider.getRules()).thenReturn(mockRules);

        service = new PolicyStatusTransitionService(rulesProvider);
    }

    @Test
    void validateTransition_shouldAllowValidTransition() {
        // Arrange
        Policy policy = createTestPolicy();
        policy.setStatus(PolicyStatus.PRE_CALCULATION);

        // Act & Assert
        assertDoesNotThrow(() ->
                service.validateTransition(policy, PolicyStatus.QUOTE_NEW));
    }

    @Test
    void validateTransition_shouldThrowWhenTransitionNotAllowed() {
        // Arrange
        Policy policy = createTestPolicy();
        policy.setStatus(PolicyStatus.PRE_CALCULATION);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () ->
                service.validateTransition(policy, PolicyStatus.POLICY_NEW));

        assertTrue(exception.getMessage().contains("Переход в статус запрещен"));
    }

    @Test
    void validateTransition_shouldThrowWhenGuardConditionFails() {
        // Arrange
        Policy policy = createTestPolicy();
        policy.setStatus(PolicyStatus.PRE_CALCULATION);
        policy.getVehicle().setMileage(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () ->
                service.validateTransition(policy, PolicyStatus.QUOTE_NEW));

        assertTrue(exception.getMessage().contains("Mileage required"));
    }

    @Test
    void validateTransition_shouldAllowTransitionWithoutGuards() {
        // Arrange
        Policy policy = createTestPolicy();
        policy.setStatus(PolicyStatus.QUOTE_NEW);

        // Act & Assert
        assertDoesNotThrow(() ->
                service.validateTransition(policy, PolicyStatus.QUOTE_SENT_FOR_APPROVAL));
    }

    @Test
    void validateTransition_shouldThrowWhenNoRulesForCurrentStatus() {
        // Arrange
        Policy policy = createTestPolicy();
        policy.setStatus(PolicyStatus.POLICY_ISSUED); // Нет правил для этого статуса

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () ->
                service.validateTransition(policy, PolicyStatus.CANCELLED));

        assertTrue(exception.getMessage().contains("Нет правил для статуса"));
    }

    @Test
    void validateTransition_shouldCombineMultipleGuardErrors() {
        // Arrange
        TransitionRule testRule = new TransitionRuleBuilder()
                .to(PolicyStatus.QUOTE_NEW)
                .withGuard(p -> false, "Error 1")
                .withGuard(p -> false, "Error 2")
                .build();

        mockRules = Map.of(PolicyStatus.PRE_CALCULATION, testRule);
        when(rulesProvider.getRules()).thenReturn(mockRules);
        service = new PolicyStatusTransitionService(rulesProvider);

        Policy policy = createTestPolicy();
        policy.setStatus(PolicyStatus.PRE_CALCULATION);

        // Act
        Exception exception = assertThrows(IllegalStateException.class, () ->
                service.validateTransition(policy, PolicyStatus.QUOTE_NEW));

        // Assert
        assertTrue(exception.getMessage().contains("Error 1"));
        assertTrue(exception.getMessage().contains("Error 2"));
    }
}
