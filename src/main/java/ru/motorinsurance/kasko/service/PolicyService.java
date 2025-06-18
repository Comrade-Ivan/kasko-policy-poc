package ru.motorinsurance.kasko.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.motorinsurance.kasko.dto.DriversDto;
import ru.motorinsurance.kasko.dto.PolicyCreateRequest;
import ru.motorinsurance.kasko.dto.PolicyResponse;
import ru.motorinsurance.kasko.enums.PolicyStatus;
import ru.motorinsurance.kasko.mappers.PolicyMapper;
import ru.motorinsurance.kasko.model.Policy;
import ru.motorinsurance.kasko.model.PolicyHolder;
import ru.motorinsurance.kasko.model.Vehicle;
import ru.motorinsurance.kasko.repository.PolicyHolderRepository;
import ru.motorinsurance.kasko.repository.PolicyRepository;
import ru.motorinsurance.kasko.repository.VehicleRepository;
import ru.motorinsurance.kasko.utils.JsonUtils;

import java.util.UUID;

import java.time.LocalDateTime;

@Service
public class PolicyService {
    private PolicyRepository policyRepository;
    private VehicleRepository vehicleRepository;
    private PolicyHolderRepository policyHolderRepository;
    private PolicyMapper policyMapper;

    @Transactional
    public PolicyResponse createPolicy(PolicyCreateRequest request) {
        String policyId = generatePolicyId();
        Vehicle vehicle = vehicleRepository.findByVin(request.getVehicle().getVin()).orElseGet(() -> {
           Vehicle newVehicle = policyMapper.toVehicleEntity(request.getVehicle());
           return vehicleRepository.save(newVehicle);
        });
        PolicyHolder policyHolder = policyHolderRepository.findByNameAndPhone(request.getPolicyHolder().getName(), request.getPolicyHolder().getContact().getPhone()).orElseGet(() -> {
            PolicyHolder newPolicyHolder = policyMapper.toPolicyHolderEntity(request.getPolicyHolder());
            return policyHolderRepository.save(newPolicyHolder);
        });

        Policy policy = Policy.builder()
                .policyId(policyId)
                .createdAt(LocalDateTime.now())
                .status(PolicyStatus.PRE_CALCULATION)
                .policyHolder(policyHolder)
                .vehicle(vehicle)
                .isCancelled(false)
                .drivers(request.getDrivers())
                .build();

        vehicle.setPolicy(policy);
        policyHolder.getPolicies().add(policy);

        policyHolderRepository.save(policyHolder);
        policyRepository.save(policy);

        return policyMapper.toPolicyResponse(policy);
    }

    /**
     * Generates a new policy ID in format KASKO-YYYY-XXXXXX
     * @return Generated policy ID
     */
    private String generatePolicyId() {
        int year = LocalDateTime.now().getYear();
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return String.format("KASKO-%d-%s", year, randomPart);
    }
}
