package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserControllerTest {
    private final UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

    @DisplayName("Тест валидации пользователя")
    @Test
    void testValidUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        ResponseEntity<?> response = userController.createUser(user);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @DisplayName("Тест валидации пользователя с невалидным email")
    @Test
    void testInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("login");
        ResponseEntity<?> response = userController.createUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((Map<String, String>) response.getBody()).get("error"));
    }

    @DisplayName("Тест валидации пользователя с пустым логином")
    @Test
    void testEmptyLogin() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin(""); // или null
        ResponseEntity<?> response = userController.createUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((Map<String, String>) response.getBody()).get("error"));
    }

    @DisplayName("Тест валидации пользователя с логином, содержащим пробел")
    @Test
    void testLoginWithSpace() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("log in"); // содержит пробел
        ResponseEntity<?> response = userController.createUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((Map<String, String>) response.getBody()).get("error"));
    }

    @DisplayName("Тест валидации пользователя с датой рождения в будущем")
    @Test
    void testFutureBirthday() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1)); // дата в будущем
        ResponseEntity<?> response = userController.createUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((Map<String, String>) response.getBody()).get("error"));
    }

    @DisplayName("Тест валидации пользователя с пустым именем")
    @Test
    void testEmptyName() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName(""); // пустое имя
        ResponseEntity<?> response = userController.createUser(user);
        User createdUser = (User) response.getBody();
        assertEquals("login", createdUser.getName()); // имя должно замениться на логин
    }
}
