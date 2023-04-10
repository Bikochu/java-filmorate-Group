package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private static long id = 0;
    private final Map<Long, Film> films = new HashMap();

    @Override
    public Film addFilm(Film film) {
        film.setId(++id);
        films.put(id, film);
        log.info("Фильм " + film.getName() + " добавлен");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с Id " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        log.info("Фильм " + film.getName() + " обновлен");
        return film;
    }

    @Override
    public void deleteFilmById(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Пользователь с Id " + id + " не найден");
        }
        films.remove(id);
    }

    @Override
    public Film findFilmById(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с Id " + id + " не найден");
        }
        return films.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getFilmsByCount(int count) {
        return null;
    }

    @Override
    public void addLike(long id, long userId) {

    }

    @Override
    public void deleteLike(long id, long userId) {

    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        return null;
    }
}
