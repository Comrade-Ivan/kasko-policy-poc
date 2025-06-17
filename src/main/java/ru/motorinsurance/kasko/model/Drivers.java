package ru.motorinsurance.kasko.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Drivers {
    private String type;
    private List<Driver> drivers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Driver {
        private String fullName;
        private Integer experience;
        private Integer age;
    }
}
