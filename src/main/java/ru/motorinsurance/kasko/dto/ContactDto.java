package ru.motorinsurance.kasko.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {
    @NotBlank
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Phone must be in format +7XXXXXXXXXX")
    private String phone;

    @Email
    private String email; // Необязательное для юр. лиц
}
