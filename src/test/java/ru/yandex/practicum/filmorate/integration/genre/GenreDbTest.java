package ru.yandex.practicum.filmorate.integration.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreDbTest {
    private final GenreDbStorage genreStorage;

    @Test
    void getGenreById() {
        assertTrue(genreStorage.getGenreById(1).getName().equals("Комедия"));
    }

    @Test
    void getAllGenres() {
        List<Genre> genres = genreStorage.getAllGenres();
        assertTrue(genres.size() == 6);
        assertTrue(genres.get(0).getName().equals("Комедия"));
        assertTrue(genres.get(5).getName().equals("Боевик"));
    }
}