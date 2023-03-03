package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/film")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private List<Film> films = new ArrayList<>();

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public void addFilm(@RequestBody Film film) {
        if (FilmValidator.validateFilm(film)) {
            films.add(film);
            log.info("Фильм " + film.getName() + " добавлен");
        }
    }

    @RequestMapping(path = "/update", method = RequestMethod.PUT)
    public void updateFilm(@RequestBody Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.add(i, film);
                log.info("Фильм " + film.getName() + " обновлен");
            }
        }
    }

    @RequestMapping(path = "/delete", method = RequestMethod.DELETE)
    public void deleteFilm(@PathVariable int filmId) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == filmId) {
                films.remove(i);
            }
        }
    }
}
