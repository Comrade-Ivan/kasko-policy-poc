package ru.motorinsurance.kasko.service.status.rule;

import org.springframework.stereotype.Component;
import ru.motorinsurance.kasko.enums.PolicyStatus;

import java.util.Map;

@Component
public class ConfigRulesProvider implements TransitionRulesProvider {
    @Override
    public Map<PolicyStatus, TransitionRule> getRules() {
        return PolicyTransitionRulesConfig.getTransitionRuleMap();
    }
}
