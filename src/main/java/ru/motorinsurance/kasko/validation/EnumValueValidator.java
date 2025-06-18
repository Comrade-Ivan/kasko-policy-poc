package ru.motorinsurance.kasko.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumValueValidator implements ConstraintValidator<ValidEnumValue, Object> {
    private Class<? extends Enum<?>> enumClass;
    private String allowedValues;

    @Override
    public void initialize(ValidEnumValue constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
        allowedValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Для проверки на null используйте @NotNull отдельно
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()
                        .replace("{allowedValues}", allowedValues))
                .addConstraintViolation();

        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equals(value.toString()));
    }
}
