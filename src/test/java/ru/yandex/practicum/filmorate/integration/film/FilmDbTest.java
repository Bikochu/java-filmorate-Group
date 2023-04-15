package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class FilmDbTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final DirectorDbStorage directorStorage;


    @Test
    void addFilm() {
        Film film = new Film(1, "blablacar1", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        filmStorage.addFilm(film);
        List<Film> films = filmStorage.getAllFilms();
        assertTrue(films.stream().anyMatch(f -> f.getName().equals("blablacar1")));
    }

    @Test
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
    void deleteFilmById() {
        Film film = new Film(1, "blablacar3", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        filmStorage.addFilm(film);
        List<Film> films = filmStorage.getAllFilms();
        films.forEach(f -> filmStorage.deleteFilmById(f.getId()));
        films = filmStorage.getAllFilms();
        assertTrue(films.isEmpty());
    }

    @Test
    void findFilmById() {
        Film film = new Film(1, "blablacar4", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        filmStorage.addFilm(film);
        Film filmCheck = filmStorage.findFilmById(1);
        assertEquals(1, filmCheck.getId());
    }

    @Test
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
    void addLike() {
        Film film1 = new Film(1, "blablacar7", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar8", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        User user = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        userStorage.addUser(user);
        filmStorage.addLike(film2.getId(), 1);
        List<Film> films = filmStorage.getTopFilms(1, null, null);
        assertEquals(2, films.get(0).getId());
    }

    @Test
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
        List<Film> filmsCheck = filmStorage.getAllFilms();
        assertTrue(filmsCheck.get(0).getLikes().isEmpty());
        assertTrue(filmsCheck.get(1).getLikes().isEmpty());
    }

    @Test
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
        List<Film> films = filmStorage.getTopFilms(5, null, null);
        assertEquals(2, films.size());
        assertEquals(2, films.get(0).getId());
    }

    @Test
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

    @Test
    void getTopFilmsByGenreAndYear() {
        Film film1 = new Film(1, "film1", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "film2", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        Film film3 = new Film(3, "film3", "ужасы", LocalDate.of(2020, 12, 27), 120, new Mpa(1, "G"), 0);
        film1.getGenres().add(new Genre(1, "Комедия"));
        film2.getGenres().add(new Genre(1, "Комедия"));
        film3.getGenres().add(new Genre(1, "Комедия"));
        User user1 = new User(1, "user@gmail.com", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));
        User user2 = new User(2, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 5, 25));
        User user3 = new User(3, "vany@mail.ru", "vany", "Иван Пушхов", LocalDate.of(2000, 5, 25));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);
        filmStorage.addLike(1, 3);
        filmStorage.addLike(2, 1);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(3, 2);
        filmStorage.addLike(3, 3);
        List<Film> topFilms = filmStorage.getTopFilms(10, 1, 2022);
        assertEquals(2, topFilms.size());
        assertEquals(topFilms.get(0).getName(), film1.getName());
        assertEquals(topFilms.get(1).getName(), film2.getName());
        topFilms = filmStorage.getTopFilms(10, 1, 2020);
        assertEquals(1, topFilms.size());
        topFilms = filmStorage.getTopFilms(10, 3, 2020);
        assertEquals(0, topFilms.size());
        topFilms = filmStorage.getTopFilms(10, 1, 2021);
        assertEquals(0, topFilms.size());
    }

    @Test
    void getTopFilmsByGenre() {
        Film film1 = new Film(1, "film1", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "film2", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        Film film3 = new Film(3, "film3", "ужасы", LocalDate.of(2020, 12, 27), 120, new Mpa(1, "G"), 0);
        film1.getGenres().add(new Genre(1, "Комедия"));
        film2.getGenres().add(new Genre(1, "Комедия"));
        film3.getGenres().add(new Genre(1, "Комедия"));
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        List<Film> films = filmStorage.getTopFilms(10, 1, null);
        assertEquals(3, films.size());
        film3.getGenres().clear();
        filmStorage.updateFilm(film3);
        films = filmStorage.getTopFilms(10, 1, null);
        assertEquals(2, films.size());
    }

    @Test
    void getTopFilmsByYear() {
        Film film1 = new Film(1, "film1", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "film2", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        Film film3 = new Film(3, "film3", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        List<Film> films = filmStorage.getTopFilms(10, null, 2022);
        assertEquals(3, films.size());
        film3.setReleaseDate(LocalDate.of(2000, 12, 12));
        filmStorage.updateFilm(film3);
        films = filmStorage.getTopFilms(10, null, 2022);
        assertEquals(2, films.size());
    }

    @Test
    void getRecommendationsIfNoLikes() {
        assertEquals(0, filmStorage.getRecommendations(1).size());
    }

    @Test
    void getRecommendationsIfNoSimilarLikes() {
        Film film1 = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar14", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        Film film3 = new Film(3, "The third", "Sci-Fi", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        userStorage.addUser(user1);

        filmStorage.addLike(3, 1);

        assertEquals(0, filmStorage.getRecommendations(1).size());
    }

    @Test
    void getRecommendationsIfSimilarLikesButNothingToRecommend() {
        Film film1 = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar14", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        Film film3 = new Film(3, "The third", "Sci-Fi", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));
        User user2 = new User(2, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 5, 25));

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        filmStorage.addLike(3, 1);
        filmStorage.addLike(3, 2);

        assertEquals(0, filmStorage.getRecommendations(1).size());
    }

    @Test
    void getRecommendationsIfSimilarLikes() {
        Film film1 = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar14", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        Film film3 = new Film(3, "The third", "Sci-Fi", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));
        User user2 = new User(2, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 5, 25));

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        filmStorage.addLike(3, 1);
        filmStorage.addLike(3, 2);
        filmStorage.addLike(2, 2);

        List<Film> recommended = filmStorage.getRecommendations(1);
        assertEquals(1, recommended.size());
        assertEquals(2, recommended.get(0).getId());
    }

    @Test
    void getRecommendationsIfSimilarLikesButNothingToRecommendForUserId2() {
        Film film1 = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar14", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        Film film3 = new Film(3, "The third", "Sci-Fi", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));
        User user2 = new User(2, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 5, 25));

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        filmStorage.addLike(3, 1);
        filmStorage.addLike(3, 2);
        filmStorage.addLike(2, 2);

        List<Film> recommended = filmStorage.getRecommendations(2);
        assertEquals(0, recommended.size());
    }

    @Test
    void getRecommendationsIfSimilarLikesForUserId2() {
        Film film1 = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar14", "ужасы", LocalDate.of(2022, 12, 27), 120, new Mpa(1, "G"), 0);
        Film film3 = new Film(3, "The third", "Sci-Fi", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));
        User user2 = new User(2, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 5, 25));

        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
        filmStorage.addFilm(film3);
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        filmStorage.addLike(3, 1);
        filmStorage.addLike(3, 2);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(1, 1);

        List<Film> recommended = filmStorage.getRecommendations(2);
        assertEquals(1, recommended.size());
        assertEquals(1, recommended.get(0).getId());
    }

    @Test
    void addFilmWithDirector() {
        Director director = directorStorage.addDirector(new Director(1, "Steven Spielberg"));

        Film film = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        film.getDirectors().add(director);
        film = filmStorage.addFilm(film);

        assertEquals(film, filmStorage.findFilmById(film.getId()));
        assertEquals(director, filmStorage.findFilmById(film.getId()).getDirectors().get(0));
    }

    @Test
    void updateFilmWithDirector() {
        Director director = directorStorage.addDirector(new Director(1, "Steven Spielberg"));
        Director director2 = directorStorage.addDirector(new Director(1, "Christopher Nolan"));
        Film film = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        film.getDirectors().add(director);
        film = filmStorage.addFilm(film);

        assertEquals(director, filmStorage.findFilmById(film.getId()).getDirectors().get(0));

        film.getDirectors().clear();
        film = filmStorage.updateFilm(film);
        assertEquals(0, film.getDirectors().size());

        film.getDirectors().add(director2);
        filmStorage.updateFilm(film);
        assertEquals(director2, filmStorage.findFilmById(film.getId()).getDirectors().get(0));
    }

    @Test
    public void getFilmsByDirector_SortedByYear() {
        Film film1 = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar14", "ужасы", LocalDate.of(2020, 12, 27), 120, new Mpa(1, "G"), 0);
        Director director = directorStorage.addDirector(new Director(1, "Steven Spielberg"));
        film1.getDirectors().add(director);
        filmStorage.addFilm(film1);
        film2.getDirectors().add(director);
        filmStorage.addFilm(film2);

        int directorId = 1;
        String sortBy = "year";

        List<Film> films = filmStorage.getFilmsByDirector(directorId, sortBy);

        assertEquals(2, films.size(), "Не верное количество фильмов.");
        assertEquals(2, films.get(0).getId(), "Неверный ID фильма");
    }

    @Test
    public void getFilmsByDirector_SortedByLikes() {
        Film film1 = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Film film2 = new Film(2, "blablacar14", "ужасы", LocalDate.of(2020, 12, 27), 120, new Mpa(1, "G"), 0);
        User user1 = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 5, 25));
        User user2 = new User(2, "zina@mail.ru", "zina", "Зина Сидорова", LocalDate.of(2000, 5, 25));
        Director director = directorStorage.addDirector(new Director(1, "Steven Spielberg"));
        film1.getDirectors().add(director);
        filmStorage.addFilm(film1);
        film2.getDirectors().add(director);
        filmStorage.addFilm(film2);
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        filmStorage.addLike(2, 1);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(1, 1);
        int directorId = 1;
        String sortBy = "likes";
        List<Film> films = filmStorage.getFilmsByDirector(directorId, sortBy);
        assertEquals(2, films.size(), "Не верное количество фильмов.");
        assertEquals(2, films.get(0).getId(), "Неверный ID фильма");
    }

    @Test
    public void getFilmsByDirector_throwsIllegalArgumentException() {
        Film film1 = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Director director = directorStorage.addDirector(new Director(1, "Steven Spielberg"));
        film1.getDirectors().add(director);
        filmStorage.addFilm(film1);
        int directorId = 1;
        String sortBy = "invalid";
        try {
            List<Film> films = filmStorage.getFilmsByDirector(directorId, sortBy);
            assert false : "Ожидалось выбрасывание исключения IllegalArgumentException.";
        } catch (IllegalArgumentException e) {
            assert true;
        }
    }

    @Test
    public void testGetFilmsByDirector_withInvalidDirectorId_throwsNotFoundException() {
        Film film1 = new Film(1, "blablacar13", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"), 0);
        Director director = directorStorage.addDirector(new Director(1, "Steven Spielberg"));
        film1.getDirectors().add(director);
        filmStorage.addFilm(film1);
        int directorId = 10;
        String sortBy = "year";
        try {
            List<Film> films = filmStorage.getFilmsByDirector(directorId, sortBy);
            assert false : "Ожидалось выбрасывание исключения NotFoundException.";
        } catch (NotFoundException e) {
            assert true;
        }
    }
}