package project.telegram.weatherbot.controller;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import project.telegram.weatherbot.exception.ValidationException;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<ExceptionMessage> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ExceptionMessage.builder()
                        .error(ex.getMessage())
                        .build());
    }

    @Data
    @Builder
    public static class ExceptionMessage {
        private String error;
    }
}
