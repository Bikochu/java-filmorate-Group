package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final static Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private static long id = 1;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into film(film_id, film_name, description, release_date, duration) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setLong(1, id);
            stmt.setString(2, film.getName());
            stmt.setString(3, film.getDescription());
            stmt.setDate(4, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(5, film.getDuration());
            return stmt;
        }, keyHolder);
        film.setId(id);
        id = keyHolder.getKey().longValue() + 1;
        addRatingToFilm(film.getId(), film.getMpa().getId());
        film.getGenres().stream().forEach(g -> addGenresToFilm(film.getId(), g.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update film set film_name = ?, description = ?," +
                " release_date = ?, duration = ? where film_id = ?";
        int upd = jdbcTemplate.update(sqlQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getId());
        if (upd == 0) {
            throw new NotFoundException("Фильм с Id " + film.getId() + " не найден");
        }
        deleteGenresToFilm(film.getId());
        deleteRatingToFilm(film.getId());
        Set<Genre> genres = new LinkedHashSet<>(film.getGenres());
        genres.stream().forEach(g -> addGenresToFilm(film.getId(), g.getId()));
        addRatingToFilm(film.getId(), film.getMpa().getId());
        film.getGenres().clear();
        film.getGenres().addAll(genres);
        return film;
    }

    @Override
    public void deleteFilmById(long id) {
        deleteGenresToFilm(id);
        deleteRatingToFilm(id);
        String sqlQuery = "delete from film where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film findFilmById(long id) {
        try {
            String sqlQuery = "select film_id, film_name, description, release_date, duration " +
                    "from film where film_id = ?";
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            film.getGenres().addAll(getGenreToFilm(id));
            film.setMpa(getRatingToFilm(film.getId()));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с Id " + id + " не найден");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "select film_id, film_name, description, release_date, duration from film";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        films.stream().forEach(film -> {
            film.getGenres().addAll(getGenreToFilm(film.getId()));
            film.setMpa(getRatingToFilm(film.getId()));
        });
        return films;
    }

    @Override
    public List<Film> getFilmsByCount(int count) {
        String sqlQuery = "select film_id, film_name, description, release_date, duration from film limit ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        films.stream().forEach(film -> {
            film.getGenres().addAll(getGenreToFilm(film.getId()));
            film.setMpa(getRatingToFilm(film.getId()));
        });
        return films;
    }

    @Override
    public void addLike(long id, long userId) {
        String checkQuery = "select count(film_id) from likes where film_id = ? and user_id = ?";
        int check = jdbcTemplate.queryForObject(checkQuery, new Object[]{id, userId}, Integer.class);
        if (check > 0) {
            return;
        }
        String sqlQuery = "insert into likes(film_id, user_id) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                id,
                userId);
    }

    @Override
    public void deleteLike(long id, long userId) {
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        String sqlQuery = "select film_id, film_name, description, release_date, duration from FILM where FILM_ID in " +
                "(select film_id from LIKES group by FILM_ID order by count(USER_ID) desc) limit ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        films.stream().forEach(film -> {
            film.getGenres().addAll(getGenreToFilm(film.getId()));
            film.setMpa(getRatingToFilm(film.getId()));
        });
        return films;
    }

    @Override
    public Genre getGenre(int genreId) {
        String sqlQuery = "select genre_id, genre_name from genre where genre_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreId);
    }

    @Override
    public Mpa getMpaById(int mpaId) {
        try {
            String sqlQuery = "select rating_id, rating_name from rating where rating_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинг с Id " + mpaId + " не найден");
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "select rating_id, rating_name from rating";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    @Override
    public Genre getGenreById(int genreId) {
        try {
            String sqlQuery = "select genre_id, genre_name from genre where genre_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с Id " + genreId + " не найден");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "select genre_id, genre_name from genre";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre updateGenre(Genre genre) {
        String sqlQuery = "update genre set genre_name = ? where genre_id = ?";
        int upd = jdbcTemplate.update(sqlQuery
                , genre.getName()
                , genre.getId());
        if (upd == 0) {
            throw new NotFoundException("Фильм с Id " + genre.getId() + " не найден");
        }
        return genre;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("film_name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();
        return film;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("genre_name"))
                .build();
        return genre;
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        Mpa mpa = Mpa.builder()
                .id(resultSet.getInt("rating_id"))
                .name(resultSet.getString("rating_name"))
                .build();
        return mpa;
    }

    private void addGenresToFilm(long filmId, int genreId) {
        String sqlQuery = "insert into film_genre(film_id, genre_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                filmId,
                genreId);
    }

    private List<Genre> getGenreToFilm(long filmId) {
        String sqlQuery = "select g.genre_id, g.genre_name from film_genre as fg join genre as g on" +
                " fg.genre_id = g.genre_id where fg.film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    private void deleteGenresToFilm(long filmId) {
        String sqlQuery = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private void addRatingToFilm(long filmId, int ratingId) {
        String sqlQuery = "insert into film_rating(film_id, rating_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                filmId,
                ratingId);
    }

    private Mpa getRatingToFilm(long filmId) {
        String sqlQuery = "select r.rating_id, r.rating_name from film_rating as rf join RATING r on" +
                " r.RATING_ID = rf.RATING_ID where rf.film_id = ?";
        List<Mpa> mpas = jdbcTemplate.query(sqlQuery, this::mapRowToMpa, filmId);
        return mpas.get(0);
    }

    private void deleteRatingToFilm(long filmId) {
        String sqlQuery = "delete from film_rating where film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }
}
