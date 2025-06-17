package ru.motorinsurance.kasko.model;

import lombok.*;
import jakarta.persistence.*;
import ru.motorinsurance.kasko.enums.VehicleUsagePurpose;

import java.time.LocalDate;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "vin", nullable = false, unique = true, length = 17)
    private String vin;

    @Column(name = "mileage")
    private Integer mileage;

    @Column(name = "actual_value", precision = 12, scale = 2)
    private Double actualValue;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_purpose", length = 30)
    private VehicleUsagePurpose usagePurpose;

    @Column(name = "registration_number", length = 15)
    private String registrationNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", referencedColumnName = "policy_id")
    private Policy policy;
}