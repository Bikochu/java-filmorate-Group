package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    @Test
    void shouldNotValidateEmptyEmail() {
        User user = new User(1, "", "goshan", "Григорий Петров", LocalDate.of(2000, 05,25));
        Assertions.assertFalse(UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateEmailNotContainDogSymbol() {
        User user = new User(1, "goshamail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05,25));
        Assertions.assertFalse(UserValidator.validateUser(user));
    }

    @Test
    void shouldValidateEmail() {
        User user = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05,25));
        Assertions.assertTrue(UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateEmptyLogin() {
        User user = new User(1, "gosha@mail.ru", "", "Григорий Петров", LocalDate.of(2000, 05,25));
        Assertions.assertFalse(UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateLoginWithWhitespace() {
        User user = new User(1, "goshamail.ru", "go shan", "Григорий Петров", LocalDate.of(2000, 05,25));
        Assertions.assertFalse(UserValidator.validateUser(user));
    }

    @Test
    void shouldValidateLogin() {
        User user = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05,25));
        Assertions.assertTrue(UserValidator.validateUser(user));
    }

    @Test
    void shouldValidateEmptyName() {
        User user = new User(1, "gosha@mail.ru", "goshan", "", LocalDate.of(2000, 05,25));
        Assertions.assertTrue(UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateBirthdayInFuture() {
        User user = new User(1, "gosha@mail.ru", "", "Григорий Петров", LocalDate.of(2035, 05,25));
        Assertions.assertFalse(UserValidator.validateUser(user));
    }

    @Test
    void shouldNotValidateBirthdayInToday() {
        User user = new User(1, "goshamail.ru", "go shan", "Григорий Петров", LocalDate.now());
        Assertions.assertFalse(UserValidator.validateUser(user));
    }

    @Test
    void shouldValidateBirthdayInPast() {
        User user = new User(1, "gosha@mail.ru", "goshan", "Григорий Петров", LocalDate.of(2000, 05,25));
        Assertions.assertTrue(UserValidator.validateUser(user));
    }
}