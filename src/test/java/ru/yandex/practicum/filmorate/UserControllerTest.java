package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userServiceMock;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userServiceMock = Mockito.mock(UserService.class);
        userController = new UserController(userServiceMock);
    }

    @Test
    void testCreateUserSuccess() {
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        when(userServiceMock.createUser(user)).thenReturn(user);

        ResponseEntity<?> response = userController.createUser(user);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, ((ResponseEntity<User>) response).getBody());
    }

    @Test
    void testCreateUserValidationFailure() {
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        user.setLogin(""); // Некорректный логин
        user.setName("Имя");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        when(userServiceMock.createUser(user)).thenThrow(new ValidationException("Ошибка валидации"));

        ResponseEntity<?> response = userController.createUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((ResponseEntity<Map<String, String>>) response).getBody().get("error"));
    }

    @Test
    void testUpdateUserSuccess() {
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Новое имя");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        when(userServiceMock.updateUser(user)).thenReturn(user);

        ResponseEntity<?> response = userController.updateUser(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, ((ResponseEntity<User>) response).getBody());
    }

    @Test
    void testUpdateUserValidationFailure() {
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        user.setLogin(""); // Некорректный логин
        user.setName("Имя");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        when(userServiceMock.updateUser(user)).thenThrow(new ValidationException("Ошибка валидации"));

        ResponseEntity<?> response = userController.updateUser(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((ResponseEntity<Map<String, String>>) response).getBody().get("error"));
    }


    @Test
    void testGetUserByIdNotFound() {
        when(userServiceMock.getUserById(1)).thenThrow(new ValidationException("Пользователь не найден"));

        ResponseEntity<?> response = userController.getUserById(1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(((ResponseEntity<Map<String, String>>) response).getBody().get("error"));
    }

    @Test
    void testAddFriendSuccess() {
        doNothing().when(userServiceMock).addFriend(1, 2);

        ResponseEntity<?> response = userController.addFriend(1, 2);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testAddFriendValidationFailure() {
        doThrow(new ValidationException("Ошибка добавления друга"))
                .when(userServiceMock).addFriend(1, 2);

        ResponseEntity<?> response = userController.addFriend(1, 2);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((ResponseEntity<Map<String, String>>) response).getBody().get("error"));
    }

    @Test
    void testRemoveFriend() {
        doNothing().when(userServiceMock).removeFriend(1, 2);

        ResponseEntity<?> response = userController.removeFriend(1, 2);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetFriendsNotFound() {
        when(userServiceMock.getFriends(1)).thenThrow(new ValidationException("Пользователь не найден"));

        ResponseEntity<?> response = userController.getFriends(1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(((ResponseEntity<Map<String, String>>) response).getBody().get("error"));
    }

    @Test
    void testGetCommonFriendsNotFound() {
        when(userServiceMock.getCommonFriends(1, 2)).thenThrow(new ValidationException("Нет общих друзей"));

        ResponseEntity<?> response = userController.getCommonFriends(1, 2);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(((ResponseEntity<Map<String, String>>) response).getBody().get("error"));
    }
}
