package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FilmControllerTest {
    private final FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage()));

    @DisplayName("Тест создания фильма")
    @Test
    void testValidFilm() {
        Film film = new Film();
        film.setName("Новый фильм");
        film.setDescription("Краткое описание");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(120);
        ResponseEntity<?> response = filmController.createFilm(film);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @DisplayName("Тест создания фильма с пустым названием")
    @Test
    void testEmptyName() {
        Film film = new Film();
        film.setName("");
        ResponseEntity<?> response = filmController.createFilm(film);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((Map<String, String>) response.getBody()).get("error"));
    }

    @DisplayName("Тест создания фильма с слишком длинным описанием")
    @Test
    void testLongDescription() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("A".repeat(201));
        ResponseEntity<?> response = filmController.createFilm(film);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((Map<String, String>) response.getBody()).get("error"));
    }

    @DisplayName("Тест создания фильма с неверной датой релиза")
    @Test
    void testInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.of(1894, 12, 28));
        ResponseEntity<?> response = filmController.createFilm(film);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((Map<String, String>) response.getBody()).get("error"));
    }

    @DisplayName("Тест создания фильма с отрицательной продолжительностью")
    @Test
    void testNegativeDuration() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDuration(-10);
        ResponseEntity<?> response = filmController.createFilm(film);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((Map<String, String>) response.getBody()).get("error"));
    }
}
