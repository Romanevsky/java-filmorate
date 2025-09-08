package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage storageFilm;
    private final UserStorage storageUser;

    public Collection<Film> findAll() {
        return storageFilm.findAll();
    }

    public Film findById(Long id) {
        return storageFilm.findById(id);
    }

    public Film create(Film film) {
        return storageFilm.create(film);
    }

    public Film update(Film newFilm) {
        return storageFilm.update(newFilm);
    }

    public Film addLike(Long filmId, Long userId) {
        Film film = storageFilm.findById(filmId);
        User user = storageUser.findById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Данный пользователь уже поставил лайк фильму");
        }

        film.getLikes().add(userId);
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        Film film = storageFilm.findById(filmId);
        User user = storageUser.findById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        if (!film.getLikes().contains(userId)) {
            throw new ValidationException("Данный пользователь не поставил лайк фильму");
        }

        film.getLikes().remove(userId);
        return film;
    }

    public Collection<Film> popularFilms(int count) {
        return storageFilm.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
