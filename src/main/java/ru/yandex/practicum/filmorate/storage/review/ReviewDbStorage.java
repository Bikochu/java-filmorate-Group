package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component("reviewDbStorage")
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review addReview(Review review) {
        String sql = "INSERT INTO REVIEWS (USER_ID,FILM_ID,CONTENT,POSITIVE,USEFUL) " +
                "VALUES (?,?,?,?,0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
            stmt.setLong(1, review.getUserId());
            stmt.setLong(2, review.getFilmId());
            stmt.setString(3, review.getContent());
            stmt.setBoolean(4, review.getPositive());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE REVIEWS SET CONTENT=?,POSITIVE=? " +
                "WHERE REVIEW_ID=?";
        int upd = jdbcTemplate.update(sql,
                review.getContent(),
                review.getPositive(),
                review.getReviewId()
        );
        if (upd == 0) {
            throw new NotFoundException("Отзыв с Id " + review.getReviewId() + " не найден");
        }
        return getById(review.getReviewId());
    }

    @Override
    public void deleteReview(long reviewId) {
        String sql = "DELETE FROM REVIEWS WHERE REVIEW_ID=?";
        int upd = jdbcTemplate.update(sql, reviewId);
        if (upd == 0) {
            throw new NotFoundException("Отзыв с Id " + reviewId + " не найден");
        }
    }

    @Override
    public Review getById(long reviewId) {
        String sql = "SELECT REVIEW_ID,USER_ID,FILM_ID,CONTENT,POSITIVE,USEFUL FROM REVIEWS " +
                "WHERE REVIEW_ID=?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToReview, reviewId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Отзыв с Id " + reviewId + " не найден");
        }
    }

    @Override
    public List<Review> getListReview(long limit) {
        String sql = "SELECT REVIEW_ID,USER_ID,FILM_ID,CONTENT,POSITIVE,USEFUL FROM REVIEWS " +
                "ORDER BY USEFUL DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, limit);
    }

    @Override
    public List<Review> getListReviewByFilmId(long filmId, long limit) {
        String sql = "SELECT REVIEW_ID,USER_ID,FILM_ID,CONTENT,POSITIVE,USEFUL FROM REVIEWS " +
                "WHERE FILM_ID=? " +
                "ORDER BY USEFUL DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, filmId, limit);
    }

    private Boolean checkOpinion(long reviewId, long userId) {
        String sql = "SELECT VOTE FROM REVIEW_LIKES WHERE REVIEW_ID=? AND USER_ID=?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, reviewId, userId);
        if (!rowSet.next()) {
            return null;
        }
        return rowSet.getBoolean("VOTE");
    }

    public Review putOpinion(Review review, long userId, boolean like) {
        String sqlIns = "INSERT INTO REVIEW_LIKES (REVIEW_ID,USER_ID,VOTE) " +
                "VALUES (?,?,?)";
        String sqlUpd = "UPDATE REVIEWS SET USEFUL=USEFUL+? WHERE REVIEW_ID=?";
        Boolean vote = checkOpinion(review.getReviewId(), userId);
        log.info("putOpinion {} {} {} {}", review.getReviewId(), userId, like, vote);
        if (vote == null) {
            jdbcTemplate.update(sqlIns, review.getReviewId(), userId, like);
            jdbcTemplate.update(sqlUpd, (like) ? 1 : -1, review.getReviewId());
        }
        return getById(review.getReviewId());
    }

    public Review delOpinion(Review review, long userId, boolean like) {
        String sqlDel = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID=? AND USER_ID=?";
        String sqlUpd = "UPDATE REVIEWS SET USEFUL=USEFUL-? WHERE REVIEW_ID=?";
        Boolean vote = checkOpinion(review.getReviewId(), userId);
        log.info("delOpinion {} {} {} {}", review.getReviewId(), userId, like, vote);
        if (vote != null) {
            if (like != vote) {
                throw new NotFoundException("Не найден " + ((like) ? "лайк" : "дизлайк") +
                        " отзыва " + review.getReviewId() + " пользователя " + userId);
            }
            int upd = jdbcTemplate.update(sqlDel, review.getReviewId(), userId);
            if (upd > 0) {
                jdbcTemplate.update(sqlUpd, (like) ? 1 : -1, review.getReviewId());
            }
        }
        return getById(review.getReviewId());
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("review_id"))
                .userId(resultSet.getLong("user_id"))
                .filmId(resultSet.getLong("film_id"))
                .content(resultSet.getString("content"))
                .positive(resultSet.getBoolean("positive"))
                .useful(resultSet.getLong("useful"))
                .build();
    }
}
