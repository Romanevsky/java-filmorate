package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Film film) {
        try {
            Film createdFilm = filmService.create(film);
            log.info("Фильм создан: {}", createdFilm);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Film film) {
        try {
            Film updatedFilm = filmService.update(film);
            log.info("Фильм обновлён: {}", updatedFilm);
            return ResponseEntity.ok(updatedFilm);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public List<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable int id) {
        return filmService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<?> addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<?> removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count);
    }

    @GetMapping("/{id}/likes")
    public int getFilmLikes(@PathVariable int id) {
        return filmService.getLikeCount(id);
    }
}
