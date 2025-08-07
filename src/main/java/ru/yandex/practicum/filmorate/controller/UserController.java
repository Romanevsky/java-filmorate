package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для работы с пользователями.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    /**
     * Создание пользователя.
     *
     * @param user объект пользователя
     * @return ResponseEntity<User> или ResponseEntity<Error>
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            validateUser(user);
            user.setId(nextId++);
            users.put(user.getId(), user);
            log.info("Пользователь создан: {}", user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Обновление пользователя.
     *
     * @param updatedUser обновлённый пользователь
     * @return ResponseEntity<User> или ResponseEntity<Error>
     */
    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody User updatedUser) {
        try {
            validateUser(updatedUser);
            if (users.containsKey(updatedUser.getId())) {
                users.put(updatedUser.getId(), updatedUser);
                log.info("Пользователь обновлён: {}", updatedUser);
                return ResponseEntity.ok(updatedUser);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Пользователь не найден"));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Получение всех пользователей.
     *
     * @return список всех пользователей
     */
    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Валидация данных пользователя.
     *
     * @param user объект пользователя
     * @throws ValidationException если данные невалидны
     */
    public void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Email должен быть валидным и содержать символ '@'.");
        }

        if (user.getLogin() == null || user.getLogin().trim().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }
}
