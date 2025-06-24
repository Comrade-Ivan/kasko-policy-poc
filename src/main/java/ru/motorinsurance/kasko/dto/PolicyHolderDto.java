package ru.motorinsurance.kasko.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import ru.motorinsurance.kasko.enums.HolderType;
import ru.motorinsurance.kasko.validation.ValidEnumValue;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyHolderDto {
    @NotBlank
    @ValidEnumValue(
            enumClass = HolderType.class,
            ignoreCase = true,
            useRussianName = true,
            message = "Неподходящий тип страхователя"
    )
    private String type; // "Физ.Лицо" или "Юр.Лицо"

    @NotBlank
    private String name; // ФИО или название организации

    @Valid
    @NotNull
    private ContactDto contact;

    // Документы могут быть добавлены отдельным запросом
}

