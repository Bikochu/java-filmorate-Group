package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    @Test
    void shouldNotValidateEmptyEmail() {
        User user = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 05,25), new HashSet<>());
        assertThrows(ValidationException.class, () -> UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateEmailNotContainDogSymbol() {
        User user = new User(1, "goshamail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05,25), new HashSet<>());
        assertThrows(ValidationException.class, () -> UserValidator.validateUser(user));
    }

    @Test
    void shouldValidateEmail() {
        User user = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05,25), new HashSet<>());
        assertDoesNotThrow(() -> UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateEmptyLogin() {
        User user = new User(1, "gosha@mail.ru", "", "Григорий Петров", LocalDate.of(2000, 05,25), new HashSet<>());
        assertThrows(ValidationException.class, () -> UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateLoginWithWhitespace() {
        User user = new User(1, "goshamail.ru", "go shan", "Григорий Петров", LocalDate.of(2000, 05,25), new HashSet<>());
        assertThrows(ValidationException.class, () -> UserValidator.validateUser(user));
    }

    @Test
    void shouldValidateLogin() {
        User user = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05,25), new HashSet<>());
        assertDoesNotThrow(() -> UserValidator.validateUser(user));
    }

    @Test
    void shouldValidateEmptyName() {
        User user = new User(1, "gosha@mail.ru", "goshan", "", LocalDate.of(2000, 05,25), new HashSet<>());
        assertDoesNotThrow(() -> UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateBirthdayInFuture() {
        User user = new User(1, "gosha@mail.ru", "", "Григорий Петров", LocalDate.of(2035, 05,25), new HashSet<>());
        assertThrows(ValidationException.class, () -> UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateBirthdayInToday() {
        User user = new User(1, "goshamail.ru", "go shan", "Григорий Петров", LocalDate.now(), new HashSet<>());
        assertThrows(ValidationException.class, () -> UserValidator.validateUser(user));
    }

    @Test
    void shouldValidateBirthdayInPast() {
        User user = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05,25), new HashSet<>());
        assertDoesNotThrow(() -> UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateNameNull() {
        User user = new User(1, "gosha@mail.ru", "goshan", null, LocalDate.of(2000, 05,25), new HashSet<>());
        assertDoesNotThrow(() -> UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateEmailNull() {
        User user = new User(1, null, "goshan", "Григорий Петров", LocalDate.of(2000, 05,25), new HashSet<>());
        assertThrows(ValidationException.class, () -> UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateLoginNull() {
        User user = new User(1, "gosha@mail.ru", null, "Григорий Петров", LocalDate.of(2000, 05,25), new HashSet<>());
        assertThrows(ValidationException.class, () -> UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateBirthdayNull() {
        User user = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", null, new HashSet<>());
        assertThrows(ValidationException.class, () -> UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidatePassedToMethodNull() {
        User user = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05,25), new HashSet<>());
        assertThrows(ValidationException.class, () -> UserValidator.validateUser(null));
    }
}