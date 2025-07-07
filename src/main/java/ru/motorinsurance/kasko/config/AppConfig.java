package ru.motorinsurance.kasko.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.motorinsurance.debeziumoutbox.OutboxEventRepository;
import ru.motorinsurance.debeziumoutbox.OutboxSentUpdated;
import ru.motorinsurance.kasko.service.status.PolicyStatusTransitionService;
import ru.motorinsurance.kasko.service.status.rule.ConfigRulesProvider;
import ru.motorinsurance.kasko.service.status.rule.TransitionRulesProvider;

@Configuration
public class AppConfig {

    @Bean
    public TransitionRulesProvider rulesProvider() {
        return new ConfigRulesProvider();  // Реальная реализация
    }

    @Bean
    public OutboxSentUpdated sentUpdated(OutboxEventRepository eventRepository) {
        return new OutboxSentUpdated(eventRepository);
    }

}
