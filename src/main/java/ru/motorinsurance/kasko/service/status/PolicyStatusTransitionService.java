package ru.motorinsurance.kasko.service.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import ru.motorinsurance.kasko.enums.PolicyStatus;
import ru.motorinsurance.kasko.model.Policy;
import ru.motorinsurance.kasko.service.status.rule.PolicyTransitionRulesConfig;
import ru.motorinsurance.kasko.service.status.rule.TransitionRule;

import java.util.Map;

@Service
public class PolicyStatusTransitionService {

    private final Map<PolicyStatus, TransitionRule> rules;

    PolicyStatusTransitionService() {
        this.rules = PolicyTransitionRulesConfig.getTransitionRuleMap();
    }

    public void validateTransition(Policy policy, PolicyStatus targetStatus) {
        //TODO: ADD USER ROLE CHECK
        try {
            System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(rules));
        } catch (Exception e) {}
        TransitionRule rule = rules.get(policy.getStatus());
        if (rule == null) {
            throw new IllegalStateException("Нет правил для статуса: " + policy.getStatus());
        }
        rule.validateTransition(targetStatus, policy);
    }
}
