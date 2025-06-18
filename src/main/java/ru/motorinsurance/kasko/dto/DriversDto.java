package ru.motorinsurance.kasko.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriversDto {
    @NotNull
    private String type;
    @Valid
    private List<Driver> drivers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Driver {
        @NotNull
        private String fullName;
        @NotNull
        @Min(0)
        private Integer experience;
        @NotNull
        @Min(16)
        private Integer age;
    }
}
