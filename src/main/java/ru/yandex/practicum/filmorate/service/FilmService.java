package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {
    @Autowired
    @Qualifier("filmDbStorage")
    private final FilmStorage storageFilm;

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
        return storageFilm.addLike(filmId, userId);
    }

    public Film deleteLike(Long filmId, Long userId) {
        return storageFilm.deleteLike(filmId, userId);
    }

    public Collection<Film> popularFilms(int count) {
        return storageFilm.popularFilms(count);
    }
}