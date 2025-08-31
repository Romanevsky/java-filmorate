package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private final FilmStorage filmStorage = new InMemoryFilmStorage();
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage);
    private final FilmService filmService = new FilmService(filmStorage);
    private final FilmController filmController = new FilmController(filmService);

    @DisplayName("Тест создания фильма")
    @Test
    void testValidFilm() {
        Film film = new Film();
        film.setName("Новый фильм");
        film.setDescription("Краткое описание");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(120);

        assertDoesNotThrow(() -> filmController.createFilm(film));
    }

    @DisplayName("Тест добавления лайка")
    @Test
    void testAddLike() {
        // Создаем пользователя
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Тестовый пользователь");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userService.create(user);

        // Создаем фильм
        Film film = new Film();
        film.setName("Любимый фильм");
        film.setDescription("Отличный фильм");
        film.setReleaseDate(LocalDate.of(1980, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmService.create(film);

        // Добавляем лайк
        assertDoesNotThrow(() -> filmController.addLike(createdFilm.getId(), createdUser.getId()));

        // Проверяем, что лайк был добавлен
        assertNotNull(filmStorage.getLikes(createdFilm.getId()));
        assertTrue(filmStorage.getLikes(createdFilm.getId()).contains(createdUser.getId()));
    }

    @DisplayName("Тест удаления лайка")
    @Test
    void testRemoveLike() {
        // Создаем пользователя
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Тестовый пользователь");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userService.create(user);

        // Создаем фильм
        Film film = new Film();
        film.setName("Любимый фильм");
        film.setDescription("Отличный фильм");
        film.setReleaseDate(LocalDate.of(1980, 1, 1));
        film.setDuration(120);

        Film createdFilm = filmService.create(film);

        // Добавляем и удаляем лайк
        assertDoesNotThrow(() -> filmController.addLike(createdFilm.getId(), createdUser.getId()));
        assertDoesNotThrow(() -> filmController.removeLike(createdFilm.getId(), createdUser.getId()));

        // Проверяем, что лайк был удален
        assertNotNull(filmStorage.getLikes(createdFilm.getId()));
        assertFalse(filmStorage.getLikes(createdFilm.getId()).contains(createdUser.getId()));
    }

    @DisplayName("Тест получения популярных фильмов")
    @Test
    void testGetPopularFilms() {
        // Создаем пользователей
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("Пользователь 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("Пользователь 2");
        user2.setBirthday(LocalDate.of(1990, 1, 1));

        User user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setLogin("user3");
        user3.setName("Пользователь 3");
        user3.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser1 = userService.create(user1);
        User createdUser2 = userService.create(user2);
        User createdUser3 = userService.create(user3);

        // Создаем фильмы
        Film film1 = new Film();
        film1.setName("Фильм 1");
        film1.setDescription("Описание 1");
        film1.setReleaseDate(LocalDate.of(1980, 1, 1));
        film1.setDuration(120);

        Film film2 = new Film();
        film2.setName("Фильм 2");
        film2.setDescription("Описание 2");
        film2.setReleaseDate(LocalDate.of(1980, 1, 1));
        film2.setDuration(120);

        Film film3 = new Film();
        film3.setName("Фильм 3");
        film3.setDescription("Описание 3");
        film3.setReleaseDate(LocalDate.of(1980, 1, 1));
        film3.setDuration(120);

        Film createdFilm1 = filmService.create(film1);
        Film createdFilm2 = filmService.create(film2);
        Film createdFilm3 = filmService.create(film3);

        // Добавляем лайки
        assertDoesNotThrow(() -> filmController.addLike(createdFilm1.getId(), createdUser1.getId()));
        assertDoesNotThrow(() -> filmController.addLike(createdFilm1.getId(), createdUser2.getId()));
        assertDoesNotThrow(() -> filmController.addLike(createdFilm2.getId(), createdUser1.getId()));
        assertDoesNotThrow(() -> filmController.addLike(createdFilm3.getId(), createdUser1.getId()));
        assertDoesNotThrow(() -> filmController.addLike(createdFilm3.getId(), createdUser2.getId()));
        assertDoesNotThrow(() -> filmController.addLike(createdFilm3.getId(), createdUser3.getId()));

        // Получаем популярные фильмы
        List<Film> popularFilms = filmService.getPopular(3);

        // Проверяем порядок
        assertEquals(3, popularFilms.size());
        assertEquals(createdFilm3.getId(), popularFilms.get(0).getId());
        assertEquals(createdFilm1.getId(), popularFilms.get(1).getId());
        assertEquals(createdFilm2.getId(), popularFilms.get(2).getId());
    }
}
