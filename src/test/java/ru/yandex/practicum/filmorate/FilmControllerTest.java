package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Новый фильм");
        testFilm.setDescription("Краткое описание");
        testFilm.setReleaseDate(LocalDate.of(1900, 1, 1));
        testFilm.setDuration(120);
    }

    @Test
    @DisplayName("Тест создания фильма")
    void testCreateFilm() {
        ResponseEntity<?> response = filmController.create(testFilm);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());

        Film createdFilm = (Film) response.getBody();
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());
    }

    @Test
    @DisplayName("Тест создания фильма с пустым названием")
    void testEmptyName() {
        testFilm.setName("");

        ResponseEntity<?> response = filmController.create(testFilm);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Тест создания фильма с слишком длинным описанием")
    void testLongDescription() {
        testFilm.setDescription("A".repeat(201));

        ResponseEntity<?> response = filmController.create(testFilm);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Тест создания фильма с неверной датой релиза")
    void testInvalidReleaseDate() {
        testFilm.setReleaseDate(LocalDate.of(1894, 12, 28));

        ResponseEntity<?> response = filmController.create(testFilm);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Тест создания фильма с отрицательной продолжительностью")
    void testNegativeDuration() {
        testFilm.setDuration(-10);

        ResponseEntity<?> response = filmController.create(testFilm);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Тест добавления и удаления лайка")
    void testAddAndRemoveLike() {
        ResponseEntity<?> response = filmController.create(testFilm);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());

        Film createdFilm = (Film) response.getBody();
        assertNotNull(createdFilm);
        assertNotNull(createdFilm.getId());

        filmController.addLike(createdFilm.getId(), 1);
        assertTrue(filmController.getFilmLikes(createdFilm.getId()) > 0);

        filmController.removeLike(createdFilm.getId(), 1);
        assertEquals(0, filmController.getFilmLikes(createdFilm.getId()));
    }

    @Test
    @DisplayName("Тест получения популярных фильмов")
    void testGetPopularFilms() {
        Film film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание 1");
        film1.setReleaseDate(LocalDate.of(1990, 1, 1));
        film1.setDuration(120);

        Film film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание 2");
        film2.setReleaseDate(LocalDate.of(1995, 1, 1));
        film2.setDuration(150);

        ResponseEntity<?> response1 = filmController.create(film1);
        ResponseEntity<?> response2 = filmController.create(film2);

        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(201, response1.getStatusCodeValue());
        assertEquals(201, response2.getStatusCodeValue());

        Film createdFilm1 = (Film) response1.getBody();
        Film createdFilm2 = (Film) response2.getBody();

        filmController.addLike(createdFilm1.getId(), 1);
        filmController.addLike(createdFilm1.getId(), 2);
        filmController.addLike(createdFilm2.getId(), 1);

        List<Film> popularFilms = filmController.getPopular(2);
        assertEquals(2, popularFilms.size());
        assertEquals(createdFilm1.getId(), popularFilms.get(0).getId());
        assertEquals(createdFilm2.getId(), popularFilms.get(1).getId());
    }
}
