package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilmById(long id);

    Film findFilmById(long id);

    List<Film> getAllFilms();

    void addLike(long id, long userId);

    void deleteLike(long id, long userId);

    List<Film> getTopFilms(Integer count, Integer genreId, Integer year);

    List<Film> getCommonFilms(long userId, long friendId);

    List<Film> getRecommendations(long userId);

    List<Film> getFilmsByDirector(int id, String sortBy);

    List<Film> getFilmsByQuery(String query, List<String> by);
}
