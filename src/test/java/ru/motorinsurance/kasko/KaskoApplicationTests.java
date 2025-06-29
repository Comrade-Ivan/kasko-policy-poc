package ru.motorinsurance.kasko;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.motorinsurance.kasko.dto.PolicyChangeStatusRequest;
import ru.motorinsurance.kasko.dto.PolicyCreateRequest;
import ru.motorinsurance.kasko.dto.PolicyResponse;
import ru.motorinsurance.kasko.dto.PolicyUpdateDto;
import ru.motorinsurance.kasko.enums.PolicyStatus;
import ru.motorinsurance.kasko.model.Policy;
import ru.motorinsurance.kasko.model.PolicyHolder;
import ru.motorinsurance.kasko.model.Vehicle;
import ru.motorinsurance.kasko.repository.PolicyRepository;
import ru.motorinsurance.kasko.service.PolicyService;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.motorinsurance.kasko.TestDataFactory.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Slf4j
class KaskoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private PolicyRepository policyRepository;

    private final String API_V1 = "/api/v1";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Environment env;

    @Test
    void printDatabaseInfo() throws Exception {
        System.out.println("ACTIVE PROFILES: " + Arrays.toString(env.getActiveProfiles()));
        System.out.println("Using database URL: " + dataSource.getConnection().getMetaData().getURL());
        System.out.println("Using database driver: " + dataSource.getConnection().getMetaData().getDriverName());
    }

    @Test
    @Transactional
    @DirtiesContext
    void createPolicy_ShouldCreatePreCalculationOnValidRequest() throws Exception {
        String requestBody = """
                {
                  "vehicle": {
                    "vin": "XTA21099765432102",
                    "mileage": 45000,
                    "actualValue": 1250000,
                    "purchaseDate": "15.05.2022",
                    "usagePurpose": "Личное использование",
                    "registrationNumber": "А123БВ777"
                  },
                  "policyHolder": {
                    "type": "Физ.Лицо",
                    "name": "Иванов Иван Иванович",
                    "contact": {
                      "phone": "+79161234567",
                      "email": "ivanov@example.com"
                    }
                  },
                  "drivers": "{\\"type\\":\\"Список\\",\\"drivers\\":[{\\"fullName\\":\\"Иванов Иван Иванович\\",\\"experience\\":10,\\"age\\":35}]}"
                }
                """;
        mockMvc.perform(
                        post(API_V1 + "/policies/create")
                                .header("Authorization", basicAuthHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyId").exists())
                .andExpect(jsonPath("$.status").value(PolicyStatus.PRE_CALCULATION.getRussianName()));
    }

    @Test
    void getPolicy_ShouldGetPolicyOnGetRequest() throws Exception {
        PolicyCreateRequest request = createTestRequest();
        PolicyResponse response = policyService.createPolicy(request);

        mockMvc.perform(
                        get(API_V1 + "/policies/" + response.getPolicyId())
                                .header("Authorization", basicAuthHeader())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyId").value(response.getPolicyId()));
    }

    @Test
    void changePolicyStatus_ShouldChangeStatusOnValidTransition() throws Exception {
        PolicyCreateRequest request = createTestRequest();
        PolicyResponse response = policyService.createPolicy(request);

        PolicyChangeStatusRequest changeStatusRequest = PolicyChangeStatusRequest.builder()
                .policyId(response.getPolicyId())
                .targetStatus(PolicyStatus.QUOTE_NEW.getRussianName())
                .build();

        mockMvc.perform(
                patch(API_V1 + "/policies/" + response.getPolicyId() + "/status")
                        .header("Authorization", basicAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(changeStatusRequest))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyId").value(response.getPolicyId()))
                .andExpect(jsonPath("$.status").value(PolicyStatus.QUOTE_NEW.getRussianName()));

        Policy dbPolicy = policyRepository.findByPolicyId(response.getPolicyId()).orElseThrow();
        assertEquals(PolicyStatus.QUOTE_NEW, dbPolicy.getStatus());
    }

    @Test
    void updatePolicy_ShouldUpdatePolicyOnValidRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        PolicyCreateRequest request = createTestRequest();
        PolicyResponse createResponse = policyService.createPolicy(request);

        LocalDate newStartDate = LocalDate.now().plusDays(1L);
        LocalDate newEndDate = LocalDate.now().plusYears(1L);
        BigDecimal newPremiumAmount = BigDecimal.valueOf(1000000);
        PolicyUpdateDto policyUpdateDto = PolicyUpdateDto.builder()
                .startDate(newStartDate)
                .endDate(newEndDate)
                .premiumAmount(newPremiumAmount)
                .build();
        log.info("------------------PERFORM REQUEST-----------------------");
        mockMvc.perform(
                patch(API_V1 + "/policies/" + createResponse.getPolicyId())
                        .header("Authorization", basicAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyUpdateDto))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyId").value(createResponse.getPolicyId()))
                .andExpect(jsonPath("$.startDate").value(newStartDate.format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.endDate").value(newEndDate.format(DATE_FORMATTER)))
                .andExpect(jsonPath("$.premiumAmount").value(newPremiumAmount));
    }

    private String basicAuthHeader() {
        String auth = "admin:admin123";
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }

}
