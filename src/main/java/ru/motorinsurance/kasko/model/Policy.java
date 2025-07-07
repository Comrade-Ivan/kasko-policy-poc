package ru.motorinsurance.kasko.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ru.motorinsurance.common.core.dto.DriversDto;
import ru.motorinsurance.common.core.enums.PaymentMethod;
import ru.motorinsurance.common.core.enums.PolicyStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "policies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Policy {

    @Id
    @Column(name = "policy_id", length = 20)
    @EqualsAndHashCode.Include
    private String policyId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "premium_amount", columnDefinition = "numeric(19,2)")
    private BigDecimal premiumAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private PolicyStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "is_cancelled")
    @Builder.Default
    private Boolean isCancelled = false;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "s3_pdf_path", length = 255)
    private String s3PdfPath;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "drivers", columnDefinition = "jsonb")
    private DriversDto drivers; //TODO: переделать со String на Dto

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holder_id")
    @ToString.Exclude
    private PolicyHolder policyHolder;

    @OneToOne(mappedBy = "policy", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Vehicle vehicle;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<StatusTransition> statusTransitions;
}
