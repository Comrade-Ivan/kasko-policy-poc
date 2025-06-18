package ru.motorinsurance.kasko.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Обработка ошибок валидации @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации",
                errors.toString()
        );

        return ResponseEntity.badRequest().body(response);
    }

    // Обработка ошибок валидации для @RequestParam, @PathVariable и т.д.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s",
                        violation.getPropertyPath(),
                        violation.getMessage()))
                .collect(Collectors.toList());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации параметров",
                errors.toString()
        );

        return ResponseEntity.badRequest().body(response);
    }

    // Обработка неверного типа Enum (например, когда передали строку вместо значения Enum)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = String.format("Параметр '%s' имеет неверное значение '%s'. Ожидается: %s",
                ex.getName(),
                ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Неверный тип параметра",
                error
        );

        return ResponseEntity.badRequest().body(response);
    }

    // Обработка кастомных бизнес-исключений
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                ex.getStatus().value(),
                ex.getError(),
                ex.getMessage()
        );

        return new ResponseEntity<>(response, ex.getStatus());
    }

    // Обработка всех непредвиденных исключений
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Internal server error: ", ex);

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Внутренняя ошибка сервера",
                "Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже."
        );

        return ResponseEntity.internalServerError().body(response);
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
    }

    // Базовый класс для бизнес-исключений
    public static abstract class BusinessException extends RuntimeException {
        private final HttpStatus status;
        private final String error;

        protected BusinessException(HttpStatus status, String error, String message) {
            super(message);
            this.status = status;
            this.error = error;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }
    }

    // Пример кастомного исключения
    public static class PolicyNotFoundException extends BusinessException {
        public PolicyNotFoundException(String policyId) {
            super(HttpStatus.NOT_FOUND,
                    "Policy Not Found",
                    String.format("Полис с ID %s не найден", policyId));
        }
    }
}