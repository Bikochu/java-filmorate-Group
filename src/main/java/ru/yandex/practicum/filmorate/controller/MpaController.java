package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private FilmService filmService;

    @Autowired
    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        return filmService.getMpaById(id);
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        return filmService.getAllMpa();
    }
}
