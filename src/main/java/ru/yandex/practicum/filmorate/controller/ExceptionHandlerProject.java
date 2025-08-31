package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerProject {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException ex) {
        log.warn("Обнаружена ошибка: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException ex) {
        log.warn("Обнаружена ошибка: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Обнаружена ошибка: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleDataAccessException(DataAccessException ex) {
        log.error("Произошла ошибка доступа к данным: {}", ex.getMessage());
        return Map.of("error", "Произошла внутренняя ошибка сервера");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Exception ex) {
        log.error("Произошла непредвиденная ошибка: {}", ex.getMessage());
        return Map.of("error", "Произошла внутренняя ошибка сервера");
    }
}
