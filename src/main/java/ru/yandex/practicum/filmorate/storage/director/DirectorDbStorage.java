package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Director addDirector(Director director) {
        String sqlQuery = "INSERT INTO DIRECTOR (NAME) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"DIRECTOR_ID"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE DIRECTOR SET NAME = ? WHERE DIRECTOR_ID = ?";
        int status = jdbcTemplate.update(sql, director.getName(), director.getId());
        if (status != 1) {
            throw new NotFoundException("WRONG DIRECTOR ID");
        }
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        String sql = "DELETE FROM DIRECTOR WHERE DIRECTOR_ID = ?";
        int status = jdbcTemplate.update(sql, id);
        if (status != 1) {
            throw new NotFoundException("WRONG DIRECTOR ID");
        }
    }

    @Override
    public List<Director> getDirectors() {
        String sqlQuery = "SELECT * FROM DIRECTOR";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Director getDirectorById(int id) {
        Director director;
        try {
            String sqlQuery = "SELECT * FROM DIRECTOR WHERE DIRECTOR_ID = ?";
            director = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("WRONG ID");
        }
        return director;
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("DIRECTOR_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}
