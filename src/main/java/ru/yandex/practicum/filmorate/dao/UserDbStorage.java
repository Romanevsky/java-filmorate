package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
@Qualifier("userDbStorage")
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    public UserDbStorage(JdbcTemplate jdbc, UserRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Collection<User> findAll() {
        String query = "SELECT * FROM \"user\"";
        return jdbc.query(query, mapper);
    }

    @Override
    public User findById(Long id) {
        String query = "SELECT * FROM \"user\" WHERE id = ?";
        User user;
        try {
            user = jdbc.queryForObject(query, mapper, id);
            return user;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }

    }

    @Override
    public User create(User user) {
        log.info("Валидация входящего запроса");
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя не заполнено, поэтому присваиваем ему значение логина: {}", user.getLogin());
            user.setName(user.getLogin());
        }
        String query = "INSERT INTO \"user\"(email, login, name, birthday)" +
                "VALUES (?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        if (id != null) {
            log.info("Создан пользователь с идентификатором: {}", id);
            user.setId(id);
            return user;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        log.info("Валидация входящего запроса");
        validateUser(newUser);

        if (findById(newUser.getId()) != null) {
            String query = "UPDATE \"user\" SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
            int rowsUpdated = jdbc.update(
                    query,
                    newUser.getEmail(),
                    newUser.getLogin(),
                    newUser.getName(),
                    newUser.getBirthday(),
                    newUser.getId()
            );
            if (rowsUpdated == 0) {
                throw new InternalServerException("Не удалось обновить данные");
            }
            return newUser;
        } else {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }
    }

    @Override
    public Collection<User> addFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }
        String query = "INSERT INTO user_friendship (user_id, friend_id, confirmed) VALUES (?,?,?)";
        jdbc.update(query, userId, friendId, true);

        return List.of(user, friend);
    }

    @Override
    public Collection<User> deleteFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id = " + friendId + " не найден");
        }

        String query = "DELETE FROM user_friendship WHERE user_id = ? AND friend_id = ?";
        jdbc.update(query, userId, friendId);

        return List.of(user, friend);
    }

    @Override
    public Collection<User> findFriends(Long userId) {
        User user = findById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        String query = "SELECT * FROM \"user\" u " +
                "JOIN user_friendship f ON f.friend_id = u.id WHERE f.user_id = ?";
        return jdbc.query(query, mapper, userId);
    }

    @Override
    public Collection<User> findCommonFriends(Long userId, Long otherId) {
        User user = findById(userId);
        User other = findById(otherId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (other == null) {
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        }

        String query = "SELECT * FROM user_friendship f1 " +
                "JOIN user_friendship f2 ON f1.friend_id = f2.friend_id " +
                "JOIN \"user\" u on f1.friend_id = u.id WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbc.query(query, mapper, userId, otherId);
    }

    private void validateUser(User user) {
        if (!user.getEmail().contains("@")) {
            log.warn("Email пользователя: {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Логин пользователя: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения пользователя: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        log.info("Валидация прошла успешно");
    }
}