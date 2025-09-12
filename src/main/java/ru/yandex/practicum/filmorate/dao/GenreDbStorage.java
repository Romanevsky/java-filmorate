package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Collection<Genre> findAll() {
        String query = "SELECT * FROM genres";
        return jdbc.query(query, mapper);
    }

    @Override
    public Genre findById(Long id) {
        String query = "SELECT * FROM genres WHERE id = ?";
        Genre genre;
        try {
            genre = jdbc.queryForObject(query, mapper, id);
            return genre;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Жанр с id = " + id + " не найден");
        }
    }

    public Collection<Genre> getGenresByFilmId(Long filmId) {
        String query = "SELECT g.* " +
                "FROM FILM_GENRES fg " +
                "JOIN GENRES g ON g.ID = fg.GENRE_ID " +
                "WHERE fg.FILM_ID = ?";
        return jdbc.query(query, mapper, filmId);
    }
}