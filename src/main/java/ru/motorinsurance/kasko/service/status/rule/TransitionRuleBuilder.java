package ru.motorinsurance.kasko.service.status.rule;

import ru.motorinsurance.common.core.enums.PolicyStatus;
import ru.motorinsurance.kasko.model.Policy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class TransitionRuleBuilder {
    private final Set<PolicyStatus> targetStatuses = new HashSet<>();
    private final Map<PolicyStatus, Map<Predicate<Policy>, String>> guardsMap = new HashMap<>();
    private PolicyStatus ruleForStatus;

    public TransitionRuleBuilder to(PolicyStatus status) {
        this.ruleForStatus = status;
        targetStatuses.add(status);
        guardsMap.put(status, new HashMap<>());
        return this;
    }

    public TransitionRuleBuilder withGuard(Predicate<Policy> guard, String errorMessage) {
        guardsMap.get(ruleForStatus).put(guard, errorMessage);
        return this;
    }

    public TransitionRule build() {
        return new TransitionRule(targetStatuses, guardsMap);
    }
}
