package ru.motorinsurance.kasko.enums;

import lombok.Getter;

@Getter
public enum VehicleUsagePurpose {
    PERSONAL("Личное использование"),
    COMMERCIAL("Коммерческие перевозки"),
    TAXI("Такси"),
    CARGO("Грузоперевозки"),
    SPECIAL("Специальный транспорт");

    private final String russianName;

    VehicleUsagePurpose(String russianName) {
        this.russianName = russianName;
    }

    public static VehicleUsagePurpose fromRussianName(String russianName) {
        for (VehicleUsagePurpose purpose : values()) {
            if (purpose.russianName.equalsIgnoreCase(russianName)) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("Неизвестное назначение ТС: " + russianName);
    }

    @Override
    public String toString() {
        return this.russianName;
    }
}