package ru.motorinsurance.kasko.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    private String vin;
    private Integer mileage;
    private BigDecimal actualValue;
    private LocalDate purchaseDate;
    private String usagePurpose;
    private String registrationNumber;
}
