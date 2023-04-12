package ru.yandex.practicum.filmorate.storage.film;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into film(film_name, description, release_date, duration, rate) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        addRatingToFilm(film.getId(), film.getMpa().getId());
        film.getGenres().stream().forEach(g -> addGenresToFilm(film.getId(), g.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE FILM set film_name = ?, description = ?," +
                " release_date = ?, duration = ?, rate = ? WHERE film_id = ?";
        int upd = jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getId());
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
        deleteLikeByFilmId(id);
        String sqlQuery = "delete from film where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film findFilmById(long id) {
        try {
            String sqlQuery = "SELECT film_id, film_name, description, release_date, duration, rate FROM FILM where film_id = ?";
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            addGenresToFilm(Collections.singletonList(film));
            addRatingToFilm(Collections.singletonList(film));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с Id " + id + " не найден");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "select film_id, film_name, description, release_date, duration, rate from film";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        addGenresToFilm(films);
        addRatingToFilm(films);
        return films;
    }

    @Override
    public List<Film> getFilmsByCount(int count) {
        String sqlQuery = "SELECT film_id, film_name, description, release_date, duration, rate " +
                "FROM film " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        addGenresToFilm(films);
        addRatingToFilm(films);
        return films;
    }

    @Override
    public void addLike(long id, long userId) {
        /*String checkQuery = "select count(film_id) from likes where film_id = ? and user_id = ?";
        int check = jdbcTemplate.queryForObject(checkQuery, new Object[]{id, userId}, Integer.class);
        if (check > 0) {
            return;
        }*/
        String sqlQuery = "INSERT INTO Likes(film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);

        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT rate FROM Film WHERE film_id=?", id);
        rs.next();
        int rate = rs.getInt("rate");
        rate++;
        String sqlRate = "UPDATE Film SET rate=? WHERE film_id=?";
        jdbcTemplate.update(sqlRate, rate, id);
    }

    @Override
    public void deleteLike(long id, long userId) {
        String sqlQuery = "DELETE FROM Likes WHERE film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, id, userId);

        SqlRowSet rs = jdbcTemplate.queryForRowSet("SELECT rate FROM Film WHERE film_id=?", id);
        rs.next();
        int rate = rs.getInt("rate");
        rate--;
        String sqlRate = "UPDATE Film SET rate=? WHERE film_id=?";
        jdbcTemplate.update(sqlRate, rate, id);
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        String sqlQuery = "SELECT film_id, film_name, description, release_date, duration, rate " +
                "FROM FILM " +
                "WHERE FILM_ID in " +
                "(SELECT film_id " +
                "FROM LIKES " +
                "GROUP BY FILM_ID " +
                "ORDER BY count(USER_ID) DESC) " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        addGenresToFilm(films);
        addRatingToFilm(films);
        return films;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("film_name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .rate(resultSet.getInt("rate"))
                .build();
    }

    private void deleteLikeByFilmId(long id) {
        String sqlQuery = "delete from likes where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private void addGenresToFilm(long filmId, int genreId) {
        String sqlQuery = "insert into film_genre(film_id, genre_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                filmId,
                genreId);
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

    private void deleteRatingToFilm(long filmId) {
        String sqlQuery = "delete from film_rating where film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private FilmToGenres mapRowFilmToGenres(ResultSet resultSet, int rowNum) throws SQLException {
        return FilmToGenres.builder()
                .filmId(resultSet.getInt("film_id"))
                .genreId(resultSet.getInt("genre_id"))
                .genreName(resultSet.getString("genre_name"))
                .build();
    }

    private void addGenresToFilm(List<Film> films) {
        List<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        Map<Long, List<Genre>> filmToGenresMap = getGenresToFilms(filmIds);
        films.stream().forEach(f -> f.getGenres().addAll(filmToGenresMap.getOrDefault(f.getId(),
                Collections.emptyList())));
    }

    private Map<Long, List<Genre>> getGenresToFilms(List<Long> filmIds) {
        String sqlQuery = "select fg.film_id, g.genre_id, g.genre_name from film_genre as fg join genre as g on" +
                " fg.genre_id = g.genre_id where fg.film_id in (:ids)";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", filmIds);
        List<FilmToGenres> filmToGenresList = namedParameterJdbcTemplate.query(sqlQuery, parameters,
                this::mapRowFilmToGenres);
        Map<Long, List<Genre>> filmToGenresMap = new HashMap<>();
        for (FilmToGenres fg : filmToGenresList) {
            Genre genre = Genre.builder()
                    .id(fg.genreId)
                    .name(fg.genreName)
                    .build();
            List<Genre> genres;
            if (filmToGenresMap.containsKey(fg.filmId)) {
                genres = filmToGenresMap.get(fg.filmId);
            } else {
                genres = new ArrayList<>();
            }
            genres.add(genre);
            filmToGenresMap.put(fg.filmId, genres);
        }
        return filmToGenresMap;
    }

    public void addRatingToFilm(List<Film> films) {
        List<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        Map<Long, Mpa> filmToRatingMap = getRatingToFilms(filmIds);
        films.stream().forEach(f -> f.setMpa(filmToRatingMap.getOrDefault(f.getId(), null)));
    }

    private Map<Long, Mpa> getRatingToFilms(List<Long> filmIds) {
        String sqlQuery = "select rf.film_id, r.rating_id, r.rating_name from film_rating as rf join RATING r on" +
                " r.RATING_ID = rf.RATING_ID where rf.film_id in (:ids)";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", filmIds);
        List<FilmToRating> filmToRatingList = namedParameterJdbcTemplate.query(sqlQuery, parameters,
                this::mapRowFilmToRating);
        return filmToRatingList.stream()
                .collect(Collectors.toMap(r -> r.filmId,
                        r -> Mpa.builder()
                                .id(r.ratingId)
                                .name(r.ratingName)
                                .build(),
                        (a, b) -> a));
    }

    private FilmToRating mapRowFilmToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return FilmToRating.builder()
                .filmId(resultSet.getInt("film_id"))
                .ratingId(resultSet.getInt("rating_id"))
                .ratingName(resultSet.getString("rating_name"))
                .build();
    }

    @Data
    @Builder
    private static class FilmToGenres {
        private long filmId;
        private int genreId;
        private String genreName;
    }

    @Data
    @Builder
    private static class FilmToRating {
        private long filmId;
        private int ratingId;
        private String ratingName;
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        String sql = "SELECT FILM_ID " +
                "FROM LIKES " +
                "WHERE USER_ID = ? " +
                    "INTERSECT SELECT FILM_ID " +
                    "FROM LIKES " +
                    "WHERE USER_ID = ?";
        List<Integer> listOfCommonFilms = jdbcTemplate.queryForList(sql, new Object[]{userId, friendId}, Integer.class);
        List<Film> listOfFilms = new ArrayList<>();
        listOfCommonFilms.forEach(film -> listOfFilms.add(findFilmById(film)));
        return listOfFilms.stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .collect(Collectors.toList());
    }


}
