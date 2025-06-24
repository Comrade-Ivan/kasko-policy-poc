package ru.motorinsurance.kasko.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.motorinsurance.kasko.service.status.PolicyStatusTransitionService;
import ru.motorinsurance.kasko.service.status.rule.ConfigRulesProvider;
import ru.motorinsurance.kasko.service.status.rule.TransitionRulesProvider;

@Configuration
public class AppConfig {

    @Bean
    public TransitionRulesProvider rulesProvider() {
        return new ConfigRulesProvider();  // Реальная реализация
    }

//    // Бин сервиса создаётся автоматически благодаря @Service,
//    // но можно объявить явно для кастомной настройки:
//    @Bean
//    public PolicyStatusTransitionService policyStatusTransitionService(
//            TransitionRulesProvider provider) {
//        return new PolicyStatusTransitionService(provider);
//    }
}
