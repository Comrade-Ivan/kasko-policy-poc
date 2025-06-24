package ru.motorinsurance.kasko.service.status.rule;

import ru.motorinsurance.kasko.enums.PolicyStatus;

import java.util.Map;

public interface TransitionRulesProvider {
    Map<PolicyStatus, TransitionRule> getRules();
}

