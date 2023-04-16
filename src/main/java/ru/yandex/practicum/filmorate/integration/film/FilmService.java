package ru.yandex.practicum.filmorate.integration.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        FilmValidator.validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        FilmValidator.validateFilm(film);
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

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId);
        User user = userStorage.findUserById(userId);
        if (film == null || user == null) {
            throw new NotFoundException("Фильм с Id " + filmId + " не найден или пользователь с Id " + userId + " не найден");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(long id, long userId) {
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        if (film == null || user == null) {
            throw new NotFoundException("Фильм с Id " + id + " не найден или пользователь с Id " + userId + " не найден");
        }
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> getTopFilms(Integer limit, Integer genreId, Integer year) {
        return filmStorage.getTopFilms(limit, genreId, year);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getFilmsByDirector(int id, String sortBy) {
        return filmStorage.getFilmsByDirector(id, sortBy);
    }

    public List<Film> getFilmsByQuery(String query, List<String> by) {
        return filmStorage.getFilmsByQuery(query, by);
    }
}
