package ru.yandex.practicum.filmorate.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public Collection<Mpa> findAll() {
        String query = "SELECT * FROM mpa";
        return jdbc.query(query, mapper);
    }

    @Override
    public Mpa findById(Long id) {
        String query = "SELECT * FROM mpa WHERE id = ?";
        Mpa mpa;
        try {
            mpa = jdbc.queryForObject(query, mapper, id);
            return mpa;
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("Рейтинг с id = " + id + " не найден");
        }
    }
}