package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film create(Film film) {
        if (film.getName() == null || film.getName().trim().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание не должно превышать 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 1, 1))) {
            throw new ValidationException("Дата релиза не может быть раньше 1895 года.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной.");
        }

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getById(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public int getLikeCount(int filmId) {
        return filmStorage.getLikeCount(filmId);
    }
}
