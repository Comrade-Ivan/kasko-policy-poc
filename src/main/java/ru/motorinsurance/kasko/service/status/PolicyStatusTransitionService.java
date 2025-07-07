package ru.motorinsurance.kasko.service.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.motorinsurance.common.core.enums.PolicyStatus;
import ru.motorinsurance.kasko.model.Policy;
import ru.motorinsurance.kasko.service.status.rule.PolicyTransitionRulesConfig;
import ru.motorinsurance.kasko.service.status.rule.TransitionRule;
import ru.motorinsurance.kasko.service.status.rule.TransitionRulesProvider;

import java.util.Map;

@Service
public class PolicyStatusTransitionService {

    private final Map<PolicyStatus, TransitionRule> rules;

    @Autowired
    public PolicyStatusTransitionService(TransitionRulesProvider rulesProvider) {
        this.rules = rulesProvider.getRules();
    }

    public void validateTransition(Policy policy, PolicyStatus targetStatus) {
        //TODO: ADD USER ROLE CHECK
        TransitionRule rule = rules.get(policy.getStatus());
        if (rule == null) {
            throw new IllegalStateException("Нет правил для статуса: " + policy.getStatus());
        }
        rule.validateTransition(targetStatus, policy);
    }
}
