package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static int FIRST_TEN_FILMS = 10;

    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        FilmValidator.validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilmById(long id) {
        filmStorage.deleteFilmById(id);
    }

    public Film findFilmById(long id) {
        return filmStorage.findFilmById(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(long id, long userId) {
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        if (film == null || user == null) {
            throw new NotFoundException("Фильм с Id " + id + " не найден или пользователь с Id " + userId + " не найден");
        }
        Set<Long> likes = film.getLikes();
        likes.add(userId);
    }

    public void deleteLike(long id, long userId) {
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        if (film == null || user == null) {
            throw new NotFoundException("Фильм с Id " + id + " не найден или пользователь с Id " + userId + " не найден");
        }
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
        }
    }

    public List<Film> getTopFilms(Integer count) {
        List<Film> allFilms = filmStorage.getAllFilms();
        if (count == FIRST_TEN_FILMS) {
            return allFilms.stream().limit(FIRST_TEN_FILMS).collect(Collectors.toList());
        }
        return allFilms.stream()
                .filter(f -> null != f.getLikes())
                .filter(f -> !f.getLikes().isEmpty())
                .sorted((a, b) -> b.getLikes().size() - a.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}

