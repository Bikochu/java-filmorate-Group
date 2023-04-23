package ru.yandex.practicum.filmorate.integration.event;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventDbTest {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

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
    void getEventById() {
        Film film = createFilm("Cool Film");
        User user = createUser("User");
        Review review = Review.builder().content("Review")
                .filmId(film.getId()).userId(user.getId()).positive(true).build();
        List<Event> list = eventStorage.getAllEvents();
        assertTrue(list.size() > 0);
        Event event = list.get(0);
        Event get = eventStorage.getEventById(event.getEventId());
        assertEquals(event, get);
    }

    @Test
    void getAllEvents() {
        Film film = createFilm("Cool Film");
        User user = createUser("User");
        List<Event> list = eventStorage.getAllEvents();
        Event event = eventStorage.createEvent("LIKE", "ADD", user.getId(), film.getId());
        List<Event> get = eventStorage.getAllEvents();
        assertTrue(get.size() > list.size());
        get.removeAll(list);
        assertEquals(1, get.size());
        assertEquals("LIKE", get.get(0).getEventType());
    }

    @Test
    void getAllEventsByUser() {
        Film film = createFilm("Cool Film");
        User user = createUser("User");
        List<Event> list = eventStorage.getAllEventsByUser(user.getId());
        Event event = eventStorage.createEvent("LIKE", "ADD", user.getId(), film.getId());
        List<Event> get = eventStorage.getAllEventsByUser(user.getId());
        assertTrue(get.size() > list.size());
        get.removeAll(list);
        assertEquals(1, get.size());
        assertEquals("LIKE", get.get(0).getEventType());
    }

    @Test
    void createEvent() {
        Film film = createFilm("Cool Film");
        User user = createUser("User");
        Event event = eventStorage.createEvent("LIKE", "ADD", user.getId(), film.getId());
        Event get = eventStorage.getEventById(event.getEventId());
        assertEquals(event, get);
    }


}
