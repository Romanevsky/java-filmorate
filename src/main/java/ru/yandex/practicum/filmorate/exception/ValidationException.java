package ru.yandex.practicum.filmorate.exception;

/**
 * Исключения для валидации
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
