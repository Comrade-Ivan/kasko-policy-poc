package ru.motorinsurance.kasko.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
@Slf4j
public class EnumValueValidator implements ConstraintValidator<ValidEnumValue, Object> {
    private Class<? extends Enum<?>> enumClass;
    private String allowedValues;
    private boolean nullable;
    private boolean ignoreCase;
    private boolean useRussianName;

    @Override
    public void initialize(ValidEnumValue constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.allowedValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        this.ignoreCase = constraintAnnotation.ignoreCase();
        this.nullable = constraintAnnotation.nullable();
        this.useRussianName = constraintAnnotation.useRussianName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return nullable;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()
                        .replace("{allowedValues}", allowedValues))
                .addConstraintViolation();
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> {
                    try {
                        if (useRussianName) {
                            Method method = e.getClass().getMethod("getRussianName");
                            String russianName = (String) method.invoke(e);
                            return ignoreCase
                                ? russianName.equalsIgnoreCase(value.toString())
                                : russianName.equals(value.toString());
                        }
                        return ignoreCase
                                ? e.name().equalsIgnoreCase(value.toString())
                                : e.name().equals(value.toString());
                    } catch (Exception ex) {
                        return false;
                    }
                });
    }
}
