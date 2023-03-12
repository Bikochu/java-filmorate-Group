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
    FilmStorage filmStorage;
    UserStorage userStorage;

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

    public Film findFilmById(int id) {
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
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        Set<Long> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
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
        if (count == null) {
            count = 10;
        }
        Collections.sort(allFilms, (a, b) -> a.getLikes().size() - b.getLikes().size());
        return allFilms.stream().limit(count).collect(Collectors.toList());
    }
}

