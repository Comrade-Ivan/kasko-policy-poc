package ru.motorinsurance.kasko.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.motorinsurance.common.core.enums.PolicyStatus;
import ru.motorinsurance.common.core.validation.ValidEnumValue;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyChangeStatusRequest {
    @NotBlank
    private String policyId;

    @NotNull
    @ValidEnumValue(
            enumClass = PolicyStatus.class,
            ignoreCase = true,
            useRussianName = true
    )
    private String targetStatus;
}
