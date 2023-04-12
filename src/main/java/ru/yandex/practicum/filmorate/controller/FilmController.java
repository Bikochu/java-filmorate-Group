package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.integration.film.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void deleteFilmById(@Valid @RequestBody Film id) {
        filmService.deleteFilmById(id.getId());
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable long id) {
        return filmService.findFilmById(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getTopFilms(count);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}

