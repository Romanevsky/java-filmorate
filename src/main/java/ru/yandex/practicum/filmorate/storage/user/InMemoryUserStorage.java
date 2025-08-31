package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<Integer>> friends = new HashMap<>();
    private int nextId = 1;

    @Override
    public User create(@NotNull User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @Override
    public User update(@NotNull User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        log.info("Пользователь обновлён: {}", user);
        return user;
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
        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
        log.info("Пользователь {} добавил в друзья {}", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (!users.containsKey(userId) || !users.containsKey(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        Set<Integer> userFriends = friends.get(userId);
        if (userFriends != null && userFriends.contains(friendId)) {
            userFriends.remove(friendId);
            friends.get(friendId).remove(userId);
            log.info("Пользователь {} удалил из друзей {}", userId, friendId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        Set<Integer> friendIds = friends.getOrDefault(userId, Collections.emptySet());
        return friendIds.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        if (!users.containsKey(userId) || !users.containsKey(otherId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        Set<Integer> userFriends = friends.getOrDefault(userId, Collections.emptySet());
        Set<Integer> otherFriends = friends.getOrDefault(otherId, Collections.emptySet());

        Set<Integer> commonFriendsIds = new HashSet<>(userFriends);
        commonFriendsIds.retainAll(otherFriends);

        return commonFriendsIds.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
