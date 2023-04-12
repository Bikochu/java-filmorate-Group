package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Test
    @DirtiesContext
    void addFilm() {
        Film film = new Film(1, "blablacar1", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        filmStorage.addFilm(film);
        List<Film> films = filmStorage.getAllFilms();
        assertTrue(films.stream().anyMatch(f -> f.getName().equals("blablacar1")));
    }

    @Test
    @DirtiesContext
    void updateFilm() {
        Film film = new Film(1, "blablacar2", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film updFilm = new Film(1, "blablacar2", "не ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        filmStorage.addFilm(film);
        List<Film> films = filmStorage.getAllFilms();
        List<Long> id = films.stream()
                .filter(f -> f.getName().equals("blablacar2"))
                .map(Film::getId)
                        .collect(Collectors.toList());
        updFilm.setId(id.get(0));
        filmStorage.updateFilm(updFilm);
        Film filmCheck = filmStorage.findFilmById(id.get(0));
        assertEquals("не ужасы", filmCheck.getDescription());
    }

    @Test
    @DirtiesContext
    void deleteFilmById() {
        Film film = new Film(1, "blablacar3", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        filmStorage.addFilm(film);
        List<Film> films = filmStorage.getAllFilms();
        films.forEach(f -> filmStorage.deleteFilmById(f.getId()));
        films = filmStorage.getAllFilms();
        assertTrue(films.isEmpty());
    }

    @Test
    @DirtiesContext
    void findFilmById() {
        Film film = new Film(1, "blablacar4", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        filmStorage.addFilm(film);
        Film filmCheck = filmStorage.findFilmById(1);
        assertEquals(1, filmCheck.getId());
    }

    @Test
    @DirtiesContext
    void getAllFilms() {
        Film film1 = new Film(1, "blablacar5", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar6", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        List<Film> filmsCheckBefore = filmStorage.getAllFilms();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        List<Film> filmsCheckAfter = filmStorage.getAllFilms();
        assertTrue(filmsCheckBefore.size() < filmsCheckAfter.size());
        assertEquals(2, (filmsCheckAfter.size() - filmsCheckBefore.size()));
    }

    @Test
    @DirtiesContext
    void addLike() {
        Film film1 = new Film(1, "blablacar7", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar8", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        User user = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user);
        filmStorage.addLike(film2.getId(), 1);
        List<Film> films = filmStorage.getTopFilms(1);
        assertEquals(2, films.get(0).getId());
    }

    @Test
    @DirtiesContext
    void deleteLike() {
        Film film1 = new Film(1, "blablacar9", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar10", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));
        User user2 = new User(2, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 5, 25));
        List<Film> filmsDrop = filmStorage.getAllFilms();
        filmsDrop.forEach(f -> filmStorage.deleteFilmById(f.getId()));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        List<Film> filmsUp = filmStorage.getAllFilms();
        filmStorage.addLike(filmsUp.get(0).getId(), 2);
        filmStorage.addLike(filmsUp.get(1).getId(), 1);
        filmStorage.deleteLike(filmsUp.get(1).getId(), 1);
        filmStorage.deleteLike(filmsUp.get(0).getId(), 2);
        List<Film> films = filmStorage.getTopFilms(1);
        assertTrue(films.isEmpty());
    }

    @Test
    @DirtiesContext
    void getTopFilms() {
        Film film1 = new Film(1, "blablacar11", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar12", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));
        User user2 = new User(2, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 5, 25));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        filmStorage.addLike(film2.getId(), 2);
        filmStorage.addLike(film1.getId(), 2);
        filmStorage.addLike(film2.getId(), 1);
        List<Film> films = filmStorage.getTopFilms(5);
        assertEquals(2, films.size());
        assertEquals(2, films.get(0).getId());
    }

    @Test
    @DirtiesContext
    void getFilmsByCount() {
        Film film1 = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar14", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));
        User user2 = new User(2, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 5, 25));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        filmStorage.addLike(film2.getId(), 2);
        filmStorage.addLike(film1.getId(), 2);
        filmStorage.addLike(film2.getId(), 1);
        List<Film> films = filmStorage.getFilmsByCount(1);
        assertEquals(1, films.size());
        assertEquals(1, films.get(0).getId());
    }

    @Test
    @DirtiesContext
    void getCommonFilms() {
        Film film1 = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar14", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));
        User user2 = new User(2, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 5, 25));
        User user3 = new User(3, "vany@mail.ru", "vany", "Иван Пушхов", LocalDate.of(2000, 5, 25));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        System.out.println(filmStorage.getAllFilms());
        System.out.println(userStorage.getAllUsers());
        filmStorage.addLike(film1.getId(), 1);
        filmStorage.addLike(film1.getId(), 2);
        filmStorage.addLike(film2.getId(), 1);
        filmStorage.addLike(film2.getId(), 2);
        filmStorage.addLike(film2.getId(), 3);
        List<Film> listOfFilms = filmStorage.getCommonFilms(1, 2);
        assertEquals(film2.getId(), listOfFilms.get(0).getId(), "Не соответствует.");
        assertEquals(2, listOfFilms.size(), "Не соответствует.");
    }
}