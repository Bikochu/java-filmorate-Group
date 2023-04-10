package ru.yandex.practicum.filmorate.integration.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbTest {
    private final MpaDbStorage mpaStorage;

    @Test
    void getMpaById() {
        assertTrue(mpaStorage.getMpaById(1).getName().equals("G"));
    }

    @Test
    void getAllMpa() {
        List<Mpa> mpas = mpaStorage.getAllMpa();
        assertTrue(mpas.size() == 5);
        assertTrue(mpas.get(0).getName().equals("G"));
        assertTrue(mpas.get(4).getName().equals("NC-17"));
    }
}