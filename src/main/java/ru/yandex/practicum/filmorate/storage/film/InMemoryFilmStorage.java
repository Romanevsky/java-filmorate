package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.ValidationException;
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
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм не найден");
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
            throw new ValidationException("Фильм не найден");
        }

        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (likes.containsKey(filmId)) {
            likes.get(filmId).remove(userId);
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return likes.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .sorted((e1, e2) -> {
                    int sizeCompare = Integer.compare(e2.getValue().size(), e1.getValue().size());
                    if (sizeCompare != 0) return sizeCompare;
                    return Integer.compare(e1.getKey(), e2.getKey()); // по возрастанию ID
                })
                .limit(count)
                .map(entry -> films.get(entry.getKey()))
                .collect(Collectors.toList());
    }
}
