package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void validateUser(User user) throws ValidationException {
        if (user.getEmail() == null || !user.getEmail().matches(".+@.+\\..+")) {
            throw new ValidationException("Email должен быть валидным адресом электронной почты.");
        }
        if (user.getLogin() == null || user.getLogin().trim().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы.");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            validateUser(user); // Валидация перед созданием
            User createdUser = userService.createUser(user);
            log.info("Пользователь создан: {}", createdUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла внутренняя ошибка сервера"));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody User updatedUser) {
        try {
            validateUser(updatedUser); // Валидация перед обновлением
            User updated = userService.updateUser(updatedUser);
            log.info("Пользователь обновлён: {}", updated);
            return ResponseEntity.ok(updated);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла внутренняя ошибка сервера"));
        }
    }

    @GetMapping
    public List getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable int id, @PathVariable int friendId) {
        try {
            userService.addFriend(id, friendId);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла внутренняя ошибка сервера"));
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        try {
            userService.removeFriend(id, friendId);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Произошла внутренняя ошибка сервера"));
        }
    }

    @GetMapping("/{id}/friends")
    public List getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
