package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testlogin");
        testUser.setName("Тестовый пользователь");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("Тест валидации пользователя")
    void testValidUser() {
        ResponseEntity<?> response = userController.create(testUser);
        assertNotNull(response);

        User createdUser = (User) response.getBody();
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
    }

    @Test
    @DisplayName("Тест валидации пользователя с невалидным email")
    void testInvalidEmail() {
        testUser.setEmail("invalid-email");
        ResponseEntity<?> response = userController.create(testUser);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Тест валидации пользователя с пустым логином")
    void testEmptyLogin() {
        testUser.setLogin("");
        ResponseEntity<?> response = userController.create(testUser);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Тест валидации пользователя с логином, содержащим пробел")
    void testLoginWithSpace() {
        testUser.setLogin("log in");
        ResponseEntity<?> response = userController.create(testUser);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Тест валидации пользователя с датой рождения в будущем")
    void testFutureBirthday() {
        testUser.setBirthday(LocalDate.now().plusDays(1));
        ResponseEntity<?> response = userController.create(testUser);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Тест валидации пользователя с пустым именем")
    void testEmptyName() {
        testUser.setName("");
        ResponseEntity<?> response = userController.create(testUser);

        assertNotNull(response);
        User createdUser = (User) response.getBody();
        assertNotNull(createdUser);
        assertEquals("testlogin", createdUser.getName()); // имя должно быть заменено на логин
    }

    @Test
    @DisplayName("Тест добавления и удаления друга")
    void testAddAndRemoveFriend() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("Пользователь 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("Пользователь 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));

        ResponseEntity<?> response1 = userController.create(user1);
        ResponseEntity<?> response2 = userController.create(user2);

        assertNotNull(response1);
        assertNotNull(response2);

        User createdUser1 = (User) response1.getBody();
        User createdUser2 = (User) response2.getBody();

        assertNotNull(createdUser1);
        assertNotNull(createdUser2);

        userController.addFriend(createdUser1.getId(), createdUser2.getId());
        assertTrue(userController.getFriends(createdUser1.getId()).contains(createdUser2));

        userController.removeFriend(createdUser1.getId(), createdUser2.getId());
        assertFalse(userController.getFriends(createdUser1.getId()).contains(createdUser2));
    }

    @Test
    @DisplayName("Тест получения общих друзей")
    void testGetCommonFriends() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("Пользователь 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("Пользователь 2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));

        User user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setLogin("user3");
        user3.setName("Пользователь 3");
        user3.setBirthday(LocalDate.of(1992, 1, 1));

        ResponseEntity<?> response1 = userController.create(user1);
        ResponseEntity<?> response2 = userController.create(user2);
        ResponseEntity<?> response3 = userController.create(user3);

        assertNotNull(response1);
        assertNotNull(response2);
        assertNotNull(response3);

        User createdUser1 = (User) response1.getBody();
        User createdUser2 = (User) response2.getBody();
        User createdUser3 = (User) response3.getBody();

        assertNotNull(createdUser1);
        assertNotNull(createdUser2);
        assertNotNull(createdUser3);

        userController.addFriend(createdUser1.getId(), createdUser3.getId());
        userController.addFriend(createdUser2.getId(), createdUser3.getId());

        List<User> commonFriends = userController.getCommonFriends(createdUser1.getId(), createdUser2.getId());
        assertEquals(1, commonFriends.size());
        assertEquals(createdUser3.getId(), commonFriends.get(0).getId());
    }
}
