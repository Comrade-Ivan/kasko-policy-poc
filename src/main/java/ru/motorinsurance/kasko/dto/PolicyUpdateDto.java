package ru.motorinsurance.kasko.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.motorinsurance.common.core.dto.DriversDto;
import ru.motorinsurance.common.core.dto.VehicleDto;
import ru.motorinsurance.common.core.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PolicyUpdateDto {
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate startDate;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate endDate;
    private BigDecimal premiumAmount;
    private PaymentMethod paymentMethod;
    private Long policyHolderId;
    private VehicleDto vehicle;
    private DriversDto drivers;
}
