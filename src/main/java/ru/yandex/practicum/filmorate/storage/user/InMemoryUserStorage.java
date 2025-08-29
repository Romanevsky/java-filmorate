package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();
    private int nextId = 1;

    @Override
    public User create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null");
        }
        user.setId(nextId++);
        users.put(user.getId(), user);
        friends.putIfAbsent(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public User update(User user) {
        if (user == null || !users.containsKey(user.getId())) {
            throw new NoSuchElementException("Пользователь с ID " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (!users.containsKey(userId)) {
            throw new NoSuchElementException("Пользователь с ID " + userId + " не найден");
        }
        if (!users.containsKey(friendId)) {
            throw new NoSuchElementException("Пользователь с ID " + friendId + " не найден");
        }
        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            throw new NoSuchElementException("Один из пользователей не существует");
        }
        friends.getOrDefault(userId, new HashSet<>()).remove(friendId);
        friends.getOrDefault(friendId, new HashSet<>()).remove(userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        if (!users.containsKey(userId)) {
            throw new NoSuchElementException("Пользователь с ID " + userId + " не найден");
        }
        return friends.getOrDefault(userId, new HashSet<>())
                .stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        if (!users.containsKey(userId) || !users.containsKey(otherId)) {
            throw new NoSuchElementException("Один из пользователей не существует");
        }
        Set<Integer> userFriends = friends.getOrDefault(userId, new HashSet<>());
        Set<Integer> otherFriends = friends.getOrDefault(otherId, new HashSet<>());

        userFriends.retainAll(otherFriends);

        return userFriends.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }
}
