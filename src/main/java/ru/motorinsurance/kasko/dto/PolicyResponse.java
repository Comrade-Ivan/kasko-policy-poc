package ru.motorinsurance.kasko.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyResponse {
    private String policyId;
    private String status;
    private BigDecimal premiumAmount;
    private String currency;
    private String startDate;
    private String endDate;
    private VehicleDto vehicle;
    private PolicyHolderDto policyHolder;
    private String drivers;
}
