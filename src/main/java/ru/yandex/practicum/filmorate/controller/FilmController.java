package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для работы с фильмами.
 */
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    /**
     * Создание нового фильма.
     *
     * @param film объект фильма
     * @return ResponseEntity<Film> или ResponseEntity<Error>
     */
    @PostMapping
    public ResponseEntity<?> createFilm(@Valid @RequestBody Film film) {
        try {
            validateFilm(film);
            film.setId(nextId++);
            films.put(film.getId(), film);
            log.info("Фильм создан: {}", film);
            return ResponseEntity.status(HttpStatus.CREATED).body(film);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Обновление фильма.
     *
     * @param updatedFilm обновлённый фильм
     * @return ResponseEntity<Film> или ResponseEntity<Error>
     */
    @PutMapping
    public ResponseEntity<?> updateFilm(@Valid @RequestBody Film updatedFilm) {
        try {
            validateFilm(updatedFilm);
            if (films.containsKey(updatedFilm.getId())) {
                films.put(updatedFilm.getId(), updatedFilm);
                log.info("Фильм обновлён: {}", updatedFilm);
                return ResponseEntity.ok(updatedFilm);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Фильм не найден"));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Получение всех фильмов.
     *
     * @return список всех фильмов
     */
    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    /**
     * Валидация данных фильма.
     *
     * @param film объект фильма
     * @throws ValidationException если данные невалидны
     */
    public void validateFilm(Film film) {
        if (film.getName() == null || film.getName().trim().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не должно превышать 200 символов.");
        }

        LocalDate earliestDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(earliestDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }
}
