package ru.motorinsurance.kasko;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.motorinsurance.kasko.enums.PolicyStatus;
import ru.motorinsurance.kasko.model.Policy;
import ru.motorinsurance.kasko.service.status.PolicyStatusTransitionService;
import org.junit.jupiter.api.Assertions.*;
import static ru.motorinsurance.kasko.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PolicyStatusTransitionServiceTest {

    @InjectMocks
    PolicyStatusTransitionService transitionService;

    @Test
    void validateTransition() {
        Policy policy = createTestPolicy();
        transitionService.validateTransition(policy, PolicyStatus.QUOTE_NEW);
    }
}
