package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director addDirector(Director director);
    Director updateDirector(Director director);
    void deleteDirector(int id);
    List<Director> getDirectors();
    Director getDirectorById(int id);
}
