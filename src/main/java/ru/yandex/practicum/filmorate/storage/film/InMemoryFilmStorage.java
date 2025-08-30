package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();
    private int nextId = 1;

    @Override
    public Film create(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NoSuchElementException("Фильм с ID " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> getById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(int filmId, int userId) {
        if (!films.containsKey(filmId)) {
            throw new NoSuchElementException("Фильм с ID " + filmId + " не найден");
        }

        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (!likes.containsKey(filmId)) {
            throw new NoSuchElementException("Фильм с ID " + filmId + " не найден");
        }

        likes.get(filmId).remove(userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return likes.entrySet().stream()
                .sorted(Map.Entry.<Integer, Set<Integer>>comparingByValue(Comparator.comparingInt(Set::size)).reversed())
                .limit(count)
                .map(entry -> films.get(entry.getKey()))
                .collect(Collectors.toList());
    }
}
