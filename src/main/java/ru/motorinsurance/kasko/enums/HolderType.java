package ru.motorinsurance.kasko.enums;

import lombok.Getter;

@Getter
public enum HolderType {
    INDIVIDUAL("Физ.Лицо"),
    LEGAL_ENTITY("Юр.Лицо");

    private final String russianName;

    HolderType(String russianName) {
        this.russianName = russianName;
    }

    public static HolderType fromString(String type) {
        for (HolderType holderType : values() ) {
            if (holderType.russianName.equalsIgnoreCase(type)) {
                return holderType;
            }
        }
        throw new IllegalArgumentException("Неизвестный тип страхователя: " + type);
    }

    @Override
    public String toString() {
        return this.russianName;
    }
}
