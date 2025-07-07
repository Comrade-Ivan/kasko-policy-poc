package ru.motorinsurance.kasko.service.status.rule;

import ru.motorinsurance.common.core.enums.PolicyStatus;

import java.math.BigDecimal;
import java.util.Map;

import static java.util.Map.entry;

public class PolicyTransitionRulesConfig {
    private static final Map<PolicyStatus, TransitionRule> TRANSITION_RULE_MAP = Map.ofEntries(
            entry(PolicyStatus.PRE_CALCULATION,
                    new TransitionRuleBuilder()
                            .to(PolicyStatus.QUOTE_NEW)
                            .withGuard(policy -> policy.getVehicle().getMileage() != null, "Не заполнен пробег ТС")
                            .withGuard(policy -> policy.getVehicle().getMileage() >= 0, "Пробег не может быть меньше 0")
                            .withGuard(policy -> policy.getVehicle().getActualValue() != null, "Не заполнена актуальная стоимость ТС")
                            .withGuard(policy -> policy.getVehicle().getActualValue().compareTo(BigDecimal.ZERO) > 0, "Актуальная стоимость ТС не может быть меньше или равна 0")
                            .withGuard(policy -> policy.getPolicyHolder() != null, "Страхователь не может быть пустым")
                            .to(PolicyStatus.CANCELLED)
                            .withGuard(policy -> policy.getCancellationReason() != null, "Причина отмены не может быть пустой")
                            .build()
            ),
            entry(PolicyStatus.QUOTE_NEW,
                    new TransitionRuleBuilder()
                            .to(PolicyStatus.QUOTE_SENT_FOR_APPROVAL)
                            .withGuard(policy -> policy.getVehicle().getMileage() >= 0, "Пробег не может быть меньше 0")
                            .withGuard(policy -> policy.getVehicle().getActualValue().compareTo(BigDecimal.ZERO) > 0, "Актуальная стоимость ТС не может быть меньше или равна 0")
                            .to(PolicyStatus.CANCELLED)
                            .withGuard(policy -> policy.getCancellationReason() != null, "Причина отмены не может быть пустой")
                            .build()
            ),
            entry(PolicyStatus.QUOTE_SENT_FOR_APPROVAL,
                    new TransitionRuleBuilder()
                            .to(PolicyStatus.QUOTE_ISSUED)
                            .to(PolicyStatus.QUOTE_REWORK)
                            .to(PolicyStatus.CANCELLED)
                            .withGuard(policy -> policy.getCancellationReason() != null, "Причина отмены не может быть пустой")
                            .build()
            ),
            entry(PolicyStatus.QUOTE_REWORK,
                    new TransitionRuleBuilder()
                            .to(PolicyStatus.QUOTE_SENT_FOR_APPROVAL)
                            .withGuard(policy -> policy.getVehicle().getMileage() >= 0, "Пробег не может быть меньше 0")
                            .withGuard(policy -> policy.getVehicle().getActualValue().compareTo(BigDecimal.ZERO) > 0, "Актуальная стоимость ТС не может быть меньше или равна 0")
                            .to(PolicyStatus.CANCELLED)
                            .withGuard(policy -> policy.getCancellationReason() != null, "Причина отмены не может быть пустой")
                            .build()
            ),
            entry(PolicyStatus.QUOTE_ISSUED,
                    new TransitionRuleBuilder()
                            .to(PolicyStatus.POLICY_NEW)
                            .to(PolicyStatus.CANCELLED)
                            .withGuard(policy -> policy.getCancellationReason() != null, "Причина отмены не может быть пустой")
                            .build()
            ),
            entry(PolicyStatus.POLICY_NEW,
                    new TransitionRuleBuilder()
                            .to(PolicyStatus.POLICY_AWAITING_PAYMENT)
                            .withGuard(policy -> policy.getPaymentMethod() != null, "Не заполнен способ оплаты")
                            .to(PolicyStatus.CANCELLED)
                            .withGuard(policy -> policy.getCancellationReason() != null, "Причина отмены не может быть пустой")
                            .build()
            ),
            entry(PolicyStatus.POLICY_AWAITING_PAYMENT,
                    new TransitionRuleBuilder()
                            .to(PolicyStatus.POLICY_ISSUED)
                            .to(PolicyStatus.CANCELLED)
                            .withGuard(policy -> policy.getCancellationReason() != null, "Причина отмены не может быть пустой")
                            .build()
            )
    );

    public static Map<PolicyStatus, TransitionRule> getTransitionRuleMap() {
        return TRANSITION_RULE_MAP;
    }

}
