package ru.motorinsurance.kasko.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ru.motorinsurance.kasko.enums.PolicyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Policy {
    private String policyId;
    private ZonedDateTime createdAt;
    private ZonedDateTime issuedAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal premiumAmount;
    private PolicyStatus status;
    private String paymentMethod;
    private Boolean isCancelled;
    private String cancellationReason;
    private String s3PdfPath;
    private Vehicle vehicle;
    private PolicyHolder policyHolder;
    private Drivers drivers;
}
