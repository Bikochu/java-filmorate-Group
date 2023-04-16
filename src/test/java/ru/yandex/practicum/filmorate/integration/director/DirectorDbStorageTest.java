package ru.yandex.practicum.filmorate.integration.director;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class DirectorDbStorageTest {

    private final DirectorDbStorage directorStorage;

    @Test
    void addDirector() {
        Director director = directorStorage.addDirector(new Director(1, "Steven Spielberg"));

        assertEquals(1, directorStorage.getDirectors().size());
        assertEquals(director, directorStorage.getDirectors().get(0));
    }

    @Test
    void addDirectorWithEmptyName() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.usingContext().getValidator();

        Director director = new Director(1, " ");
        Set<ConstraintViolation<Director>> violations = validator.validate(director);

        assertEquals(1, violations.size(), "The name should not by empty");
    }

    @Test
    void addDirectorWithWrongId() {
        Director director = directorStorage.addDirector(new Director(10, "Steven Spielberg"));

        assertEquals(1, directorStorage.getDirectors().size());
        assertEquals(1, director.getId());
    }

    @Test
    void updateDirector() {
        Director director = directorStorage.addDirector(new Director(1, "Steven Spielberg"));

        director.setName("Christopher Nolan");

        directorStorage.updateDirector(director);

        assertEquals(1, directorStorage.getDirectors().size());
        assertEquals(director, directorStorage.getDirectors().get(0));
    }

    @Test
    void updateDirectorWithWrongId() {
        Director director = directorStorage.addDirector(new Director(1, "Steven Spielberg"));

        director.setId(10);

        assertThrows(NotFoundException.class, () -> directorStorage.updateDirector(director));
    }

    @Test
    void deleteDirector() {
        Director director = directorStorage.addDirector(new Director(1, "Steven Spielberg"));

        directorStorage.deleteDirector(director.getId());

        assertEquals(0, directorStorage.getDirectors().size());
    }

    @Test
    void deleteDirectorWithWrongId() {
        directorStorage.addDirector(new Director(1, "Steven Spielberg"));

        assertThrows(NotFoundException.class, () -> directorStorage.deleteDirector(10));
        assertEquals(1, directorStorage.getDirectors().size());
    }

    @Test
    void getDirectors() {
        Director director = directorStorage.addDirector(new Director(1, "Steven Spielberg"));
        Director director2 = directorStorage.addDirector(new Director(2, "Christopher Nolan"));
        Director director3 = directorStorage.addDirector(new Director(3, "Quentin Tarantino"));

        assertEquals(3, directorStorage.getDirectors().size());
        assertEquals(director, directorStorage.getDirectors().get(0));
        assertEquals(director2, directorStorage.getDirectors().get(1));
        assertEquals(director3, directorStorage.getDirectors().get(2));
    }

    @Test
    void getDirectorById() {
        Director director = directorStorage.addDirector(new Director(1, "Steven Spielberg"));
        Director director2 = directorStorage.addDirector(new Director(2, "Christopher Nolan"));
        Director director3 = directorStorage.addDirector(new Director(3, "Quentin Tarantino"));

        assertEquals(director, directorStorage.getDirectorById(director.getId()));
        assertEquals(director2, directorStorage.getDirectorById(director2.getId()));
        assertEquals(director3, directorStorage.getDirectorById(director3.getId()));
    }

    @Test
    void getDirectorWithWrongId() {
        assertThrows(NotFoundException.class, () -> directorStorage.getDirectorById(10));
    }
}