package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage);
    private final UserController userController = new UserController(userService);

    @DisplayName("Тест добавления друга")
    @Test
    void testAddFriend() {
        // Создаем пользователей
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("Пользователь 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("Пользователь 2");
        user2.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser1 = userService.create(user1);
        User createdUser2 = userService.create(user2);

        // Добавляем друга
        assertDoesNotThrow(() -> userController.addFriend(createdUser1.getId(), createdUser2.getId()));

        // Проверяем, что друг был добавлен
        List<User> friends1 = userService.getFriends(createdUser1.getId());
        List<User> friends2 = userService.getFriends(createdUser2.getId());

        assertEquals(1, friends1.size());
        assertEquals(createdUser2.getId(), friends1.get(0).getId());

        assertEquals(1, friends2.size());
    }
}