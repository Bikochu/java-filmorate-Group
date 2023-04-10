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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Test
    void addFilm() {
        Film film = new Film(1, "blablacar1", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        filmStorage.addFilm(film);
        List<Film> films = filmStorage.getAllFilms();
        assertTrue(films.stream().anyMatch(f -> f.getName().equals("blablacar1")));
    }

    @Test
    void updateFilm() {
        Film film = new Film(1, "blablacar2", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        Film updFilm = new Film(1, "blablacar2", "не ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        filmStorage.addFilm(film);
        List<Film> films = filmStorage.getAllFilms();
        List<Long> id = films.stream()
                .filter(f -> f.getName().equals("blablacar2"))
                .map(Film::getId)
                        .collect(Collectors.toList());
        updFilm.setId(id.get(0));
        filmStorage.updateFilm(updFilm);
        Film filmCheck = filmStorage.findFilmById(id.get(0));
        assertTrue(filmCheck.getDescription().equals("не ужасы"));
    }

    @Test
    void deleteFilmById() {
        Film film = new Film(1, "blablacar3", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        filmStorage.addFilm(film);
        List<Film> films = filmStorage.getAllFilms();
        films.forEach(f -> filmStorage.deleteFilmById(f.getId()));
        films = filmStorage.getAllFilms();
        assertTrue(films.isEmpty());
    }

    @Test
    void findFilmById() {
        Film film = new Film(1, "blablacar4", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        filmStorage.addFilm(film);
        Film filmCheck = filmStorage.findFilmById(1);
        assertTrue(filmCheck.getId() == 1);
    }

    @Test
    void getAllFilms() {
        Film film1 = new Film(1, "blablacar5", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        Film film2 = new Film(1, "blablacar6", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"));
        List<Film> filmsCheckBefore = filmStorage.getAllFilms();
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        List<Film> filmsCheckAfter = filmStorage.getAllFilms();
        assertTrue(filmsCheckBefore.size() < filmsCheckAfter.size());
        assertTrue((filmsCheckAfter.size() - filmsCheckBefore.size()) == 2);
    }

    @Test
    void addLike() {
        Film film1 = new Film(1, "blablacar7", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        Film film2 = new Film(1, "blablacar8", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"));
        User user = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user);
        filmStorage.addLike(2, 1);
        List<Film> films = filmStorage.getTopFilms(1);
        assertTrue(films.get(0).getId() == 2);
    }

    @Test
    void deleteLike() {
        Film film1 = new Film(1, "blablacar9", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        Film film2 = new Film(1, "blablacar10", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"));
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 05, 25));
        User user2 = new User(1, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 05, 25));
        List<Film> filmsDrop = filmStorage.getAllFilms();
        filmsDrop.forEach(f -> filmStorage.deleteFilmById(f.getId()));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        List<Film> filmsUp = filmStorage.getAllFilms();

        filmStorage.addLike(filmsUp.get(0).getId(), 2);
        filmStorage.addLike(filmsUp.get(0).getId(), 2);
        filmStorage.addLike(filmsUp.get(1).getId(), 1);
        filmStorage.deleteLike(filmsUp.get(1).getId(), 1);
        filmStorage.deleteLike(filmsUp.get(1).getId(), 2);
        filmStorage.deleteLike(filmsUp.get(0).getId(), 2);
        List<Film> films = filmStorage.getTopFilms(1);
        assertTrue(films.isEmpty());
    }

    @Test
    void getTopFilms() {
        Film film1 = new Film(1, "blablacar11", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        Film film2 = new Film(1, "blablacar12", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"));
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
        Film film1 = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        Film film2 = new Film(1, "blablacar14", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"));
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
}