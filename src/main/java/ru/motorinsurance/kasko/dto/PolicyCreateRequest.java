package ru.motorinsurance.kasko.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.motorinsurance.common.core.dto.PolicyHolderDto;
import ru.motorinsurance.common.core.dto.VehicleDto;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyCreateRequest {

    @Valid
    @NotNull
    private VehicleDto vehicle;

    @Valid
    @NotNull
    private PolicyHolderDto policyHolder;

    @Valid //TODO: add validation
    private String drivers;

    private LocalDate startDate;
}
