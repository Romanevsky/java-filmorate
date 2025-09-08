package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    @Qualifier("userDbStorage")
    private final UserStorage storage;

    public Collection<User> findAll() {
        return storage.findAll();
    }

    public User findById(Long id) {
        return storage.findById(id);
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User newUser) {
        return storage.update(newUser);
    }

    public Collection<User> addFriend(Long userId, Long friendId) {
        return storage.addFriend(userId, friendId);
    }

    public Collection<User> deleteFriend(Long userId, Long friendId) {
        return storage.deleteFriend(userId, friendId);
    }

    public Collection<User> findFriends(Long userId) {
        return storage.findFriends(userId);
    }

    public Collection<User> findCommonFriends(Long userId, Long otherId) {
        return storage.findCommonFriends(userId, otherId);
    }

}