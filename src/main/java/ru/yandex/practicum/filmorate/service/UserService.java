package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(@NotNull User user) {
        return userStorage.create(user);
    }

    public User update(@NotNull User user) {
        return userStorage.update(user);
    }

    public User getById(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }
}
