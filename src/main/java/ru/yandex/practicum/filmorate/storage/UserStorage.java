package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User findById(Long id);

    User create(User user);

    User update(User newUser);

    Collection<User> addFriend(Long userId, Long friendId);

    Collection<User> deleteFriend(Long userId, Long friendId);

    Collection<User> findFriends(Long userId);

    Collection<User> findCommonFriends(Long userId, Long otherId);
}