package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private static int id = 0;
    private List<Film> films = new ArrayList<>();

    @RequestMapping(method = RequestMethod.POST)
    public Film addFilm(@Valid @RequestBody Film film) {
        FilmValidator.validateFilm(film);
        film.setId(id++);
        films.add(film);
        log.info("Фильм " + film.getName() + " добавлен");

        return film;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Film updateFilm(@Valid @RequestBody Film film) {
        boolean checkFilm = films.stream().anyMatch(film1 -> film1.getId() == film.getId());
        if (checkFilm) {
            for (int i = 0; i < films.size(); i++) {
                if (films.get(i).getId() == film.getId()) {
                    films.set(i, film);
                    log.info("Фильм " + film.getName() + " обновлен");
                    continue;
                }
            }
        } else {
            throw new ValidationException("Фильм с Id " + film.getId() + " не найден");
        }
        return film;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Film> getAllFilms() {
        return films;
    }
}

