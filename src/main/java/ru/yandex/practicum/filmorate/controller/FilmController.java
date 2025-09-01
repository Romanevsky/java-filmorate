package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

/**
 * Контроллер для работы с фильмами.
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable @Positive Long id) {  // <-- Добавлено: @Positive
        return filmService.findById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long userId
    ) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long userId
    ) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> popularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.popularFilms(count);
    }
}
