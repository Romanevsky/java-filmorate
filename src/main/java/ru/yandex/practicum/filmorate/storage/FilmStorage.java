package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film findById(Long id);

    Film create(Film film);

    Film update(Film newFilm);

    Film addLike(Long filmId, Long userId);

    Film deleteLike(Long filmId, Long userId);

    Collection<Film> popularFilms(int count);
}