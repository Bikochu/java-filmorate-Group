package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilmById(long id);

    Film findFilmById(long id);

    List<Film> getAllFilms();

    List<Film> getFilmsByCount(int count);

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    List<Film> getTopFilms(Integer count);
}
