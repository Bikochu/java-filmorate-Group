package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmDbTest {
    private final FilmDbStorage filmStorage;

    private final UserDbStorage userStorage;

    @Order(1)
    @Test
    void addFilm() {
        Film film = new Film(1, "blablacar", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        filmStorage.addFilm(film);
        Film filmCheck = filmStorage.findFilmById(1);
        assertTrue(filmCheck.getId() == 1);
    }

    @Order(2)
    @Test
    void updateFilm() {
        Film updFilm = new Film(1, "blablacar", "не ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        filmStorage.updateFilm(updFilm);
        Film filmCheck = filmStorage.findFilmById(1);
        assertTrue(filmCheck.getDescription().equals("не ужасы"));
    }

    @Order(3)
    @Test
    void findFilmById() {
        Film filmCheck = filmStorage.findFilmById(1);
        assertTrue(filmCheck.getId() == 1);
    }

    @Order(4)
    @Test
    void getAllFilms() {
        Film film2 = new Film(1, "bla part 2", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"));
        filmStorage.addFilm(film2);
        List<Film> films = filmStorage.getAllFilms();
        assertTrue(films.size() == 2);
    }

    @Order(5)
    @Test
    void addLike() {
        User user = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user);
        filmStorage.addLike(2, 1);
        List<Film> films = filmStorage.getTopFilms(1);
        assertTrue(films.get(0).getId() == 2);
    }

    @Order(6)
    @Test
    void getTopFilms() {
        User user2 = new User(1, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 05, 25));
        userStorage.addUser(user2);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(1, 2);
        List<Film> films = filmStorage.getTopFilms(5);
        assertTrue(films.size() == 2);
        assertTrue(films.get(0).getId() == 2);
    }

    @Order(7)
    @Test
    void getFilmsByCount() {
        List<Film> films = filmStorage.getFilmsByCount(1);
        assertTrue(films.size() == 1);
        assertTrue(films.get(0).getId() == 1);
    }

    @Order(8)
    @Test
    void deleteLike() {
        filmStorage.deleteLike(2, 1);
        filmStorage.deleteLike(2, 2);
        filmStorage.deleteLike(1, 2);
        List<Film> films = filmStorage.getTopFilms(1);
        assertTrue(films.isEmpty());
    }

    @Order(9)
    @Test
    void deleteFilmById() {
        filmStorage.deleteFilmById(1);
        List<Film> films = filmStorage.getAllFilms();
        assertTrue(films.size() == 1);
    }
}