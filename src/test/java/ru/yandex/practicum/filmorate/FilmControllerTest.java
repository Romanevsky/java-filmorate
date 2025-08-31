package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class FilmControllerTest {

    private FilmService filmServiceMock;
    private UserService userServiceMock;
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmServiceMock = Mockito.mock(FilmService.class);
        userServiceMock = Mockito.mock(UserService.class);
        filmController = new FilmController(filmServiceMock, userServiceMock);
    }

    @Test
    void testCreateFilmSuccess() {
        Film film = new Film();
        film.setId(1);
        film.setName("Новый фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(120);

        when(filmServiceMock.createFilm(film)).thenReturn(film);

        ResponseEntity<?> response = filmController.createFilm(film);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(film, ((ResponseEntity<Film>) response).getBody());
    }

    @Test
    void testCreateFilmValidationFailure() {
        Film film = new Film();
        film.setId(1);
        film.setName(""); // Некорректное имя
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(120);

        when(filmServiceMock.createFilm(film)).thenThrow(new ValidationException("Ошибка валидации"));

        ResponseEntity<?> response = filmController.createFilm(film);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((Map<String, String>) ((ResponseEntity<Map<String, String>>) response).getBody()).get("error"));
    }

    @Test
    void testUpdateFilmSuccess() {
        Film film = new Film();
        film.setId(1);
        film.setName("Обновлённый фильм");
        film.setDescription("Новое описание");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(120);

        when(filmServiceMock.updateFilm(film)).thenReturn(film);

        ResponseEntity<?> response = filmController.updateFilm(film);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(film, ((ResponseEntity<Film>) response).getBody());
    }

    @Test
    void testUpdateFilmValidationFailure() {
        Film film = new Film();
        film.setId(1);
        film.setName(""); // Некорректное имя
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(120);

        when(filmServiceMock.updateFilm(film)).thenThrow(new ValidationException("Ошибка валидации"));

        ResponseEntity<?> response = filmController.updateFilm(film);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((ResponseEntity<Map<String, String>>) response).getBody().get("error"));
    }

    @Test
    void testGetFilmByIdNotFound() {
        when(filmServiceMock.getFilmById(1)).thenThrow(new ValidationException("Фильм не найден"));

        ResponseEntity<?> response = filmController.getFilmById(1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(((Map<String, String>) ((ResponseEntity<Map<String, String>>) response).getBody()).get("error"));
    }

    @Test
    void testAddLikeSuccess() {
        User user = new User();
        user.setId(1);
        user.setEmail("user@example.com");
        user.setLogin("login");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        doNothing().when(filmServiceMock).addLike(1, 1);
        doReturn(user).when(userServiceMock).getUserById(1);

        ResponseEntity<?> response = filmController.addLike(1, 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testAddLikeUserNotFound() {
        doThrow(new ValidationException("Пользователь не найден"))
                .when(userServiceMock).getUserById(1);

        ResponseEntity<?> response = filmController.addLike(1, 1);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(((ResponseEntity<Map<String, String>>) response).getBody().get("error"));
    }

    @Test
    void testRemoveLike() {
        doNothing().when(filmServiceMock).removeLike(1, 1);

        ResponseEntity<?> response = filmController.removeLike(1, 1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
