package ru.motorinsurance.kasko.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import ru.motorinsurance.kasko.enums.VehicleUsagePurpose;
import ru.motorinsurance.kasko.model.Vehicle;
import ru.motorinsurance.kasko.validation.ValidEnumValue;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {
    @NotBlank
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "VIN must be 17 alphanumeric characters")
    private String vin;

    @Min(0)
    private Integer mileage;

    @DecimalMin("0.0")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal actualValue;

    @NotNull
    private String purchaseDate; // Формат "dd.MM.yyyy"

    @NotNull(message = "Цель использования не может быть пустой")
    @ValidEnumValue(
            enumClass = VehicleUsagePurpose.class,
            message = "Недопустимая цель использования ТС. Допустимые: {allowedValues}"
    )
    private VehicleUsagePurpose usagePurpose; // "Личное" или "Коммерческое"

    private String registrationNumber; // Гос. номер (необязательное)
}
