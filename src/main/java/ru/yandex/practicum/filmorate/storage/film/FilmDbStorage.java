package ru.yandex.practicum.filmorate.storage.film;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
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
        film.getGenres().forEach(g -> addGenresToFilm(film.getId(), g.getId()));
        setDirectors(film.getDirectors(), film.getId());
        film.getDirectors().clear();
        film.getDirectors().addAll(getDirectorsByFilmId(film.getId()));
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
        Set<Genre> genres = new LinkedHashSet<>(film.getGenres());
        genres.forEach(g -> addGenresToFilm(film.getId(), g.getId()));
        film.getGenres().clear();
        film.getGenres().addAll(genres);

        deleteRatingToFilm(film.getId());
        addRatingToFilm(film.getId(), film.getMpa().getId());

        deleteDirectorsByFilmId(film.getId());
        setDirectors(film.getDirectors(), film.getId());
        film.getDirectors().clear();
        film.getDirectors().addAll(getDirectorsByFilmId(film.getId()));
        return film;
    }

    @Override
    public void deleteFilmById(long id) {
        deleteGenresToFilm(id);
        deleteRatingToFilm(id);
        deleteLikeByFilmId(id);
        String sqlQuery = "delete from film where film_id = ?";
        int upd = jdbcTemplate.update(sqlQuery, id);
        if (upd == 0) {
            throw new NotFoundException("Фильм с Id " + id + " не найден");
        }
    }

    @Override
    public Film findFilmById(long id) {
        try {
            String sqlQuery = "SELECT film_id, film_name, description, release_date, duration, rate FROM FILM " +
                    "WHERE film_id = ?";
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            addGenresToFilm(Collections.singletonList(film));
            addRatingToFilm(Collections.singletonList(film));
            film.getDirectors().addAll(getDirectorsByFilmId(film.getId()));
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с Id " + id + " не найден");
        }
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT film_id, film_name, description, release_date, duration, rate FROM film";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        addGenresToFilm(films);
        addRatingToFilm(films);
        addDirectorsToFilm(films);
        return films;
    }

    @Override
    public void addLike(long id, long userId) {
        String checkQuery = "SELECT COUNT(film_id) FROM likes WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkQuery, Integer.class, id, userId);
        if (count != null && count > 0) {
            return;
        }
        String sqlQuery = "INSERT INTO Likes(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
        String sqlRate = "UPDATE Film SET rate=rate+1 WHERE film_id=?";
        jdbcTemplate.update(sqlRate, id);
    }

    @Override
    public void deleteLike(long id, long userId) {
        String sqlQuery = "DELETE FROM Likes WHERE film_id = ? AND user_id = ?";
        int del = jdbcTemplate.update(sqlQuery, id, userId);
        if (del <= 0) {
            return;
        }
        String sqlRate = "UPDATE Film SET rate=rate-1 WHERE film_id=?";
        jdbcTemplate.update(sqlRate, id);
    }

    @Override
    public List<Film> getTopFilms(Integer limit, Integer genreId, Integer year) {
        List<Film> films;
        String sqlQuery = "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, f.rate, " +
                "COUNT(l.user_id) AS COUNT " +
                "FROM FILM f " +
                "LEFT JOIN FILM_GENRE fg on f.film_id = fg.film_id " +
                "LEFT JOIN LIKES l on f.film_id = l.film_id {} GROUP BY f.film_id ORDER BY COUNT DESC LIMIT ?";
        if (genreId == null && year == null) {
            films = jdbcTemplate.query(sqlQuery.replace("{}", ""), this::mapRowToFilm, limit);
        } else if (genreId == null) {
            films = jdbcTemplate.query(sqlQuery.replace(
                    "{}", "WHERE EXTRACT(YEAR FROM release_date) = ?"), this::mapRowToFilm, year, limit);
        } else if (year == null) {
            films = jdbcTemplate.query(sqlQuery.replace(
                    "{}", "WHERE genre_id = ?"), this::mapRowToFilm, genreId, limit);
        } else {
            films = jdbcTemplate.query(sqlQuery.replace(
                    "{}", "WHERE genre_id = ? " +
                            "AND EXTRACT(YEAR FROM release_date) = ? "), this::mapRowToFilm, genreId, year, limit);
        }
        addGenresToFilm(films);
        addRatingToFilm(films);
        addDirectorsToFilm(films);
        return films;
    }

    @Override
    public List<Film> getRecommendations(long userId) {
        String sqlQuery = "SELECT f.* FROM film f " +
                "JOIN (SELECT DISTINCT l2.film_id, COUNT(*) relevation " +
                "FROM likes l1 " +
                "LEFT JOIN likes l2 ON l1.user_id = l2.user_id " +
                "WHERE l1.film_id IN (SELECT film_id FROM likes WHERE user_id = ?) " +
                "AND l2.film_id NOT IN (SELECT film_id FROM likes WHERE user_id = ?) " +
                "GROUP BY l1.user_id, l2.film_id " +
                "ORDER BY relevation DESC) AS r " +
                "ON r.film_id = f.film_id";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, userId, userId);
        addGenresToFilm(films);
        addRatingToFilm(films);
        addDirectorsToFilm(films);
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
        String sqlQuery = "DELETE FROM likes WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private void addGenresToFilm(long filmId, int genreId) {
        String sqlQuery = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery,
                filmId,
                genreId);
    }

    private void deleteGenresToFilm(long filmId) {
        String sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private void addRatingToFilm(long filmId, int ratingId) {
        String sqlQuery = "INSERT INTO film_rating(film_id, rating_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, ratingId);
    }

    private void deleteRatingToFilm(long filmId) {
        String sqlQuery = "DELETE FROM film_rating WHERE film_id = ?";
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
        films.forEach(f -> f.getGenres().addAll(filmToGenresMap.getOrDefault(f.getId(),
                Collections.emptyList())));
    }

    private Map<Long, List<Genre>> getGenresToFilms(List<Long> filmIds) {
        String sqlQuery = "SELECT fg.film_id, g.genre_id, g.genre_name FROM film_genre AS fg JOIN genre AS g ON" +
                " fg.genre_id = g.genre_id WHERE fg.film_id IN (:ids)";
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

    private void addRatingToFilm(List<Film> films) {
        List<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        Map<Long, Mpa> filmToRatingMap = getRatingToFilms(filmIds);
        films.forEach(f -> f.setMpa(filmToRatingMap.getOrDefault(f.getId(), null)));
    }

    private List<Director> getDirectorsByFilmId(long id){
        String sql = "SELECT * FROM DIRECTOR D LEFT JOIN FILM_DIRECTOR FD on D.DIRECTOR_ID = FD.DIRECTOR_ID " +
                "WHERE FILM_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Director d = new Director();
            d.setId(rs.getInt("DIRECTOR_ID"));
            d.setName(rs.getString("NAME"));
            return d;
        }, id);
    }

    private void setDirectors(List<Director> directors, long filmId){
        String sql = "MERGE INTO FILM_DIRECTOR (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Director d = directors.get(i);
                ps.setLong(1, filmId);
                ps.setInt(2, d.getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });
    }

    private void addDirectorsToFilm(List<Film> films){
        for (Film film : films){
            film.getDirectors().addAll(getDirectorsByFilmId(film.getId()));
        }
    }

    private void deleteDirectorsByFilmId(long id){
        jdbcTemplate.update("DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?", id);
    }



    private Map<Long, Mpa> getRatingToFilms(List<Long> filmIds) {
        String sqlQuery = "SELECT rf.film_id, r.rating_id, r.rating_name FROM film_rating AS rf JOIN RATING r ON" +
                " r.RATING_ID = rf.RATING_ID WHERE rf.film_id IN (:ids)";
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
