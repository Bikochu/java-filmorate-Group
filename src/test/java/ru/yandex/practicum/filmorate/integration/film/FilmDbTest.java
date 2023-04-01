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
class FilmDbTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

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
        Film film = new Film(1, "blablacar", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        Film updFilm = new Film(1, "blablacar", "не ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        filmStorage.addFilm(film);
        filmStorage.updateFilm(updFilm);
        Film filmCheck = filmStorage.findFilmById(1);
        assertTrue(filmCheck.getDescription().equals("не ужасы"));
    }

    @Test
    void findFilmById() {
        Film film = new Film(1, "blablacar", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        filmStorage.addFilm(film);
        Film filmCheck = filmStorage.findFilmById(1);
        assertTrue(filmCheck.getId() == 1);
    }

    @Test
    void getAllFilms() {
        Film film1 = new Film(1, "blablacar", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        Film film2 = new Film(1, "bla part 2", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        List<Film> films = filmStorage.getAllFilms();
        assertTrue(films.size() == 2);
    }

    @Test
    void addLike() {
        Film film1 = new Film(1, "blablacar", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        Film film2 = new Film(1, "bla part 2", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"));
        User user = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user);
        filmStorage.addLike(2, 1);
        List<Film> films = filmStorage.getTopFilms(1);
        assertTrue(films.get(0).getId() == 2);
    }

    @Test
    void getTopFilms() {
        Film film1 = new Film(1, "blablacar", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        Film film2 = new Film(1, "bla part 2", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"));
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User user2 = new User(1, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 05, 25));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(1, 2);
        filmStorage.addLike(2, 1);
        List<Film> films = filmStorage.getTopFilms(5);
        assertTrue(films.size() == 2);
        assertTrue(films.get(0).getId() == 2);
    }

    @Test
    void getFilmsByCount() {
        Film film1 = new Film(1, "blablacar", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        Film film2 = new Film(1, "bla part 2", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"));
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User user2 = new User(1, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 05, 25));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(1, 2);
        filmStorage.addLike(2, 1);
        List<Film> films = filmStorage.getFilmsByCount(1);
        assertTrue(films.size() == 1);
        assertTrue(films.get(0).getId() == 1);
    }

    @Test
    void deleteLike() {
        Film film1 = new Film(1, "blablacar", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        Film film2 = new Film(1, "bla part 2", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"));
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User user2 = new User(1, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 05, 25));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(1, 2);
        filmStorage.addLike(2, 1);
        filmStorage.deleteLike(2, 1);
        filmStorage.deleteLike(2, 2);
        filmStorage.deleteLike(1, 2);
        List<Film> films = filmStorage.getTopFilms(1);
        assertTrue(films.isEmpty());
    }

    @Test
    void deleteFilmById() {
        Film film = new Film(1, "blablacar", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        filmStorage.addFilm(film);
        filmStorage.deleteFilmById(1);
        List<Film> films = filmStorage.getAllFilms();
        assertTrue(films.isEmpty());
    }
}