package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private List<Film> films = new ArrayList<>();

    @RequestMapping(method = RequestMethod.POST)
    public void addFilm(@Valid @RequestBody Film film) {
        if (FilmValidator.validateFilm(film)) {
            films.add(film);
            log.info("Фильм " + film.getName() + " добавлен");
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void updateFilm(@Valid @RequestBody Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.add(i, film);
                log.info("Фильм " + film.getName() + " обновлен");
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Film> getAllFilms() {
        return films;
    }
}

