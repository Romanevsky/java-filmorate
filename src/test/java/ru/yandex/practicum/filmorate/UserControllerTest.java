package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    @DisplayName("Тест валидации пользователя")
    @Test
    void testValidUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertDoesNotThrow(() -> userController.validateUser(user));
    }

    @DisplayName("Тест валидации пользователя с невалидным email")
    @Test
    void testInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("login");

        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @DisplayName("Тест валидации пользователя с пустым логином")
    @Test
    void testEmptyLogin() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin(""); // или null

        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @DisplayName("Тест валидации пользователя с логином, содержащим пробел")
    @Test
    void testLoginWithSpace() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("log in"); // содержит пробел

        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @DisplayName("Тест валидации пользователя с датой рождения в будущем")
    @Test
    void testFutureBirthday() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1)); // дата в будущем

        assertThrows(ValidationException.class, () -> userController.validateUser(user));
    }

    @DisplayName("Тест валидации пользователя с пустым именем")
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
