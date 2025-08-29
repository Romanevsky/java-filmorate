package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @DisplayName("Тест валидации фильма")
    @Test
    void testValidFilm() {
        Film film = new Film();
        film.setName("Новый фильм");
        film.setDescription("Краткое описание");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(120);

        assertDoesNotThrow(() -> filmController.validateFilm(film));
    }

    @DisplayName("Тест создания фильма с пустым названием")
    @Test
    void testEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Краткое описание");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @DisplayName("Тест создания фильма с слишком длинным описанием")
    @Test
    void testLongDescription() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @DisplayName("Тест создания фильма с неверной датой релиза")
    @Test
    void testInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Краткое описание");
        film.setReleaseDate(LocalDate.of(1894, 12, 28));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @DisplayName("Тест создания фильма с отрицательной продолжительностью")
    @Test
    void testNegativeDuration() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Краткое описание");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(-10);

        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }
}
