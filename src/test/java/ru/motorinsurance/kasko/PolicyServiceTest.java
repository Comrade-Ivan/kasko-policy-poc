package ru.motorinsurance.kasko;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.motorinsurance.kasko.dto.*;
import ru.motorinsurance.kasko.enums.PolicyStatus;
import ru.motorinsurance.kasko.enums.VehicleUsagePurpose;
import ru.motorinsurance.kasko.mappers.PolicyMapper;
import ru.motorinsurance.kasko.model.Policy;
import ru.motorinsurance.kasko.model.PolicyHolder;
import ru.motorinsurance.kasko.model.StatusTransition;
import ru.motorinsurance.kasko.model.Vehicle;
import ru.motorinsurance.kasko.repository.PolicyHolderRepository;
import ru.motorinsurance.kasko.repository.PolicyRepository;
import ru.motorinsurance.kasko.repository.StatusTransitionRepository;
import ru.motorinsurance.kasko.repository.VehicleRepository;
import ru.motorinsurance.kasko.service.PolicyService;
import ru.motorinsurance.kasko.service.status.PolicyStatusTransitionService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.motorinsurance.kasko.TestDataFactory.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private PolicyHolderRepository policyHolderRepository;

    @Mock
    private PolicyMapper policyMapper;

    @InjectMocks
    private PolicyService policyService;

    @Mock
    private PolicyStatusTransitionService policyStatusTransitionService;

    @Mock
    private StatusTransitionRepository statusTransitionRepository;

    @Test
    void createPolicy_WithNewVehicleAndNewHolder_ShouldCreateAllEntities() {
        // Arrange
        PolicyCreateRequest request = createTestRequest();
        VehicleDto vehicleDto = request.getVehicle();
        PolicyHolderDto holderDto = request.getPolicyHolder();

        when(vehicleRepository.findByVin(TEST_VIN)).thenReturn(Optional.empty());
        when(policyHolderRepository.findByNameAndPhone(holderDto.getName(), holderDto.getContact().getPhone()))
                .thenReturn(Optional.empty());

        Vehicle savedVehicle = createTestVehicle();
        PolicyHolder savedHolder = createTestPolicyHolder();
        Policy expectedPolicy = createTestPolicy(savedVehicle, savedHolder);

        when(policyMapper.toVehicleEntity(vehicleDto)).thenReturn(savedVehicle);
        when(policyMapper.toPolicyHolderEntity(holderDto)).thenReturn(savedHolder);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);
        when(policyHolderRepository.save(any(PolicyHolder.class))).thenReturn(savedHolder);
        when(policyRepository.save(any(Policy.class))).thenReturn(expectedPolicy);
        when(policyMapper.toPolicyResponse(any(Policy.class))).thenReturn(createTestResponse());

        // Act
        PolicyResponse result = policyService.createPolicy(request);

        // Assert
        assertNotNull(result);
        verify(vehicleRepository).findByVin(TEST_VIN);
        verify(policyHolderRepository).findByNameAndPhone(holderDto.getName(), holderDto.getContact().getPhone());
        verify(vehicleRepository).save(savedVehicle);
        verify(policyHolderRepository, times(2)).save(savedHolder);
        verify(policyRepository).save(any(Policy.class));
    }

    @Test
    void createPolicy_WithExistingVehicle_ShouldReuseVehicle() {
        // Arrange
        PolicyCreateRequest request = createTestRequest();
        Vehicle existingVehicle = createTestVehicle();
        PolicyHolder newHolder = createTestPolicyHolder();

        when(policyMapper.toPolicyHolderEntity(request.getPolicyHolder())).thenReturn(newHolder);
        when(vehicleRepository.findByVin(TEST_VIN)).thenReturn(Optional.of(existingVehicle));
        when(policyHolderRepository.findByNameAndPhone(anyString(), anyString())).thenReturn(Optional.empty());
        when(policyHolderRepository.save(any(PolicyHolder.class))).thenReturn(newHolder);
        when(policyRepository.save(any(Policy.class))).thenReturn(createTestPolicy(existingVehicle, newHolder));
        when(policyMapper.toPolicyResponse(any(Policy.class))).thenReturn(createTestResponse());

        // Act
        PolicyResponse result = policyService.createPolicy(request);

        // Assert
        assertNotNull(result);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void createPolicy_WithExistingHolder_ShouldReuseHolder() {
        // Arrange
        PolicyCreateRequest request = createTestRequest();
        Vehicle newVehicle = createTestVehicle();
        PolicyHolder existingHolder = createTestPolicyHolder();

        when(policyMapper.toVehicleEntity(request.getVehicle())).thenReturn(newVehicle);
        when(vehicleRepository.findByVin(TEST_VIN)).thenReturn(Optional.empty());
        when(policyHolderRepository.findByNameAndPhone(anyString(), anyString()))
                .thenReturn(Optional.of(existingHolder));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(newVehicle);
        when(policyRepository.save(any(Policy.class))).thenReturn(createTestPolicy(newVehicle, existingHolder));
        when(policyMapper.toPolicyResponse(any(Policy.class))).thenReturn(createTestResponse());

        // Act
        PolicyResponse result = policyService.createPolicy(request);

        // Assert
        assertNotNull(result);
        verify(policyHolderRepository, times(1)).save(any(PolicyHolder.class));
    }

    @Test
    void createPolicy_ShouldSetCorrectPolicyStatus() {
        // Arrange
        PolicyCreateRequest request = createTestRequest();
        Vehicle savedVehicle = createTestVehicle();
        PolicyHolder savedPolicyHolder = createTestPolicyHolder();
        Policy savedPolicy = createTestPolicy(savedVehicle, savedPolicyHolder);

        when(vehicleRepository.findByVin(anyString())).thenReturn(Optional.of(savedVehicle));
        when(policyHolderRepository.findByNameAndPhone(anyString(), anyString())).thenReturn(Optional.of(savedPolicyHolder));
        when(policyRepository.save(any(Policy.class))).thenReturn(savedPolicy);

        // Act
        policyService.createPolicy(request);

        // Assert
        verify(policyRepository).save(argThat(policy ->
                policy.getStatus() == PolicyStatus.PRE_CALCULATION
        ));
    }

    @Test
    void createPolicy_ShouldGenerateValidPolicyId() {
        // Arrange
        PolicyCreateRequest request = createTestRequest();
        Vehicle savedVehicle = createTestVehicle();
        PolicyHolder savedPolicyHolder = createTestPolicyHolder();
        ArgumentCaptor<Policy> policyCaptor = ArgumentCaptor.forClass(Policy.class);

        when(vehicleRepository.findByVin(anyString())).thenReturn(Optional.of(savedVehicle));
        when(policyHolderRepository.findByNameAndPhone(anyString(), anyString())).thenReturn(Optional.of(savedPolicyHolder));
        when(policyRepository.save(policyCaptor.capture())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // Act
       policyService.createPolicy(request);

       Policy savedPolicy = policyCaptor.getValue();
        // Assert
        assertNotNull(savedPolicy.getPolicyId());
        assertTrue(savedPolicy.getPolicyId().matches("KASKO-\\d{4}-[A-Z0-9]{6}"));
    }

    @Test
    void createPolicy_ShouldMaintainBidirectionalRelationships() {
        // Arrange
        PolicyCreateRequest request = createTestRequest();
        Vehicle vehicle = createTestVehicle();
        PolicyHolder holder = createTestPolicyHolder();
        Policy policy = createTestPolicy(vehicle, holder);

        when(vehicleRepository.findByVin(anyString())).thenReturn(Optional.of(vehicle));
        when(policyHolderRepository.findByNameAndPhone(anyString(), anyString())).thenReturn(Optional.of(holder));
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        // Act
        policyService.createPolicy(request);

        // Assert
        verify(policyRepository).save(argThat(p ->
                p.getVehicle() != null &&
                        p.getPolicyHolder() != null &&
                        p.getVehicle().getPolicy() == p &&
                        p.getPolicyHolder().getPolicies().contains(p)
        ));
    }

    @Test
    void policyChangeStatus_ShouldSuccessOnValidTransition() {
        Policy savedPolicy = createTestPolicy();
        PolicyStatus initialStatus = PolicyStatus.PRE_CALCULATION;
        PolicyStatus targetStatus = PolicyStatus.QUOTE_NEW;

        savedPolicy.setStatus(initialStatus);

        PolicyChangeStatusRequest request = PolicyChangeStatusRequest.builder()
                .policyId("1")
                .targetStatus(targetStatus)
                .build();
        ArgumentCaptor<StatusTransition> transitionArgumentCaptor = ArgumentCaptor.forClass(StatusTransition.class);

        when(policyRepository.findByPolicyId(anyString())).thenReturn(Optional.of(savedPolicy));
        when(statusTransitionRepository.save(transitionArgumentCaptor.capture())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        policyService.changePolicyStatus(request);

        StatusTransition savedTransition = transitionArgumentCaptor.getValue();

        verify(policyStatusTransitionService).validateTransition(savedPolicy, targetStatus);
        verify(policyRepository).save(argThat(p -> p.getStatus().equals(targetStatus)));
        assertEquals(savedTransition.getFromStatus(), initialStatus.getRussianName());
        assertEquals(savedTransition.getToStatus(), targetStatus.getRussianName());
        assertNotNull(savedTransition.getPolicy());

    }

    @Test
    void policyChangeStatus_ShouldThrowExceptionOnFailedValidation() {
        Policy savedPolicy = createTestPolicy();
        PolicyStatus targetStatus = PolicyStatus.QUOTE_NEW;

        PolicyChangeStatusRequest request = PolicyChangeStatusRequest.builder()
                .policyId("1")
                .targetStatus(targetStatus)
                .build();

        when(policyRepository.findByPolicyId(anyString())).thenReturn(Optional.of(savedPolicy));
        doThrow(new IllegalStateException("Transition invalidation")).when(policyStatusTransitionService).validateTransition(savedPolicy, targetStatus);

        assertThrows(IllegalStateException.class, () -> policyService.changePolicyStatus(request));
        verify(policyRepository, never()).save(savedPolicy);
    }

    private PolicyCreateRequest createTestRequest() {
        return PolicyCreateRequest.builder()
                .vehicle(VehicleDto.builder()
                        .vin(TEST_VIN)
                        .mileage(45000)
                        .actualValue(BigDecimal.valueOf(1250000))
                        .purchaseDate("15.05.2022")
                        .usagePurpose(VehicleUsagePurpose.PERSONAL.getRussianName())
                        .registrationNumber("А123БВ777")
                        .build())
                .policyHolder(PolicyHolderDto.builder()
                        .type("Физ.Лицо")
                        .name("Иванов Иван Иванович")
                        .contact(ContactDto.builder()
                                .phone(TEST_PHONE)
                                .email(TEST_EMAIL)
                                .build())
                        .build())
                .drivers("{\"type\":\"Список\",\"drivers\":[{\"fullName\":\"Иванов Иван Иванович\",\"experience\":10,\"age\":35}]}")
                .build();
    }

}
