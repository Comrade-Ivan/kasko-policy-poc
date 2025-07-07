package ru.motorinsurance.kasko;

import ru.motorinsurance.common.core.dto.ContactDto;
import ru.motorinsurance.common.core.dto.PolicyHolderDto;
import ru.motorinsurance.common.core.dto.VehicleDto;
import ru.motorinsurance.common.core.enums.HolderType;
import ru.motorinsurance.common.core.enums.PolicyStatus;
import ru.motorinsurance.common.core.enums.VehicleUsagePurpose;
import ru.motorinsurance.kasko.dto.*;
import ru.motorinsurance.kasko.model.Policy;
import ru.motorinsurance.kasko.model.PolicyHolder;
import ru.motorinsurance.kasko.model.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestDataFactory {

    public static final String TEST_VIN = "XTA21099765432100";
    public static final String TEST_PHONE = "+79161234567";
    public static final String TEST_EMAIL = "test@example.com";

    public static Vehicle createTestVehicle() {
        return Vehicle.builder()
                .vin(TEST_VIN)
                .mileage(45000)
                .actualValue(BigDecimal.valueOf(1250000))
                .purchaseDate(LocalDate.now())
                .build();
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

    public static Policy createTestPolicy() {
        return Policy.builder()
                .policyId("KASKO-2024-123456")
                .createdAt(LocalDateTime.now())
                .startDate(LocalDate.now())
                .status(PolicyStatus.PRE_CALCULATION)
                .isCancelled(false)
                .vehicle(createTestVehicle())
                .policyHolder(createTestPolicyHolder())
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

    public static PolicyCreateRequest createTestRequest() {
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
                .startDate(LocalDate.now())
                .build();
    }

}
