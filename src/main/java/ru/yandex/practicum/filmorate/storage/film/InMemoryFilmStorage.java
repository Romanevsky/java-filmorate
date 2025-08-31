package ru.yandex.practicum.filmorate.storage.film;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();
    private int nextId = 1;

    @Override
    public Film create(@NotNull Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        log.info("Фильм создан: {}", film);
        return film;
    }

    @Override
    public Film update(@NotNull Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.info("Фильм обновлён: {}", film);
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
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        Set<Integer> filmLikes = likes.get(filmId);
        if (filmLikes != null && filmLikes.contains(userId)) {
            filmLikes.remove(userId);
            log.info("Пользователь {} убрал лайк с фильма {}", userId, filmId);
        }
    }

    @Override
    public List<Film> getPopular(int count) {
        if (count <= 0) {
            count = 10;
        }

        return films.values().stream()
                .filter(Objects::nonNull)
                .filter(film -> film.getId() != 0)
                .sorted((f1, f2) -> {
                    int likes1 = likes.getOrDefault(f1.getId(), Collections.emptySet()).size();
                    int likes2 = likes.getOrDefault(f2.getId(), Collections.emptySet()).size();
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Integer> getLikes(int filmId) { // {{ edit_1 }}
        return likes.getOrDefault(filmId, Collections.emptySet());
    }


}
