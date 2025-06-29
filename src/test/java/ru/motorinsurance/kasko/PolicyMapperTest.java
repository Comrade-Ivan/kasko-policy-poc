package ru.motorinsurance.kasko;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.motorinsurance.kasko.dto.PolicyResponse;
import ru.motorinsurance.kasko.dto.PolicyUpdateDto;
import ru.motorinsurance.kasko.mappers.PolicyMapper;
import ru.motorinsurance.kasko.model.Policy;
import ru.motorinsurance.kasko.model.PolicyHolder;
import ru.motorinsurance.kasko.model.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.motorinsurance.kasko.TestDataFactory.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Slf4j
public class PolicyMapperTest {

    private final PolicyMapper policyMapper = Mappers.getMapper(PolicyMapper.class);

    @Test
    void shouldMapPolicyToPolicyResponseDTO() {
        Vehicle vehicle = createTestVehicle();
        PolicyHolder policyHolder = createTestPolicyHolder();
        Policy policy = createTestPolicy(vehicle, policyHolder);

        PolicyResponse response = policyMapper.toPolicyResponse(policy);

        assertEquals(response.getPolicyHolder().getContact().getPhone(), policyHolder.getPhone());
        assertEquals(response.getPolicyHolder().getContact().getEmail(), policyHolder.getEmail());

    }

    @Test
    void policyUpdate_ShouldUpdateOnPolicyUpdateDto() {
        Policy savedPolicy = createTestPolicy();
        LocalDate newStartDate = LocalDate.now().plusDays(1L);
        LocalDate newEndDate = LocalDate.now().plusYears(1L);
        BigDecimal newPremiumAmount = BigDecimal.valueOf(1000000);
        PolicyUpdateDto policyUpdateDto = PolicyUpdateDto.builder()
                .startDate(newStartDate)
                .endDate(newEndDate)
                .premiumAmount(newPremiumAmount)
                .build();

        policyMapper.updatePolicyFromDto(policyUpdateDto, savedPolicy);

        assertEquals(savedPolicy.getStartDate(), newStartDate);
        assertEquals(savedPolicy.getEndDate(), newEndDate);
        assertEquals(savedPolicy.getPremiumAmount(), newPremiumAmount);
    }
}
