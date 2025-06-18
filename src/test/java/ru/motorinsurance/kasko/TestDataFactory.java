package ru.motorinsurance.kasko;

import org.springframework.stereotype.Component;
import ru.motorinsurance.kasko.dto.ContactDto;
import ru.motorinsurance.kasko.dto.PolicyHolderDto;
import ru.motorinsurance.kasko.dto.PolicyResponse;
import ru.motorinsurance.kasko.dto.VehicleDto;
import ru.motorinsurance.kasko.enums.HolderType;
import ru.motorinsurance.kasko.enums.PolicyStatus;
import ru.motorinsurance.kasko.model.Policy;
import ru.motorinsurance.kasko.model.PolicyHolder;
import ru.motorinsurance.kasko.model.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class TestDataFactory {

    public static final String TEST_VIN = "XTA21099765432101";
    public static final String TEST_PHONE = "+79161234567";
    public static final String TEST_EMAIL = "test@example.com";

    public static Vehicle createTestVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVin(TEST_VIN);
        vehicle.setMileage(45000);
        vehicle.setActualValue(BigDecimal.valueOf(1250000));
        return vehicle;
    }

    public static PolicyHolder createTestPolicyHolder() {
        PolicyHolder holder = new PolicyHolder();
        holder.setName("Иванов Иван Иванович");
        holder.setType(HolderType.INDIVIDUAL);
        holder.setPhone(TEST_PHONE);
        holder.setEmail(TEST_EMAIL);
        return holder;
    }

    public static Policy createTestPolicy(Vehicle vehicle, PolicyHolder holder) {
        return Policy.builder()
                .policyId("KASKO-2024-123456")
                .createdAt(LocalDateTime.now())
                .status(PolicyStatus.PRE_CALCULATION)
                .isCancelled(false)
                .vehicle(vehicle)
                .policyHolder(holder)
                .build();
    }

    public static PolicyResponse createTestResponse() {
        return PolicyResponse.builder()
                .policyId("KASKO-2024-123456")
                .status("Предрасчет")
                .vehicle(createTestVehicleDto())
                .policyHolder(createTestPolicyHolderDto())
                .build();
    }

    public static VehicleDto createTestVehicleDto() {
        return VehicleDto.builder()
                .vin(TEST_VIN)
                .mileage(45000)
                .actualValue(BigDecimal.valueOf(1250000))
                .build();
    }

    public static PolicyHolderDto createTestPolicyHolderDto() {
        return PolicyHolderDto.builder()
                .name("Иванов Иван Иванович")
                .contact(ContactDto.builder()
                        .phone(TEST_PHONE)
                        .email(TEST_EMAIL)
                        .build())
                .build();
    }

}
