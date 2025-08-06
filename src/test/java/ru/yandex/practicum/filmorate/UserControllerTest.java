package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private final UserController userController = new UserController();

    @Test
    void testValidUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertDoesNotThrow(() -> userController.validateUser(user));
    }

    @Test
    void testInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("login");

        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void testEmptyLogin() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin(""); // или null

        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void testLoginWithSpace() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("log in"); // содержит пробел

        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void testFutureBirthday() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1)); // дата в будущем

        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @Test
    void testEmptyName() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName(""); // пустое имя

        userController.validateUser(user);
        assertEquals("login", user.getName()); // имя должно замениться на логин
    }
}
