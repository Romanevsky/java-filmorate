package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private final FilmController filmController = new FilmController();

    @Test
    void testValidFilm() {
        Film film = new Film();
        film.setName("Новый фильм");
        film.setDescription("Краткое описание");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(120);

        assertDoesNotThrow(() -> filmController.validateFilm(film));
    }

    @Test
    void testEmptyName() {
        Film film = new Film();
        film.setName("");

        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @Test
    void testLongDescription() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("A".repeat(201));

        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @Test
    void testInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Фильм");
        film.setReleaseDate(LocalDate.of(1894, 12, 28));

        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @Test
    void testNegativeDuration() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDuration(-10);

        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }
}
