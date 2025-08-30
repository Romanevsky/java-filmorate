package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        validateUser(user);
        return userStorage.create(user);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Неверный email");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не должен содержать пробелов");
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    public User update(User user) {
        User existingUser = getById(user.getId());
        return userStorage.update(user);
    }

    public User getById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public void addFriend(int userId, int friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = getById(userId);
        User friend = getById(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        User user = getById(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = getById(userId);
        User other = getById(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }
}
