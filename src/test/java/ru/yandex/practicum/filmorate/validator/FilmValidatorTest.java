package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorTest {

    @Test
    void shouldNotValidateEmptyNameFilm() {
        Film film = new Film(1, "", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldValidateFilm() {
        Film film = new Film(1, "blablacar", "ужасы", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateDescription201Symbols() {
        Film film = new Film(1, "bla", "Фильмов много — и с каждым годом становится всё больше. Чем их больше, тем больше разных оценок. Чем больше оценок, тем сложнее сделать выбор. Однако не время сдаваться! Вы .111111111111111111111111111", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldValidateDescription200Symbols() {
        Film film = new Film(1, "bla", "Фильмов много — и с каждым годом становится всё больше. Чем их больше, тем больше разных оценок. Чем больше оценок, тем сложнее сделать выбор. Однако не время сдаваться! Вы .11111111111111111111111111", LocalDate.of(2022, 12, 15), 120, new Mpa(1, "G"));
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateDateBefore18951228() {
        Film film = new Film(1, "bla", "ужасы", LocalDate.of(1895, 12, 27), 120, new Mpa(1, "G"));
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldValidateDate18951228() {
        Film film = new Film(1, "blablacar", "ужасы", LocalDate.of(1895, 12, 28), 120, new Mpa(1, "G"));
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateDurationMinus1() {
        Film film = new Film(1, "bla", "ужасы", LocalDate.of(2022, 12, 27), -1, new Mpa(1, "G"));
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateDurationZero() {
        Film film = new Film(1, "bla", "ужасы", LocalDate.of(2022, 12, 28), 0, new Mpa(1, "G"));
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldValidateDurationPlus1() {
        Film film = new Film(1, "blablacar", "ужасы", LocalDate.of(2022, 12, 28), 1, new Mpa(2, "PG"));
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateNameNull() {
        Film film = new Film(1, null, "ужасы", LocalDate.of(2022, 12, 28), 1, new Mpa(1, "G"));
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateDescriptionNull() {
        Film film = new Film(1, "bla", null, LocalDate.of(2022, 12, 28), 1, new Mpa(1, "G"));
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidateReleaseDateNull() {
        Film film = new Film(1, "bla", "ужасы", null, 1, new Mpa(1, "G"));
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(film));
    }

    @Test
    void shouldNotValidatePassedToMethodNull() {
        Film film = new Film(1, "bla", "ужасы", LocalDate.of(2022, 12, 28), 1, new Mpa(1, "G"));
        assertThrows(ValidationException.class, () -> FilmValidator.validateFilm(null));
    }
}