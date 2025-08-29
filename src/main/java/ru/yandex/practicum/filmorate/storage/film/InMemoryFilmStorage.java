package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();
    private int nextId = 1;

    @Override
    public Film create(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        likes.putIfAbsent(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NoSuchElementException("Фильм не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getById(int id) {
        return Optional.ofNullable(films.get(id));
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
        if (!films.containsKey(filmId)) {
            throw new NoSuchElementException("Фильм с ID " + filmId + " не найден");
        }
        likes.getOrDefault(filmId, new HashSet<>()).remove(userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> {
                    int likes1 = likes.getOrDefault(f1.getId(), new HashSet<>()).size();
                    int likes2 = likes.getOrDefault(f2.getId(), new HashSet<>()).size();
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .collect(java.util.stream.Collectors.toList());
    }
}
