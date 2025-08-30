package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    // Для хранения лайков: ключ - id фильма, значение - множество id пользователей
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        }
        return null;
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
        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        likes.computeIfPresent(filmId, (k, v) -> {
            v.remove(userId);
            return v.isEmpty() ? null : v;
        });
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(
                        getLikeCount(f2.getId()),
                        getLikeCount(f1.getId())
                ))
                .limit(count)
                .toList();
    }

    @Override
    public int getLikeCount(int filmId) {
        return likes.getOrDefault(filmId, Collections.emptySet()).size();
    }
}
