package ru.yandex.practicum.filmorate.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    private final static Logger log = LoggerFactory.getLogger(FilmValidator.class);
    private static LocalDate dateRelease = LocalDate.of(1895,12, 28);

    public static boolean validateFilm(Film film) {
        try {
            if (film.getName().isBlank()) {
                throw new ValidationException("Название не может быть пустым");
            }
            if (film.getDescription().length() > 200) {
                throw new ValidationException("Максимальная длина описания — 200 символов");
            }
            if (film.getReleaseDate().isBefore(dateRelease)) {
                throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
            }
            if (film.getDuration() <= 0) {
                throw new ValidationException("Продолжительность фильма должна быть положительной");
            }
        } catch (ValidationException e) {
            log.warn("Валидация не пройдена", e);
            return false;
        }
        return true;
    }
}
