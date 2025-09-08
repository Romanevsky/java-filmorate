package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;


@Slf4j
@Component
@Qualifier("filmDbStorage")
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;
    @Autowired
    GenreDbStorage genreStorage;
    @Autowired
    MpaDbStorage mpaStorage;

    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Collection<Film> findAll() {
        String query = "SELECT * FROM film";
        List<Film> films = jdbc.query(query, mapper);
        films.forEach(this::loadDetailsToFilm);

        return films;
    }

    @Override
    public Film findById(Long id) {
        String query = "SELECT * FROM film WHERE id = ?";
        Film film;
        try {
            film = jdbc.queryForObject(query, mapper, id);
            loadDetailsToFilm(film);
            return film;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    private void loadDetailsToFilm(Film film) {
        String query = "SELECT mpa_id FROM film WHERE id = ?";
        Long mpaId = jdbc.queryForObject(query, Long.class, film.getId());

        Mpa mpa = mpaStorage.findById(mpaId);
        film.setMpa(mpa);

        Set<Genre> genresOfFilm = new HashSet<>(genreStorage.getGenresByFilmId(film.getId()));
        film.setGenres(genresOfFilm);
    }

    @Override
    public Film create(Film film) {
        log.info("Валидация входящего запроса");
        validateFilm(film);

        String query = "INSERT INTO film(name, description, releaseDate, duration, mpa_id)" +
                "VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(query, new String[]{"id"});
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                ps.setInt(4, film.getDuration());

                if (film.getMpa() != null && film.getMpa().getId() != null) {
                    ps.setInt(5, film.getMpa().getId().intValue());
                } else {
                    ps.setNull(5, Types.INTEGER);
                }

                return ps;
            }, keyHolder);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Указанный MPA рейтинг не найден");
        }

        Long id = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        if (id == null) {
            throw new InternalServerException("Не удалось сохранить данные");
        }

        log.info("Добавлен фильм с идентификатором: {}", id);
        film.setId(id);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addGenresToFilm(id, film.getGenres());
        }
        return film;
    }

    private void addGenresToFilm(Long film_id, Set<Genre> genres) {
        String query = "INSERT INTO film_genres(film_id, genre_id)" +
                "VALUES (?, ?)";
        try {
            jdbc.batchUpdate(query, genres.stream()
                    .map(genre -> new Object[]{film_id, genre.getId()})
                    .collect(Collectors.toList()));
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Указанный жанр не найден");
        }
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (findById(newFilm.getId()) != null) {
            log.info("Валидация входящего запроса");
            validateFilm(newFilm);
            log.info("Обновление параметров фильма");
            String query = "UPDATE film SET name = ?, description = ?, releaseDate = ?, duration = ?";
            int rowsUpdated = jdbc.update(
                    query,
                    newFilm.getName(),
                    newFilm.getDescription(),
                    newFilm.getReleaseDate(),
                    newFilm.getDuration()
            );
            if (rowsUpdated == 0) {
                throw new InternalServerException("Не удалось обновить данные");
            }
            log.info("Фильма успешно обновлен");
            return newFilm;
        }

        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private void validateFilm(Film film) {
        if (film.getDescription().length() > 200) {
            log.warn("Описание фильма: {}", film.getDescription());
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12, 28))) {
            log.warn("Дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = findById(filmId);

        String query = "INSERT INTO film_likes(film_id, user_id)" +
                "VALUES (?, ?)";

        jdbc.update(query, filmId, userId);

        return film;
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        Film film = findById(filmId);

        String query = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(query, filmId, userId);

        return film;
    }

    @Override
    public Collection<Film> popularFilms(int count) {
        String query = "SELECT f.* FROM film f " +
                "LEFT JOIN (SELECT film_id, COUNT(user_id) AS like_count " +
                "           FROM film_likes GROUP BY film_id) fl " +
                "ON f.id = fl.film_id " +
                "ORDER BY COALESCE(fl.like_count, 0) DESC " +
                "LIMIT ?";
        List<Film> films = jdbc.query(query, mapper, count);
        films.forEach(this::loadDetailsToFilm);

        return films;
    }
}