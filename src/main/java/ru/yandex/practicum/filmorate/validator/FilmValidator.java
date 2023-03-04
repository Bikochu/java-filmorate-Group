package ru.yandex.practicum.filmorate.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    private final static Logger log = LoggerFactory.getLogger(FilmValidator.class);
    private static LocalDate dateRelease = LocalDate.of(1895, 12, 28);

    public static void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(dateRelease)) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Валидация не пройдена");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
