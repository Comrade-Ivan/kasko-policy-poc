package ru.motorinsurance.kasko.service.status.rule;

import ru.motorinsurance.kasko.enums.PolicyStatus;
import ru.motorinsurance.kasko.model.Policy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public record TransitionRule (Set<PolicyStatus> allowedTargetStatuses,Map<PolicyStatus, Map<Predicate<Policy>, String>> guardsMap) {
    public void validateTransition(PolicyStatus targetStatus, Policy policy) {
        if (!allowedTargetStatuses.contains(targetStatus)) {
            throw new IllegalStateException("Переход в статус запрещен. Разрешены только: " + allowedTargetStatuses);
        }

        Set<String> guardErrors = new HashSet<>();
        guardsMap.get(targetStatus).forEach((guard, errorMessage) -> {
            if (!guard.test(policy)) {
                guardErrors.add(errorMessage);
            }
        });

        if (!guardErrors.isEmpty()) {
            throw new IllegalStateException(guardErrors.stream().reduce("", (x,y) -> x + ", " + y));
        }
    }
}
