package ru.motorinsurance.kasko.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyHolderDto {
    @NotBlank
    private String type; // "Физ.Лицо" или "Юр.Лицо"

    @NotBlank
    private String name; // ФИО или название организации

    @Valid
    @NotNull
    private ContactDto contact;

    // Документы могут быть добавлены отдельным запросом
}

