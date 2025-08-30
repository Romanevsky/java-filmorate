package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    // Для хранения друзей
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        }
        return null;
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        friends.computeIfPresent(userId, (k, v) -> {
            v.remove(friendId);
            return v.isEmpty() ? null : v;
        });
    }

    @Override
    public List<User> getFriends(int userId) {
        return friends.getOrDefault(userId, Collections.emptySet())
                .stream()
                .map(this::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Integer> userFriends = friends.getOrDefault(userId, Collections.emptySet());
        Set<Integer> otherFriends = friends.getOrDefault(otherId, Collections.emptySet());

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
