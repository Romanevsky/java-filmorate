package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public Film create(@NotNull Film film) {
        return filmStorage.create(film);
    }

    public Film update(@NotNull Film film) {
        return filmStorage.update(film);
    }

    public Film getById(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
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
        return filmStorage.getPopular(count);
    }
}
