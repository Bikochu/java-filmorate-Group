package ru.yandex.practicum.filmorate.integration.review;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbTest {
    @Autowired
    private final ReviewStorage reviewStorage;
    @Autowired
    private final UserStorage userStorage;
    @Autowired
    private final FilmStorage filmStorage;

    private Film createFilm(String name) {
        Film film = Film.builder()
                .duration(100)
                .description("description")
                .releaseDate(LocalDate.of(2022, 05, 11))
                .name(name)
                .mpa(Mpa.builder().id(1).build())
                .build();
        filmStorage.addFilm(film);
        return film;
    }

    private User createUser(String name) {
        User user = User.builder().name(name).email("mail@yandex.com").login(name)
                .birthday(LocalDate.of(2000, 06, 12))
                .build();
        userStorage.addUser(user);
        return user;
    }


    @Test
    void addReview() {
        Film film = createFilm("Cool Film");
        User user = createUser("User");
        Review review = Review.builder().content("Review")
                .filmId(film.getId()).userId(user.getId()).positive(true).build();
        reviewStorage.addReview(review);
        List<Review> list = reviewStorage.getListReview(100);
        assertTrue(list.contains(review));
    }

    @Test
    void updateReview() {
        Film film = createFilm("Cool Film");
        User user = createUser("User");
        Review review = Review.builder().content("Review")
                .filmId(film.getId()).userId(user.getId()).positive(true).build();
        reviewStorage.addReview(review);

        String newContent = "New Content";
        review.setContent(newContent);
        reviewStorage.updateReview(review);
        review = reviewStorage.getById(review.getReviewId());
        assertEquals(newContent, review.getContent());
    }

    @Test
    void deleteReview() {
        Film film = createFilm("Cool Film");
        User user = createUser("User");
        Review review = Review.builder().content("Review")
                .filmId(film.getId()).userId(user.getId()).positive(true).build();
        reviewStorage.addReview(review);
        List<Review> list = reviewStorage.getListReview(100);
        assertTrue(list.contains(review));
        reviewStorage.deleteReview(review.getReviewId());
        list = reviewStorage.getListReview(100);
        assertFalse(list.contains(review));
    }

    @Test
    void getById() {
        Film film = createFilm("Cool Film");
        User user = createUser("User");
        Review review = Review.builder().content("Review")
                .filmId(film.getId()).userId(user.getId()).positive(true).build();
        reviewStorage.addReview(review);
        Review get = reviewStorage.getById(review.getReviewId());
        assertEquals(review, get);
        reviewStorage.deleteReview(review.getReviewId());
        assertThrows(NotFoundException.class, () -> reviewStorage.getById(review.getReviewId()));
    }

    @Test
    void getListReview() {
        List<Review> list = reviewStorage.getListReview(100);

        Film film = createFilm("Cool Film");
        User user = createUser("User");
        Review review = Review.builder().content("Review")
                .filmId(film.getId()).userId(user.getId()).positive(true).build();
        reviewStorage.addReview(review);

        List<Review> get = reviewStorage.getListReview(100);
        assertTrue(get.size() > list.size());
        assertFalse(list.contains(review));
        assertTrue(get.contains(review));
    }

    @Test
    void getListReviewByFilmId() {
        Film film = createFilm("Cool Film");
        User user = createUser("User");
        List<Review> list = reviewStorage.getListReviewByFilmId(film.getId(), 100);

        Review review = Review.builder().content("Review")
                .filmId(film.getId()).userId(user.getId()).positive(true).build();
        reviewStorage.addReview(review);

        List<Review> get = reviewStorage.getListReviewByFilmId(film.getId(), 100);
        assertTrue(get.size() > list.size());
        assertFalse(list.contains(review));
        assertTrue(get.contains(review));
    }

    @Test
    void opinion() {
        Film film = createFilm("Cool Film");
        User user1 = createUser("User1");
        User user2 = createUser("User2");
        Review review = Review.builder().content("Review")
                .filmId(film.getId()).userId(user1.getId()).positive(true).build();
        reviewStorage.addReview(review);
        long useful = review.getUseful();
        review = reviewStorage.putOpinion(review, user1.getId(), true);
        assertEquals(useful + 1, review.getUseful());
        review = reviewStorage.putOpinion(review, user2.getId(), false);
        assertEquals(useful, review.getUseful());
        review = reviewStorage.delOpinion(review, user1.getId(), true);
        assertEquals(useful - 1, review.getUseful());
        review = reviewStorage.delOpinion(review, user2.getId(), false);
        assertEquals(useful, review.getUseful());
    }


}
